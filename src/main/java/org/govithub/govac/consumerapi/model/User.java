package org.govithub.govac.consumerapi.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "users")
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public long id;

	@Column(name = "username")
	public String username;

	@Column(name = "first_name")
	public String firstName;

	@Column(name = "last_name")
	public String lastName;

	@Column(name = "email")
	public String email;

	@Column(name = "phone")
	public String phone;

	@Column(name = "cnp")
	public String cnp;

	public User() {
	}

	public User(long id, String username, String firsName, String lastName, String email, String phone, String cnp) {
		this.id = id;
		this.username = username;
		this.firstName = firsName;
		this.lastName = lastName;
		this.email = email;
		this.phone = phone;
		this.cnp = cnp;
	}
}
