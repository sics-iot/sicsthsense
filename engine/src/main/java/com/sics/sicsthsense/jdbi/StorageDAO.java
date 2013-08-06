package com.sics.sicsthsense.jdbi;

import java.util.List;

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
  @SqlQuery("select * from users where id = :id limit 1")
	@Mapper(UserMapper.class)
  User findUserById(@Bind("id") long id);

  @SqlQuery("select * from users where username = :username limit 1")
	@Mapper(UserMapper.class)
  User findUserByUsername(@Bind("username") String username);

  @SqlQuery("select * from users where email = :email limit 1")
	@Mapper(UserMapper.class)
  User findUserByEmail(@Bind("email") String email);

  @SqlQuery("select user_name from users where id = :id limit 1")
  String findUsernameById(@Bind("id") long id);

  @SqlUpdate("insert into users (id, name) values (:id, :name)")
  void insertUser(@Bind("id") long id, @Bind("name") String name);


	// Resources
  @SqlQuery("select * from resources where owner_id = :id")
	@Mapper(ResourceMapper.class)
  List<Resource> findResourcesByOwnerId(@Bind("id") long id);

  @SqlQuery("select * from resources where id = :id limit 1")
	@Mapper(ResourceMapper.class)
  Resource findResourceById(@Bind("id") long id);


  @SqlUpdate("insert into resources(owner_id, label, polling_period, last_polled, polling_url, polling_authentication_key, description, parent_id, secret_key, version, last_posted ) values (:owner_id, :label, :polling_period, :last_polled, :polling_url, :polling_authentication_key, :description, :parent_id, :secret_key, :version, :last_posted)")
  void insertResource(
		@Bind("owner_id") long owner_id, 
		@Bind("label") String label,
		@Bind("polling_period") String polling_period,
		@Bind("last_polled") String last_polled,
		@Bind("polling_url") String polling_url,
		@Bind("polling_authentication_key") String polling_authentication_key,
		@Bind("description") String description,
		@Bind("parent_id") long parent_id,
		@Bind("secret_key") String secret_key,
		@Bind("version") String version,
		@Bind("last_posted") String last_posted 
	);

	// Streams
  @SqlQuery("select * from streams where resource_id = :resourceid")
	@Mapper(StreamMapper.class)
  List<Stream> findStreamsByResourceId(@Bind("resourceid") long resourceid);

  @SqlQuery("select * from streams where id = :id limit 1")
	@Mapper(StreamMapper.class)
  Stream findStreamById(@Bind("id") long id);

	// Parsers
  @SqlQuery("select * from parsers where id = :id limit 1")
	@Mapper(ParserMapper.class)
  Parser findParserById(@Bind("id") long id);

	// DataPoints
  @SqlQuery("select * from data_point_double where stream_id = :stream_id limit 10")
	@Mapper(PointMapper.class)
  DataPoint findPointByStreamId(@Bind("stream_id") long stream_id);

  @SqlQuery("select * from data_point_double where stream_id = :stream_id limit :limit")
	@Mapper(PointMapper.class)
  List<DataPoint> findPointsByStreamId(@Bind("stream_id") long stream_id, @Bind("limit") int limit);

  @SqlUpdate("insert into data_point_double(id, stream_id, timestamp, data) values (:id, :stream_id, :timestamp, :data)")
  void insertDataPoint(@Bind("id") long id, @Bind("stream_id") long stream_id, @Bind("timestamp") long timestamp, @Bind("data") double data);



}
