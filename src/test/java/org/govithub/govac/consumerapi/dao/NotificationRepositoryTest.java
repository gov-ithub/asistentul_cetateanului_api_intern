package org.govithub.govac.consumerapi.dao;

import java.util.List;

import org.govithub.govac.dao.model.Application;
import org.govithub.govac.dao.model.Notification;
import org.govithub.govac.dao.model.Provider;
import org.govithub.govac.dao.model.User;
import org.govithub.govac.dao.model.json.JsonMetadata;
import org.govithub.govac.dao.repository.ApplicationRepository;
import org.govithub.govac.dao.repository.NotificationRepository;
import org.govithub.govac.dao.repository.ProviderRepository;
import org.govithub.govac.dao.repository.UserRepository;
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
	
	@Autowired
	public ProviderRepository providerRepository;
	
	@Autowired
	public ApplicationRepository applicationRepository;
	
	
	private JsonMetadata requirements = new JsonMetadata();
	@Before
	public void init() {
		requirements.set("reqKey", "reqValue");
    	user = userRepository.save(new User("testFN", "testLN", "test@email.com", "123456", "1231231232222"));
    	prov1 = providerRepository.save(new Provider(user, "prov1", ""));
		app1prov1 = applicationRepository.save(new Application(prov1, "app1", "", "", requirements));
		prov1 = providerRepository.save(new Provider(user, "prov1", ""));
		app2prov1 = applicationRepository.save(new Application(prov1, "app2", "", "", requirements));
		prov2 = providerRepository.save(new Provider(user, "prov2", ""));
		app1prov2 = applicationRepository.save(new Application(prov2, "app1", "", "", requirements));
		prov2 = providerRepository.save(new Provider(user, "prov2", ""));
		app2prov2 = applicationRepository.save(new Application(prov2, "app2", "", "", requirements));
	}
	
	private Provider prov1;
	private Provider prov2;
	private Application app1prov1;
	private Application app2prov2;
	private Application app2prov1;
	private Application app1prov2;
	private JsonMetadata meta;
	
	@Test
	public void insertNotificationTest(){
		meta = new JsonMetadata();
		meta.set("metaKey", "metaValue");
		Notification notification = notificationRepository.save(new Notification(app1prov1, "Notif.user0.1", "desc1", "s.desc1", 1l, user, meta));
		notification = notificationRepository.save(notification);
		notification = notificationRepository.findOne(notification.id);
		Assert.assertNotNull(notification);
	}
	
	@Test
	public void findByUserAndOtherFiltersTest() throws Exception{
		notificationRepository.save(new Notification(app1prov1, "Notif.user.1", "desc", "s.desc1", 0l, user, meta));
		notificationRepository.save(new Notification(app2prov1, "Notif.user.2", "desc-keyword", "s.desc2", 1l, user, meta));
		notificationRepository.save(new Notification(app1prov2, "Notif.user.3", "desc-keyword", "s.desc3", 2l, user, meta));
		notificationRepository.save(new Notification(app2prov2, "Notif.user.4", "desc", "s.desc3", 3l, user, meta));
		
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
		notificationRepository.save(new Notification(app1prov1, "Notif.user.1", "desc", "s.desc1", 0l, user, meta));
		notificationRepository.save(new Notification(app2prov1, "Notif.user.2", "desc-keyword", "s.desc2", 1l, user, meta));
		notificationRepository.save(new Notification(app1prov2, "Notif.user.3", "desc-keyword", "s.desc3", 2l, user, meta));
		notificationRepository.save(new Notification(app2prov2, "Notif.user.4", "desc", "s.desc3", 3l, user, meta));
		
		// filter only by application
		List<Notification> list = notificationRepository.findByApplicationAndOtherFilters(app1prov2.id, "%", -1l, "%", Long.MIN_VALUE, Long.MAX_VALUE);
		checkNotifications(list, new String[]{"Notif.user.1", "Notif.user.3"}, 1);
		
		// filter by application and timestamp
		list = notificationRepository.findByApplicationAndOtherFilters(app1prov1.id, "%", -1l, "%", 0l, 1l);
		checkNotifications(list, new String[]{"Notif.user.1"}, 1);
		
		// filter by application and user
		list = notificationRepository.findByApplicationAndOtherFilters(app2prov2.id, "%", user.id, "%", Long.MIN_VALUE, Long.MAX_VALUE);
		checkNotifications(list, new String[]{"Notif.user.2", "Notif.user.4"}, 1);
		
		// filter by application and provider
		list = notificationRepository.findByApplicationAndOtherFilters(app1prov1.id, "%", -1l, "prov1", Long.MIN_VALUE, Long.MAX_VALUE);
		checkNotifications(list, new String[]{"Notif.user.1"}, 1);
		
		// filter by application and user and provider
		list = notificationRepository.findByApplicationAndOtherFilters(app1prov1.id, "%", user.id, "prov1", Long.MIN_VALUE, Long.MAX_VALUE);
		checkNotifications(list, new String[]{"Notif.user.1", }, 1);
		
		// filter by application and keyword
		list = notificationRepository.findByApplicationAndOtherFilters(app1prov2.id, "desc-keyword", user.id, "%", Long.MIN_VALUE, Long.MAX_VALUE);
		checkNotifications(list, new String[]{"Notif.user.3"}, 1);		
		
		// filter by application and user and provider and keyword and timestamp
		list = notificationRepository.findByApplicationAndOtherFilters(app2prov1.id, "desc-keyword", user.id, "prov1", Long.MIN_VALUE, Long.MAX_VALUE);
		checkNotifications(list, new String[]{"Notif.user.2"}, 1);
		list = notificationRepository.findByApplicationAndOtherFilters(app1prov2.id, "desc", user.id, "prov1", Long.MIN_VALUE, Long.MAX_VALUE);
		Assert.assertEquals(0, list.size());
		list = notificationRepository.findByApplicationAndOtherFilters(app1prov2.id, "desc-keyword", user.id, "prov1", 2l, 3l);
		Assert.assertEquals(0, list.size());
	}
	
	@After
	public void cleanUp() {
		notificationRepository.deleteAll();
		applicationRepository.deleteAll();
		providerRepository.deleteAll();
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
