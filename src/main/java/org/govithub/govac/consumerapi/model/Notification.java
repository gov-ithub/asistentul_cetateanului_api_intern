package org.govithub.govac.consumerapi.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "notifications")
public class Notification {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public long id;

	@Column(name = "title")
	public String title;
	
	@Column(name="description")
	public String description;
	
	@Column(name="short_description")
	public String shortDescription;
	
	@Column(name = "provider")
	public String provider;
	
	@Column(name= "application")
	public String application;
	
	@Column(name = "timestamp")
	public long timestamp;
	
	@ManyToOne
    @JoinColumn(name = "user_id")
	public User user;

	public Notification() {
	}

	public Notification(long id, String title, String description, String short_description,
			String provider, String application, long timestamp, User user) {
		this.id = id;
		this.title = title;
		this.description = description;
		this.shortDescription = short_description;
		this.provider = provider;
		this.timestamp = timestamp;
		this.application = application;
		this.user = user;
	}
}
