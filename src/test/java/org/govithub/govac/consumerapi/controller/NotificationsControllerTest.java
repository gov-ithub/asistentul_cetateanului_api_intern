package org.govithub.govac.consumerapi.controller;


import static io.restassured.RestAssured.when;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;

import org.apache.http.HttpStatus;
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
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import io.restassured.RestAssured;

@ContextConfiguration(locations = {"classpath:test-context.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
@TestPropertySource(locations="classpath:test.properties") 
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class NotificationsControllerTest {
		
	@Autowired
	public UserRepository userRepository;
	
	@Autowired
	public ApplicationRepository applicationRepository;
	
	@Autowired
	public ProviderRepository providerRepository;
	
	@Autowired
	public NotificationRepository notificationRepository;
	
	@Value("${local.server.port}")
    int port;
	
	private User user0;
	private User user1;
	
	private Provider prov1;
	private Provider prov2;
	private Application app1prov1;
	private Application app2prov1;
	private Application app1prov2;
	private Application app2prov2;
	
	private JsonMetadata requirements = new JsonMetadata();
    @Before
    public void setUp() {
    	requirements.set("reqkey", "reqValue");
    	user0 = userRepository.save(new User("test", "user", "test@user.com", "0722123123", "1841322319942"));
    	user1 = userRepository.save(new User("test", "user", "test@user.com", "0722123123", "1841322319942"));
    	prov1 = providerRepository.save(new Provider(user0, "prov1", ""));
		app1prov1 = applicationRepository.save(new Application(prov1, "app1", "", "", requirements));
		app2prov1 = applicationRepository.save(new Application(prov1, "app2", "", "", requirements));
		prov2 = providerRepository.save(new Provider(user0, "prov2", ""));
		app1prov2 = applicationRepository.save(new Application(prov2, "app1", "", "", requirements));
		app2prov2 = applicationRepository.save(new Application(prov2, "app2", "", "", requirements));
		
		JsonMetadata meta1 = new JsonMetadata();
		meta1.set("meta1key", "meta1value");
		JsonMetadata meta2 = new JsonMetadata();
		meta1.set("meta1key", "meta1value");
		
		notificationRepository.save(new Notification(app1prov1, "Notif.user0.1", "desc1", "s.desc1", 1l, user0, meta1));
		notificationRepository.save(new Notification(app1prov2, "Notif.user0.2", "desc2", "s.desc2", 2l, user0, meta2));
		notificationRepository.save(new Notification(app2prov1, "Notif.user0.3", "desc3", "s.desc3", 3l, user0, meta1));
		
		
		notificationRepository.save(new Notification(app2prov2, "Notif.user1.1", "desc4", "s.desc4", 1l, user1, meta2));
		notificationRepository.save(new Notification(app1prov2, "Notif.user1.2", "desc5", "s.desc5", 2l, user1, meta1));
		notificationRepository.save(new Notification(app2prov1, "Notif.user1.3", "desc6", "s.desc6", 3l, user1, meta2));
		notificationRepository.save(new Notification(app1prov1, "Notif.user1.4", "desc7", "s.desc7", 4l, user1, meta1));
		
		RestAssured.port = port;
    }
    
    @After
    public void cleanUp() {
    	notificationRepository.deleteAll();
    	applicationRepository.deleteAll();
    	providerRepository.deleteAll();
    	userRepository.deleteAll();
    }
    
    
    @Test
    public void getNotificationsByUserTest() throws Exception {
    	when().
        get("/user_notifications?user_id={id}", user0.id).
        then().
        	statusCode(HttpStatus.SC_OK).
        	body("title.size()", is(3)).
        	body("title", hasItems("Notif.user0.1", "Notif.user0.2", "Notif.user0.3")).
        	body("description", hasItems("desc1", "desc2", "desc3")).
        	body("shortDescription", hasItems("s.desc1", "s.desc2", "s.desc3")).
        	body("timestamp", hasItems(1, 2, 3));
    }
    
    @Test
    public void getNotificationsByUserAndApplicationTest() throws Exception {
    	when().
        get("/user_notifications?user_id={id}&application=app1", user0.id).
        then().
        	statusCode(HttpStatus.SC_OK).
        	body("title.size()", is(2)).
        	body("title", hasItems("Notif.user0.1", "Notif.user0.2"));
    }
    
    @Test
    public void getNotificationsByUserAndProviderTest() throws Exception {
    	when().
        get("/user_notifications?user_id={id}&provider=prov2", user0.id).
        then().
        	statusCode(HttpStatus.SC_OK).
        	body("title.size()", is(1)).
        	body("title", hasItems("Notif.user0.2"));
    }
    
    @Test
    public void getNotificationsByUserAndProviderAndApplicationTest() throws Exception {
    	when().
        get("/user_notifications?user_id={id}&provider=prov2&application=app2", user0.id).
        then().
        	statusCode(HttpStatus.SC_OK).
        	body("title.size()", is(0));
    	
		when().
        get("/user_notifications?user_id={id}&provider=prov2&application=app1", user0.id).
        then().
        	statusCode(HttpStatus.SC_OK).
        	body("title.size()", is(1)).
        	body("title", hasItems("Notif.user0.2"));
    }
    
    @Test
    public void getNotificationsByUserAndProviderAndApplicationAndTimestampTest() throws Exception {
    	when().
        get("/user_notifications?user_id={id}&provider=prov2&application=app2&startTimestamp=1&endTimestamp=1", user0.id).
        then().
        	statusCode(HttpStatus.SC_OK).
        	body("title.size()", is(0));
    	
		when().
        get("/user_notifications?user_id={id}&provider=prov2&application=app2&startTimestamp=1&endTimestamp=2", user1.id).
        then().
        	statusCode(HttpStatus.SC_OK).
        	body("title.size()", is(1)).
        	body("title", hasItems("Notif.user1.1"));
    }
    
    
    @Test
    public void getNotificationsByUserAndProviderAndApplicationAndTimestampAndKeywordTest() throws Exception {
    	when().
        get("/user_notifications?user_id={id}&provider=prov2&application=app2&startTimestamp=1&endTimestamp=2&keyword=nomatch", user0.id).
        then().
        	statusCode(HttpStatus.SC_OK).
        	body("title.size()", is(0));
    	
		when().
        get("/user_notifications?user_id={id}&provider=prov2&application=app2&startTimestamp=1&endTimestamp=2&keyword=user1.1", user1.id).
        then().
        	statusCode(HttpStatus.SC_OK).
        	body("title.size()", is(1)).
        	body("title", hasItems("Notif.user1.1"));
    }
    
    @Test
    public void getNotificationsByUserAndKeywordTest() throws Exception {
    	when().
        get("/user_notifications?user_id={id}&keyword=nomatch", user0.id).
        then().
        	statusCode(HttpStatus.SC_OK).
        	body("title.size()", is(0));
    	
    	when().
		get("/user_notifications?user_id={id}&keyword=user0.1", user0.id).
        then().
        	statusCode(HttpStatus.SC_OK).
        	body("title.size()", is(1)).
        	body("title", hasItems("Notif.user0.1"));
    	
    	when().
		get("/user_notifications?user_id={id}&keyword=esc1", user0.id).
        then().
        	statusCode(HttpStatus.SC_OK).
        	body("title.size()", is(1)).
        	body("title", hasItems("Notif.user0.1"));
    	
    	when().
		get("/user_notifications?user_id={id}&keyword=s.desc1", user0.id).
        then().
        	statusCode(HttpStatus.SC_OK).
        	body("title.size()", is(1)).
        	body("title", hasItems("Notif.user0.1"));
    	
    	when().
		get("/user_notifications?user_id={id}&keyword=rov1", user0.id).
        then().
        	statusCode(HttpStatus.SC_OK).
        	body("title.size()", is(2)).
        	body("title", hasItems("Notif.user0.1", "Notif.user0.3"));
    	
    	when().
		get("/user_notifications?user_id={id}&keyword=pp1", user0.id).
        then().
        	statusCode(HttpStatus.SC_OK).
        	body("title.size()", is(2)).
        	body("title", hasItems("Notif.user0.1", "Notif.user0.2"));
    }
    
    @Test
    public void getNotificationsByApplicationTest() throws Exception {
    	when().
        get("/app_notifications?application_id={id}", app1prov1.id).
        then().
        	statusCode(HttpStatus.SC_OK).
        	body("title.size()", is(2)).
        	body("title", hasItems("Notif.user0.1", "Notif.user1.4")).
        	body("description", hasItems("desc1", "desc7")).
        	body("shortDescription", hasItems("s.desc1", "s.desc7")).
        	body("timestamp", hasItems(1, 4));
    }
    
    @Test
    public void getNotificationsByApplicationAndProviderTest() throws Exception {
    	when().
        get("/app_notifications?application_id={id}&provider=prov2", app1prov2.id).
        then().
        	statusCode(HttpStatus.SC_OK).
        	body("title.size()", is(2)).
        	body("title", hasItems("Notif.user0.2", "Notif.user1.2"));
    }
    
    @Test
    public void getNotificationsByApplicationAndProviderAndTimestampTest() throws Exception {
    	when().
        get("/app_notifications?provider=prov2&application_id={id}&startTimestamp=2&endTimestamp=2", app1prov2.id).
        then().
        	statusCode(HttpStatus.SC_OK).
        	body("title.size()", is(2)).
    		body("title", hasItems("Notif.user0.2"));
    	
		when().
        get("/app_notifications?provider=prov2&application_id={id}&startTimestamp=3&endTimestamp=3", app1prov2.id).
        then().
        	statusCode(HttpStatus.SC_OK).
        	body("title.size()", is(0));
    }
    
    
    @Test
    public void getNotificationsByApplicationAndProviderAndTimestampAndKeywordTest() throws Exception {
    	when().
        get("/app_notifications?provider=prov1&application_id={id}&startTimestamp=1&endTimestamp=2&keyword=nomatch", app1prov1.id).
        then().
        	statusCode(HttpStatus.SC_OK).
        	body("title.size()", is(0));
    	
		when().
        get("/app_notifications?provider=prov1&application_id={id}&startTimestamp=1&endTimestamp=2&keyword=user0.1", app1prov1.id).
        then().
        	statusCode(HttpStatus.SC_OK).
        	body("title.size()", is(1)).
        	body("title", hasItems("Notif.user0.1"));
    }
    
    @Test
    public void getNotificationsByApplicationAndKeywordTest() throws Exception {
    	when().
        get("/app_notifications?application_id={id}&keyword=nomatch", app1prov1.id).
        then().
        	statusCode(HttpStatus.SC_OK).
        	body("title.size()", is(0));
    	
    	when().
		get("/app_notifications?application_id={id}&keyword=ser0.1", app1prov1.id).
        then().
        	statusCode(HttpStatus.SC_OK).
        	body("title.size()", is(1)).
        	body("title", hasItems("Notif.user0.1"));
    	
    	when().
		get("/app_notifications?application_id={id}&keyword=esc1", app1prov1.id).
        then().
        	statusCode(HttpStatus.SC_OK).
        	body("title.size()", is(1)).
        	body("title", hasItems("Notif.user0.1"));
    	
    	when().
		get("/app_notifications?application_id={id}&keyword=s.desc1", app1prov1.id).
        then().
        	statusCode(HttpStatus.SC_OK).
        	body("title.size()", is(1)).
        	body("title", hasItems("Notif.user0.1"));
    	
    	when().
		get("/app_notifications?application_id={id}&keyword=rov1", app1prov1.id).
        then().
        	statusCode(HttpStatus.SC_OK).
        	body("title.size()", is(2)).
        	body("title", hasItems("Notif.user0.1", "Notif.user1.4"));
    	
    }
}
