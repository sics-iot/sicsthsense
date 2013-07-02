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
  void insert(@Bind("id") int id, @Bind("name") String name);


	// Resources
  @SqlQuery("select * from resources where id = :id")
	@Mapper(ResourceMapper.class)
  Resource findResourceById(@Bind("id") int id);


	// Streams
  @SqlQuery("select * from resources where id = :id")
	@Mapper(StreamMapper.class)
  Stream findStreamById(@Bind("id") int id);
}

