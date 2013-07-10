package com.sics.sicsthsense.jdbi;

import org.skife.jdbi.v2.*;
import org.skife.jdbi.v2.sqlobject.*;
import org.skife.jdbi.v2.sqlobject.customizers.*;

import com.yammer.dropwizard.jdbi.*;
import com.yammer.dropwizard.db.*;

import com.sics.sicsthsense.core.*;

public interface StorageDAO {

  //@SqlUpdate("create table something (id int primary key, name varchar(100))")
  //void createSomethingTable();

	// Users
  @SqlQuery("select * from users where id = :id")
	@Mapper(UserMapper.class)
  User findUserById(@Bind("id") int id);

  @SqlQuery("select user_name from users where id = :id")
  String findUsernameById(@Bind("id") int id);

  @SqlUpdate("insert into users (id, name) values (:id, :name)")
  void insertUser(@Bind("id") int id, @Bind("name") String name);


	// Resources
  @SqlQuery("select * from resources where id = :id")
	@Mapper(ResourceMapper.class)
  Resource findResourceById(@Bind("id") long id);

  @SqlUpdate("insert into resources(owner_id, label, polling_period, last_polled, polling_url, polling_authentication_key, description, parent_id, secret_key, version, last_posted ) values (:owner_id, :label, :polling_period, :last_polled, :polling_url, :polling_authentication_key, :description, :parent_id, :secret_key, 1, -1)")
  void insertResource(
		@Bind("owner_id") String owner_id, 
		@Bind("label") String label,
		@Bind("polling_period") String polling_period,
		@Bind("last_polled") String last_polled,
		@Bind("polling_url") String polling_url,
		@Bind("polling_authentication_key") String polling_authentication_key,
		@Bind("description") String description,
		@Bind("parent_id") String parent_id,
		@Bind("secret_key") String secret_key,
		@Bind("version") String version,
		@Bind("last_posted") String last_posted 
	);

	// Streams
  @SqlQuery("select * from resources where id = :id")
	@Mapper(StreamMapper.class)
  Stream findStreamById(@Bind("id") long id);
}

