package org.govithub.govac.consumerapi.controller;

import org.govithub.govac.consumerapi.dao.UserRepository;
import org.govithub.govac.consumerapi.model.User;
import org.govithub.govac.consumerapi.util.GovacException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/users/{id}")
public class UsersController {

	private final UserRepository usersRepo;

	@Autowired
	public UsersController(UserRepository userRepository) {
		this.usersRepo = userRepository;
	}
	
	
	@RequestMapping(method = RequestMethod.POST)
	public User updateUser(@PathVariable(value="id") long userId, @RequestBody User user) throws GovacException {
		User existingUser = usersRepo.findOne(userId);
		if (existingUser == null) {
			throw new GovacException("Invalid user.");
		}
		
		existingUser.username = user.username;
		existingUser.firstName = user.firstName;
		existingUser.lastName = user.lastName;
		existingUser.cnp = user.cnp;
		existingUser.email = user.email;
		existingUser.phone = user.phone;
		
		existingUser = usersRepo.save(existingUser);
		return existingUser;
	}
}
