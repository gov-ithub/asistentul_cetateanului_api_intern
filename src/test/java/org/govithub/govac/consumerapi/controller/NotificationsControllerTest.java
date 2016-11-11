package org.govithub.govac.consumerapi.controller;


import static io.restassured.RestAssured.when;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;

import org.apache.http.HttpStatus;
import org.govithub.govac.consumerapi.dao.NotificationRepository;
import org.govithub.govac.consumerapi.dao.UserRepository;
import org.govithub.govac.consumerapi.model.Notification;
import org.govithub.govac.consumerapi.model.User;
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
	public NotificationRepository notificationRepository;
	
	@Value("${local.server.port}")
    int port;
	
	private User user0;
	private User user1;
	
    @Before
    public void setUp() {
    	user0 = userRepository.save(new User(1, "test.user", "test", "user", "test@user.com", "0722123123", "1841322319942"));
		user1 = userRepository.save(new User(2, "test2.user", "test2", "user", "test2@user.com", "0722123124", "1841322319943"));
		
		notificationRepository.save(new Notification(3, "Notif.user0.1", "desc1", "s.desc1", "prov1", "app1", 1, user0));
		notificationRepository.save(new Notification(4, "Notif.user0.2", "desc2", "s.desc2", "prov2", "app2", 2, user0));
		notificationRepository.save(new Notification(5, "Notif.user0.3", "desc3", "s.desc3", "prov1", "app1", 3, user0));
		
		notificationRepository.save(new Notification(6, "Notif.user1.1", "desc4", "s.desc4", "prov2", "app2", 1, user1));
		notificationRepository.save(new Notification(7, "Notif.user1.2", "desc5", "s.desc5", "prov2", "app1", 2, user1));
		notificationRepository.save(new Notification(8, "Notif.user1.3", "desc6", "s.desc6", "prov1", "app2", 3, user1));
		notificationRepository.save(new Notification(9, "Notif.user1.4", "desc7", "s.desc7", "prov1", "app1", 4, user1));
		
		RestAssured.port = port;
    }
    
    @After
    public void cleanUp() {
    	notificationRepository.deleteAll();
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
        	body("provider", hasItems("prov1")).
        	body("application", hasItems("app1", "app2")).
        	body("timestamp", hasItems(1, 2, 3));
    }
    
    @Test
    public void getNotificationsByUserAndApplicationTest() throws Exception {
    	when().
        get("/user_notifications?user_id={id}&application=app1", user0.id).
        then().
        	statusCode(HttpStatus.SC_OK).
        	body("title.size()", is(2)).
        	body("title", hasItems("Notif.user0.1", "Notif.user0.3"));
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
        get("/user_notifications?user_id={id}&provider=prov2&application=app1", user0.id).
        then().
        	statusCode(HttpStatus.SC_OK).
        	body("title.size()", is(0));
    	
		when().
        get("/user_notifications?user_id={id}&provider=prov2&application=app2", user0.id).
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
        get("/user_notifications?user_id={id}&provider=prov2&application=app2&startTimestamp=1&endTimestamp=2", user0.id).
        then().
        	statusCode(HttpStatus.SC_OK).
        	body("title.size()", is(1)).
        	body("title", hasItems("Notif.user0.2"));
    }
    
    
    @Test
    public void getNotificationsByUserAndProviderAndApplicationAndTimestampAndKeywordTest() throws Exception {
    	when().
        get("/user_notifications?user_id={id}&provider=prov2&application=app2&startTimestamp=1&endTimestamp=2&keyword=nomatch", user0.id).
        then().
        	statusCode(HttpStatus.SC_OK).
        	body("title.size()", is(0));
    	
		when().
        get("/user_notifications?user_id={id}&provider=prov2&application=app2&startTimestamp=1&endTimestamp=2&keyword=user0.2", user0.id).
        then().
        	statusCode(HttpStatus.SC_OK).
        	body("title.size()", is(1)).
        	body("title", hasItems("Notif.user0.2"));
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
        	body("title", hasItems("Notif.user0.1", "Notif.user0.3"));
    }
    
    @Test
    public void getNotificationsByApplicationTest() throws Exception {
    	when().
        get("/app_notifications?application=app1").
        then().
        	statusCode(HttpStatus.SC_OK).
        	body("title.size()", is(4)).
        	body("title", hasItems("Notif.user0.1", "Notif.user0.3", "Notif.user1.2", "Notif.user1.4")).
        	body("description", hasItems("desc1", "desc3", "desc5", "desc7")).
        	body("shortDescription", hasItems("s.desc1", "s.desc3", "s.desc5", "s.desc7")).
        	body("provider", hasItems("prov1", "prov2")).
        	body("application", hasItems("app1")).
        	body("timestamp", hasItems(1, 2, 3, 4));
    }
    
    @Test
    public void getNotificationsByApplicationAndProviderTest() throws Exception {
    	when().
        get("/app_notifications?application=app1&provider=prov2").
        then().
        	statusCode(HttpStatus.SC_OK).
        	body("title.size()", is(1)).
        	body("title", hasItems("Notif.user1.2"));
    }
    
    @Test
    public void getNotificationsByApplicationAndProviderAndTimestampTest() throws Exception {
    	when().
        get("/app_notifications?provider=prov2&application=app2&startTimestamp=1&endTimestamp=1").
        then().
        	statusCode(HttpStatus.SC_OK).
        	body("title.size()", is(1)).
    		body("title", hasItems("Notif.user1.1"));
    	
		when().
        get("/app_notifications?provider=prov2&application=app2&startTimestamp=3&endTimestamp=3").
        then().
        	statusCode(HttpStatus.SC_OK).
        	body("title.size()", is(0));
    }
    
    
    @Test
    public void getNotificationsByApplicationAndProviderAndTimestampAndKeywordTest() throws Exception {
    	when().
        get("/app_notifications?provider=prov1&application=app1&startTimestamp=1&endTimestamp=2&keyword=nomatch").
        then().
        	statusCode(HttpStatus.SC_OK).
        	body("title.size()", is(0));
    	
		when().
        get("/app_notifications?provider=prov1&application=app1&startTimestamp=1&endTimestamp=2&keyword=user0.1").
        then().
        	statusCode(HttpStatus.SC_OK).
        	body("title.size()", is(1)).
        	body("title", hasItems("Notif.user0.1"));
    }
    
    @Test
    public void getNotificationsByApplicationAndKeywordTest() throws Exception {
    	when().
        get("/app_notifications?application=app1&keyword=nomatch").
        then().
        	statusCode(HttpStatus.SC_OK).
        	body("title.size()", is(0));
    	
    	when().
		get("/app_notifications?application=app1&keyword=ser0.1").
        then().
        	statusCode(HttpStatus.SC_OK).
        	body("title.size()", is(1)).
        	body("title", hasItems("Notif.user0.1"));
    	
    	when().
		get("/app_notifications?application=app1&keyword=esc1").
        then().
        	statusCode(HttpStatus.SC_OK).
        	body("title.size()", is(1)).
        	body("title", hasItems("Notif.user0.1"));
    	
    	when().
		get("/app_notifications?application=app1&keyword=s.desc1").
        then().
        	statusCode(HttpStatus.SC_OK).
        	body("title.size()", is(1)).
        	body("title", hasItems("Notif.user0.1"));
    	
    	when().
		get("/app_notifications?application=app1&keyword=rov1").
        then().
        	statusCode(HttpStatus.SC_OK).
        	body("title.size()", is(3)).
        	body("title", hasItems("Notif.user0.1", "Notif.user0.3", "Notif.user1.4"));
    	
    }
}
