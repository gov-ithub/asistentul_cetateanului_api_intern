package org.govithub.govac.consumerapi.dao.migrations;

import org.flywaydb.core.api.migration.spring.SpringJdbcMigration;
import org.springframework.jdbc.core.JdbcTemplate;

public class V1__User implements SpringJdbcMigration {
	public void migrate(JdbcTemplate jdbcTemplate) throws Exception {
		jdbcTemplate.execute(
				"CREATE TABLE users(id bigserial NOT NULL, "
				+ "username character varying(255), "
				+ "cnp character varying(255), email character varying(255), "
				+ "first_name character varying(255),   last_name character varying(255), "
				+ "phone character varying(255), "
				+ "CONSTRAINT users_pkey PRIMARY KEY (id));");
		
		jdbcTemplate.execute(
				"CREATE TABLE notifications ( id bigint NOT NULL, application character varying(255), " 
				+ "description character varying(255), provider character varying(255), "
				+ "short_description character varying(255), \"timestamp\" bigint, title character varying(255), user_id bigint, "
				+ "CONSTRAINT notifications_pkey PRIMARY KEY (id), "
				+ "CONSTRAINT fk_notifications_users FOREIGN KEY (user_id) REFERENCES users (id) "
				+ "MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION );");
	}
}