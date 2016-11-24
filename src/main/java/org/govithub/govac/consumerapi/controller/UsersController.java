package org.govithub.govac.consumerapi.controller;

import java.util.Optional;

import org.govithub.govac.consumerapi.util.GovacException;
import org.govithub.govac.dao.model.User;
import org.govithub.govac.dao.repository.UserRepository;
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
		Optional<User> existingUserOpt = usersRepo.findById(userId);
		if (!existingUserOpt.isPresent()) {
			throw new GovacException("Invalid user.");
		}
		
		User existingUser = existingUserOpt.get();
		
		if (user.username != null && !user.username.isEmpty())
			existingUser.username = user.username;
		if (user.firstName != null && !user.firstName.isEmpty())
			existingUser.firstName = user.firstName;
		if (user.lastName != null && !user.lastName.isEmpty())
			existingUser.lastName = user.lastName;
		if (user.cnp != null && !user.cnp.isEmpty())
			existingUser.cnp = user.cnp;
		if (user.email != null && !user.email.isEmpty())
			existingUser.email = user.email;
		if (user.phone != null && !user.phone.isEmpty())
			existingUser.phone = user.phone;

		existingUser = usersRepo.save(existingUser);
		return existingUser;
	}
}
