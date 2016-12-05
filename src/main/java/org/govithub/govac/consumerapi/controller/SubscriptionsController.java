package org.govithub.govac.consumerapi.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.govithub.govac.consumerapi.util.GovacException;
import org.govithub.govac.dao.model.Application;
import org.govithub.govac.dao.model.Subscription;
import org.govithub.govac.dao.repository.ApplicationRepository;
import org.govithub.govac.dao.repository.SubscriptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/subscriptions")
public class SubscriptionsController {

	private final SubscriptionRepository subscriptionRepo;
	
	private final ApplicationRepository applicationRepo;
	
	@Autowired
	public SubscriptionsController(SubscriptionRepository subscriptionRepository, ApplicationRepository applicationRepo) {
		this.subscriptionRepo = subscriptionRepository;
		this.applicationRepo = applicationRepo;
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public Subscription getSubscription(@PathVariable(value="id") long subscriptionId) throws GovacException {
		Optional<Subscription> existingSubscriptionOpt = subscriptionRepo.findById(subscriptionId);
		if (!existingSubscriptionOpt.isPresent()) {
			throw new GovacException("No subscriptions found.");
		}
		
		Subscription existingSubscription = existingSubscriptionOpt.get();		
		return existingSubscription;
	}
	
	@RequestMapping(value = "", method = RequestMethod.GET)
	public List<Subscription> getSubscriptionByUserAndApplication(@RequestParam(value="user_id") long userId, @RequestParam(value="application_id", defaultValue="-1") long applicationId) throws GovacException {
		if (applicationId != -1) {
			Optional<Subscription> existingSubscriptionOpt = subscriptionRepo.findByUserIdAndApplicationId(userId, applicationId);
			if (!existingSubscriptionOpt.isPresent()) {
				throw new GovacException("No subscriptions found.");
			}
			Subscription existingSubscription = existingSubscriptionOpt.get();		
			List<Subscription> result = new ArrayList<Subscription>();
			result.add(existingSubscription);
			return result;
		}
		else {
			return subscriptionRepo.findByUserId(userId);
		}
	}
	
	@RequestMapping(value = "/{id}", method = RequestMethod.POST)
	public Subscription updateSubscription(@PathVariable(value="id") long subscriptionId, @RequestBody Subscription subscription) throws GovacException {
		Optional<Subscription> existingSubscriptionOpt = subscriptionRepo.findById(subscriptionId);
		if (!existingSubscriptionOpt.isPresent()) {
			throw new GovacException("Invalid subscription.");
		}
		
		Subscription existingSubscription = existingSubscriptionOpt.get();
		
		if (subscription.metadata != null) {
			checkApplicationRequirements(subscription);
			existingSubscription.metadata = subscription.metadata;
			existingSubscription = subscriptionRepo.save(existingSubscription);
		}
		
		return existingSubscription;
	}
	
	@RequestMapping(value = "", method = RequestMethod.POST)
	public Subscription createSubscription(@RequestBody Subscription subscription) throws GovacException {
		Optional<Subscription> existingSubscriptionOpt = subscriptionRepo.findByUserIdAndApplicationId(subscription.userId, subscription.applicationId);
		if (existingSubscriptionOpt.isPresent()) {
			throw new GovacException("Subscription already exists.");
		}
		
		checkApplicationRequirements(subscription);
		
		subscription = subscriptionRepo.save(subscription);
		return subscription;
	}

	private void checkApplicationRequirements(Subscription subscription) throws GovacException {
		Optional<Application> applicationOpt = applicationRepo.findById(subscription.applicationId);
		if (!applicationOpt.isPresent()) {
			throw new GovacException("Invalid application");
		}
		
		Application application = applicationOpt.get();
		if (application.requirements != null) {
			for (String key: application.requirements.keySet()) {
				if (!subscription.metadata.containsKey(key)) {
					throw new GovacException("Metadata field should contain key: [" + key + "]");
				}
			}
		}
	}
}
