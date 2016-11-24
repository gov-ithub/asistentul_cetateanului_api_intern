package org.govithub.govac.consumerapi.dao;

import java.util.List;

import org.govithub.govac.consumerapi.model.Notification;
import org.govithub.govac.consumerapi.model.User;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration(locations = {"classpath:test-context.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
@TestPropertySource(locations="classpath:test.properties")
public class NotificationRepositoryTest {
	private User user;
	
	@Autowired
	public UserRepository userRepository;
	
	@Autowired
	public NotificationRepository notificationRepository;
	
	@Before
	public void init() {
    	userRepository.save(new User(1, "test.user", "testFN", "testLN", "test@email.com", "123456", "1231231232222"));
    	user = userRepository.findByUsername("test.user");
	}
	
	@Test
	public void insertNotificationTest(){
		Notification notification = new Notification(1l, "Notif.title", "Notif.description",
				"Notif.shortdesc", "Notif.provider", "Notif.application", System.currentTimeMillis(), user);
		notification = notificationRepository.save(notification);
		notification = notificationRepository.findOne(notification.id);
		Assert.assertNotNull(notification);
	}
	
	@Test
	public void findByUserAndOtherFiltersTest() throws Exception{
		notificationRepository.save(new Notification(2, "Notif.user.1", "desc", "s.desc1", "prov1", "app1", 0, user));
		notificationRepository.save(new Notification(3, "Notif.user.2", "desc-keyword", "s.desc2", "prov1", "app2", 1, user));
		notificationRepository.save(new Notification(4, "Notif.user.3", "desc-keyword", "s.desc3", "prov2", "app1", 2, user));
		notificationRepository.save(new Notification(5, "Notif.user.4", "desc", "s.desc4", "prov2", "app2", 3, user));
		
		// filter only by user
		List<Notification> list = notificationRepository.findByUserAndOtherFilters(user.id, "%", "%", "%", Integer.MIN_VALUE, Integer.MAX_VALUE);
		checkNotifications(list, new String[]{"Notif.user.1", "Notif.user.2", "Notif.user.3", "Notif.user.4"}, 4);
		
		// filter by user and timestamp
		list = notificationRepository.findByUserAndOtherFilters(user.id, "%", "%", "%", 0, 1);
		checkNotifications(list, new String[]{"Notif.user.1", "Notif.user.2"}, 2);
		
		// filter by user and application
		list = notificationRepository.findByUserAndOtherFilters(user.id, "%", "app1", "%", Integer.MIN_VALUE, Integer.MAX_VALUE);
		checkNotifications(list, new String[]{"Notif.user.1", "Notif.user.3"}, 2);
		
		// filter by user and provider
		list = notificationRepository.findByUserAndOtherFilters(user.id, "%", "%", "prov1", Integer.MIN_VALUE, Integer.MAX_VALUE);
		checkNotifications(list, new String[]{"Notif.user.1", "Notif.user.2"}, 2);
		
		// filter by user and application and provider
		list = notificationRepository.findByUserAndOtherFilters(user.id, "%", "app1", "prov1", Integer.MIN_VALUE, Integer.MAX_VALUE);
		checkNotifications(list, new String[]{"Notif.user.1", }, 1);
		
		// filter by user and keyword
		list = notificationRepository.findByUserAndOtherFilters(user.id, "desc-keyword", "%", "%", Integer.MIN_VALUE, Integer.MAX_VALUE);
		checkNotifications(list, new String[]{"Notif.user.2", "Notif.user.3"}, 2);		
		
		// filter by user and application and provider and keyword and timestamp
		list = notificationRepository.findByUserAndOtherFilters(user.id, "desc-keyword", "app2", "prov1", Integer.MIN_VALUE, Integer.MAX_VALUE);
		checkNotifications(list, new String[]{"Notif.user.2"}, 1);
		list = notificationRepository.findByUserAndOtherFilters(user.id, "desc", "app2", "prov1", Integer.MIN_VALUE, Integer.MAX_VALUE);
		Assert.assertEquals(0, list.size());
		list = notificationRepository.findByUserAndOtherFilters(user.id, "desc-keyword", "app2", "prov1", 2, 3);
		Assert.assertEquals(0, list.size());
	}
	
	
	@Test
	public void findByApplicationAndOtherFiltersTest() throws Exception{
		notificationRepository.save(new Notification(2, "Notif.user.1", "desc", "s.desc1", "prov1", "app1", 0, user));
		notificationRepository.save(new Notification(3, "Notif.user.2", "desc-keyword", "s.desc2", "prov1", "app2", 1, user));
		notificationRepository.save(new Notification(4, "Notif.user.3", "desc-keyword", "s.desc3", "prov2", "app1", 2, user));
		notificationRepository.save(new Notification(5, "Notif.user.4", "desc", "s.desc4", "prov2", "app2", 3, user));
		
		// filter only by application
		List<Notification> list = notificationRepository.findByApplicationAndOtherFilters("app1", "%", -1, "%", Integer.MIN_VALUE, Integer.MAX_VALUE);
		checkNotifications(list, new String[]{"Notif.user.1", "Notif.user.3"}, 2);
		
		// filter by application and timestamp
		list = notificationRepository.findByApplicationAndOtherFilters("app1", "%", -1, "%", 0, 1);
		checkNotifications(list, new String[]{"Notif.user.1"}, 1);
		
		// filter by application and user
		list = notificationRepository.findByApplicationAndOtherFilters("app2", "%", user.id, "%", Integer.MIN_VALUE, Integer.MAX_VALUE);
		checkNotifications(list, new String[]{"Notif.user.2", "Notif.user.4"}, 2);
		
		// filter by application and provider
		list = notificationRepository.findByApplicationAndOtherFilters("app1", "%", -1, "prov1", Integer.MIN_VALUE, Integer.MAX_VALUE);
		checkNotifications(list, new String[]{"Notif.user.1"}, 1);
		
		// filter by application and user and provider
		list = notificationRepository.findByApplicationAndOtherFilters("app1", "%", user.id, "prov1", Integer.MIN_VALUE, Integer.MAX_VALUE);
		checkNotifications(list, new String[]{"Notif.user.1", }, 1);
		
		// filter by application and keyword
		list = notificationRepository.findByApplicationAndOtherFilters("app1", "desc-keyword", user.id, "%", Integer.MIN_VALUE, Integer.MAX_VALUE);
		checkNotifications(list, new String[]{"Notif.user.3"}, 1);		
		
		// filter by application and user and provider and keyword and timestamp
		list = notificationRepository.findByApplicationAndOtherFilters("app2", "desc-keyword", user.id, "prov1", Integer.MIN_VALUE, Integer.MAX_VALUE);
		checkNotifications(list, new String[]{"Notif.user.2"}, 1);
		list = notificationRepository.findByApplicationAndOtherFilters("app2", "desc", user.id, "prov1", Integer.MIN_VALUE, Integer.MAX_VALUE);
		Assert.assertEquals(0, list.size());
		list = notificationRepository.findByApplicationAndOtherFilters("app2", "desc-keyword", user.id, "prov1", 2, 3);
		Assert.assertEquals(0, list.size());
	}
	
	@After
	public void after() {
		notificationRepository.deleteAll();
		userRepository.deleteAll();
	}
	
	private boolean checkNotifications(List<Notification> notifications, String[] titles, int size) throws Exception{
		Assert.assertEquals(size, notifications.size());
		for (Notification n : notifications) {
			boolean found = false;
			for (String title : titles) {
				if (n.title.equals(title)) {
					found = true;
					break;
				}
			}
			if (!found)
				throw new Exception("Wrong notifications found for user.");
		}
		
		return true;
	}
	
}
