package org.govithub.govac.consumerapi.dao;

import org.govithub.govac.dao.model.User;
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
public class UserRepositoryTest {

	@Autowired
    private UserRepository userRepository;

    @Before
    public void init() {
    }

    @Test
    public void testSaveAndFindUser() {
    	User user = new User("testFN", "testLN", "test@email.com", "123456", "1231231232222");
    	user = userRepository.save(user);
    	user = userRepository.findById(user.id).get();
    	Assert.assertNotNull(user);
    }
    
    @Test
    public void testUpdateUserMetaData(){
    	User user = new User("testFN", "testLN", "test@email.com", "123456", "1234567890");
    	user.id = 1l;
		user = userRepository.save(user);
    	user = userRepository.findOne(user.id);
    	Assert.assertNotNull(user);
    	Assert.assertEquals("testFN", user.firstName);
    	Assert.assertEquals("testLN", user.lastName);
    	Assert.assertEquals("test@email.com", user.email);
    	Assert.assertEquals("123456", user.phone);
    	Assert.assertEquals("1234567890", user.cnp);
    	
    	user.firstName = "testFN.updated";
    	user.lastName = "testLN.updated";
    	user.email = "test@email-updated.com";
    	user.phone = "654321";
    	user.cnp = "0987654321";
    	userRepository.save(user);
    	user = userRepository.findOne(user.id);
    	
    	Assert.assertNotNull(user);
    	Assert.assertEquals("testFN.updated", user.firstName);
    	Assert.assertEquals("testLN.updated", user.lastName);
    	Assert.assertEquals("test@email-updated.com", user.email);
    	Assert.assertEquals("654321", user.phone);
    	Assert.assertEquals("0987654321", user.cnp);
    }
    
    @After
    public void cleanUp(){
    	userRepository.deleteAll();
    }
}
