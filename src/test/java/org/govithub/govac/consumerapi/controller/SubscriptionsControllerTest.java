package org.govithub.govac.consumerapi.controller;


import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;

import java.io.IOException;

import org.apache.http.HttpStatus;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.govithub.govac.dao.model.Application;
import org.govithub.govac.dao.model.Provider;
import org.govithub.govac.dao.model.Subscription;
import org.govithub.govac.dao.model.User;
import org.govithub.govac.dao.model.json.JsonMetadata;
import org.govithub.govac.dao.repository.ApplicationRepository;
import org.govithub.govac.dao.repository.ProviderRepository;
import org.govithub.govac.dao.repository.SubscriptionRepository;
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
import io.restassured.http.ContentType;

@ContextConfiguration(locations = {"classpath:test-context.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
@TestPropertySource(locations="classpath:test.properties") 
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SubscriptionsControllerTest {
		
	@Autowired
	public UserRepository userRepository;
	
	@Autowired
	public ApplicationRepository applicationRepository;
	
	@Autowired
	public ProviderRepository providerRepository;
	
	@Autowired
	public SubscriptionRepository subscriptionRepository;
	
	@Value("${local.server.port}")
    int port;
	
	private User user;
	
	private Provider prov;
	private Application app1;
	private Application app2;
	
	private ObjectMapper objectMapper = new ObjectMapper();
	
	private JsonMetadata requirements = new JsonMetadata();
    @Before
    public void setUp() {
    	requirements.set("reqkey", "reqValue");
    	user = userRepository.save(new User("test", "user", "test@user.com", "0722123123", "1841322319942"));
    	prov = providerRepository.save(new Provider(user, "prov1", ""));
		app1 = applicationRepository.save(new Application(prov, "app1", "", "", requirements));
		app2 = applicationRepository.save(new Application(prov, "app2", "", "", requirements));
		
		JsonMetadata meta1 = new JsonMetadata();
		meta1.set("meta1key", "meta1value");
		
		RestAssured.port = port;
		
		objectMapper.disable(org.codehaus.jackson.map.SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS);

    }
    
    @After
    public void cleanUp() {
    	subscriptionRepository.deleteAll();
    	applicationRepository.deleteAll();
    	providerRepository.deleteAll();
    	userRepository.deleteAll();
    }
    
    
    @Test
    public void createSubscriptionTest() throws Exception {
    	JsonMetadata metadata = new JsonMetadata();
    	metadata.set("reqkey", "valueTest");
    	
    	given().
		contentType(ContentType.JSON).
			body(objectMapper.writeValueAsString(new Subscription(user.id, app1.id, System.currentTimeMillis(), metadata))).
		when().
        	post("/subscriptions").
        then().
        	statusCode(HttpStatus.SC_OK).
        	body("metadata.metadataMap.reqkey", is("valueTest")).
        	body("applicationId", is(app1.id.intValue())).
        	body("userId", is(user.id.intValue())).
        	body("id", is(subscriptionRepository.findByUserIdAndApplicationId(user.id, app1.id).get().id.intValue()));
    	
 
    	given().
		contentType(ContentType.JSON).
			body(objectMapper.writeValueAsString(new Subscription(user.id, app1.id, System.currentTimeMillis(), metadata))).
		when().
        	post("/subscriptions").
        then().
        	statusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR); // subscription already exists
    }
    
    @Test
    public void updateSubscription() throws JsonGenerationException, JsonMappingException, IOException {
    	JsonMetadata metadata = new JsonMetadata();
    	metadata.set("reqkey", "valueTest");
    	Subscription subscription = subscriptionRepository.save(new Subscription(user.id, app1.id, System.currentTimeMillis(), metadata));
    	
    	subscription.metadata.set("reqkey", "valueUpdated");
    	
    	given().
		contentType(ContentType.JSON).
			body(objectMapper.writeValueAsString(subscription)).
		when().
        	post("/subscriptions/{id}", subscription.id).
        then().
        	statusCode(HttpStatus.SC_OK).
        	body("metadata.metadataMap.reqkey", is("valueUpdated")).
        	body("applicationId", is(app1.id.intValue())).
        	body("userId", is(user.id.intValue())).
        	body("id", is(subscriptionRepository.findByUserIdAndApplicationId(user.id, app1.id).get().id.intValue()));    
    	
    	
    	subscription.metadata.set("reqkey", "valueUpdated2");
    	subscription.applicationId = app2.id; //should be ignored
    	
    	given().
		contentType(ContentType.JSON).
			body(objectMapper.writeValueAsString(subscription)).
		when().
        	post("/subscriptions/{id}", subscription.id).
        then().
        	statusCode(HttpStatus.SC_OK).
        	body("metadata.metadataMap.reqkey", is("valueUpdated2")).
        	body("applicationId", is(app1.id.intValue())). 
        	body("userId", is(user.id.intValue())).
        	body("id", is(subscriptionRepository.findByUserIdAndApplicationId(user.id, app1.id).get().id.intValue()));    
    }
    
    @Test
    public void getSubscriptionById() {
    	JsonMetadata metadata = new JsonMetadata();
    	metadata.set("reqkey", "valueTest");
    	Subscription subscription = subscriptionRepository.save(new Subscription(user.id, app1.id, System.currentTimeMillis(), metadata));
    	
    	when().
        get("/subscriptions/{id}", subscription.id).
        then().
        	statusCode(HttpStatus.SC_OK).
        	body("id", is(subscription.id.intValue())).
        	body("metadata.metadataMap.reqkey", is("valueTest"));
    }
    
    @Test
    public void getSubscriptionByUserIdAndApplicationId() {
    	User user2 = userRepository.save(new User("test", "user", "test@user.com", "0722123123", "1841322319942"));
    	
    	JsonMetadata metadata = new JsonMetadata();
    	metadata.set("reqkey", "valueTest");
    	Subscription subscription1 = subscriptionRepository.save(new Subscription(user.id, app1.id, System.currentTimeMillis(), metadata));
    	subscriptionRepository.save(new Subscription(user.id, app2.id, System.currentTimeMillis(), metadata));
    	Subscription subscription4 = subscriptionRepository.save(new Subscription(user2.id, app1.id, System.currentTimeMillis(), metadata));
    	
    	when().
        get("/subscriptions?user_id={userId}&application_id={appId}", user.id, app1.id).
        then().
        	statusCode(HttpStatus.SC_OK).
        	body("id.size()", is(1)).
        	body("id", hasItems(subscription1.id.intValue()));
    	
    	when().
        get("/subscriptions?user_id={userId}&application_id={appId}", user2.id, app1.id).
        then().
        	statusCode(HttpStatus.SC_OK).
        	body("id.size()", is(1)).
        	body("id", hasItems(subscription4.id.intValue()));
    	
    	when().
        get("/subscriptions?user_id={userId}&application_id={appId}", user2.id, app2.id).
        then().
        	statusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR); //no subscription found exception
    }
}
