package org.govithub.govac.consumerapi.controller;

import java.util.List;

import org.govithub.govac.dao.model.Notification;
import org.govithub.govac.dao.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class NotificationsController {
	
	private final NotificationRepository notificationRepo;
	
	@Autowired
	public NotificationsController(NotificationRepository notificationRepository) {
		this.notificationRepo = notificationRepository;
	}

	@RequestMapping("/user_notifications")
	public List<Notification> getUserNotifications(
			@RequestParam(value = "user_id") long userId,
			@RequestParam(value = "keyword", defaultValue = "") String keyword,
			@RequestParam(value = "provider", defaultValue = "") String provider,
			@RequestParam(value = "application", defaultValue = "") String application,
			@RequestParam(value = "startTimestamp", defaultValue = "0") long startTimestamp,
			@RequestParam(value = "endTimestamp", defaultValue = "0") long endTimestamp) {
		
		keyword = keyword.isEmpty() ? "%" : "%" + keyword + "%";
		application = application.isEmpty() ? "%" : "%" + application + "%";
		provider = provider.isEmpty() ? "%" : "%" + provider + "%";
		startTimestamp = startTimestamp == 0 ? Long.MIN_VALUE : startTimestamp;
		endTimestamp = endTimestamp == 0 ? Long.MAX_VALUE : endTimestamp;
		
		return notificationRepo.findByUserAndOtherFilters(userId, keyword, application, provider, startTimestamp, endTimestamp);
	}
	
	@RequestMapping("/app_notifications")
	public List<Notification> getAppNotifications(
			@RequestParam(value = "application_id") long applicationId,
			@RequestParam(value = "user_id", defaultValue = "-1") long userId,
			@RequestParam(value = "keyword", defaultValue = "") String keyword,
			@RequestParam(value = "provider", defaultValue = "") String provider,
			@RequestParam(value = "startTimestamp", defaultValue = "0") long startTimestamp,
			@RequestParam(value = "endTimestamp", defaultValue = "0") long endTimestamp) {

		
		if (provider.isEmpty() && keyword.isEmpty() && 
				startTimestamp == 0 && endTimestamp == 0 && userId < 0)
			return notificationRepo.findByApplicationId(applicationId);
		
		keyword = keyword.isEmpty() ? "%" : "%" + keyword + "%";
		provider = provider.isEmpty() ? "%" : "%" + provider + "%";
		startTimestamp = startTimestamp == 0 ? Long.MIN_VALUE : startTimestamp;
		endTimestamp = endTimestamp == 0 ? Long.MAX_VALUE : endTimestamp;
		
		return notificationRepo.findByApplicationAndOtherFilters(applicationId, keyword, userId, provider, startTimestamp, endTimestamp);
	}
}
