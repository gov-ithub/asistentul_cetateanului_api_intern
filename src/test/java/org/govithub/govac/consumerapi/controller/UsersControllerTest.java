package org.govithub.govac.consumerapi.controller;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.apache.http.HttpStatus;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.govithub.govac.dao.model.User;
import org.govithub.govac.dao.repository.NotificationRepository;
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
public class UsersControllerTest {

	@Autowired
	public UserRepository userRepository;
	
	@Autowired
	public NotificationRepository notificationsRepository;
	
	private User user;
	
	@Value("${local.server.port}")
    int port;
	
	private ObjectMapper objectMapper = new ObjectMapper();
	
	@Before
	public void setup() {
		RestAssured.port = port;
		cleanUp();
		user = userRepository.save(new User("user1FN", "user1LN", "user1@email.com", "123456", "1231231231122"));
		userRepository.save(new User("user2FN", "user2LN", "user2@email.com", "345678", "2345677654433"));		
	}
	
	@After
	public void cleanUp(){
		notificationsRepository.deleteAll();
		userRepository.deleteAll();
	}
	
	
	@Test
	public void shouldUpdateUser() throws JsonGenerationException, JsonMappingException, IOException {
		given().
		contentType(ContentType.JSON).
			body(objectMapper.writeValueAsString(new User("userFN-updated", "userLN-updated", "updated@email.com", "111222", "111222"))).
		when().
        	post("/users/{id}", user.id).
        then().
        	statusCode(HttpStatus.SC_OK).
        	body("firstName", is("userFN-updated")).
        	body("lastName", is("userLN-updated")).
        	body("email", is("updated@email.com")).
        	body("phone", is("111222")).
        	body("cnp", is("111222"));
        	
		User u = userRepository.findOne(user.id);
		assertEquals("updated@email.com", u.email);
		assertEquals("111222", u.phone);
	}
}
