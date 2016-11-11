package org.govithub.govac.consumerapi.controller;

import java.util.List;

import org.govithub.govac.consumerapi.dao.NotificationRepository;
import org.govithub.govac.consumerapi.model.Notification;
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
			@RequestParam(value = "application") String application,
			@RequestParam(value = "user_id", defaultValue = "-1") long userId,
			@RequestParam(value = "keyword", defaultValue = "") String keyword,
			@RequestParam(value = "provider", defaultValue = "") String provider,
			@RequestParam(value = "startTimestamp", defaultValue = "0") long startTimestamp,
			@RequestParam(value = "endTimestamp", defaultValue = "0") long endTimestamp) {

		
		if (!application.isEmpty() && provider.isEmpty() && keyword.isEmpty() && 
				startTimestamp == 0 && endTimestamp == 0 && userId < 0)
			return notificationRepo.findByApplication(application);
		
		keyword = keyword.isEmpty() ? "%" : "%" + keyword + "%";
		application = application.isEmpty() ? "%" : "%" + application + "%";
		provider = provider.isEmpty() ? "%" : "%" + provider + "%";
		startTimestamp = startTimestamp == 0 ? Long.MIN_VALUE : startTimestamp;
		endTimestamp = endTimestamp == 0 ? Long.MAX_VALUE : endTimestamp;
		
		return notificationRepo.findByApplicationAndOtherFilters(application, keyword, userId, provider, startTimestamp, endTimestamp);
	}
}
