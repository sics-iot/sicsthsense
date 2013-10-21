/*
 * Copyright (c) 2013, Swedish Institute of Computer Science
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of The Swedish Institute of Computer Science nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE SWEDISH INSTITUTE OF COMPUTER SCIENCE BE LIABLE 
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. */

/* Description:
 * TODO:
 * */
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

  @SqlQuery("select * from resources where polling_period>0")
	@Mapper(ResourceMapper.class)
  List<Resource> findPolledResources();

  @SqlQuery("select id from resources where label = :label limit 1")
  int findResourceId(@Bind("label") String label);

  @SqlUpdate("insert into resources(owner_id, label, polling_period, polling_url, polling_authentication_key, description, parent_id, secret_key, version) values (:owner_id, :label, :polling_period, :polling_url, :polling_authentication_key, :description, :parent_id, :secret_key, :version)")
  void insertResource(
		@Bind("label") String label,
		@Bind("version") String version,
		@Bind("owner_id")  long owner_id, 
		@Bind("parent_id") long parent_id,
		@Bind("polling_url") String polling_url,
		@Bind("polling_authentication_key") String polling_authentication_key,
		@Bind("polling_period") long polling_period,
		@Bind("secret_key")  String secret_key,
		@Bind("description") String description
	);

  //@SqlUpdate("update resources set owner_id=':owner_id', label=':label', polling_period=':polling_period', last_polled=':last_polled', polling_url=':polling_url', polling_authentication_key=':polling_authentication_key', description=':description', parent_id=':parent_id', secret_key=':secret_key', version=':version', last_posted=':last_posted' where id  ':id'")
  @SqlUpdate("update resources set label = :label, polling_period=:polling_period, polling_url=:polling_url, polling_authentication_key=:polling_authentication_key where id = :id")
  void updateResource(
		@Bind("id") long id,
		@Bind("label") String label,
		@Bind("version") String version,
		@Bind("owner_id")  long owner_id, 
		@Bind("parent_id") Long parent_id,
		@Bind("polling_url") String polling_url,
		@Bind("polling_authentication_key") String polling_authentication_key,
		@Bind("polling_period") long polling_period,
		@Bind("secret_key")  String secret_key,
		@Bind("description") String description,
		@Bind("last_polled") long last_polled,
		@Bind("last_posted") long last_posted 
	);

  @SqlUpdate("delete from resources where id = :id")
  void deleteResource(@Bind("id") long id);


	// Streams
  @SqlQuery("select * from streams where resource_id = :resourceId")
	@Mapper(StreamMapper.class)
  List<Stream> findStreamsByResourceId(@Bind("resourceId") long resourceId);

  @SqlQuery("select * from streams where id = :id limit 1")
	@Mapper(StreamMapper.class)
  Stream findStreamById(@Bind("id") long id);

  @SqlQuery("select id from streams where resource_id = :resource_id and secret_key = :secret_key limit 1")
  int findStreamId(@Bind("resource_id") long id, @Bind("secret_key") String secret_key);

  @SqlUpdate("insert into streams( type, latitude, longitude, description, public_access, public_search, frozen, history_size, last_updated, secret_key, owner_id, resource_id, version) values (  :type, :latitude, :longitude, :description, :public_access, :public_search, :frozen, :history_size, :last_updated, :secret_key, :owner_id, :resource_id, :version)")
  void insertStream(
		@Bind("type")        String type, 
		@Bind("latitude")    double latitude, 
		@Bind("longitude")   double longitude,
		@Bind("description") String description, 
		@Bind("public_access") boolean public_access,
		@Bind("public_search") boolean public_search, 
		@Bind("frozen")       boolean frozen,
		@Bind("history_size") int history_size, 
		@Bind("last_updated") long last_updated, 
		@Bind("secret_key")   String secret_key, 
		@Bind("owner_id")     long owner_id, 
		@Bind("resource_id")  long resource_id, 
		@Bind("version")      int version 
	);

  @SqlUpdate("delete from streams where id = :id")
  void deleteStream(@Bind("id") long id);

	// Parsers
  @SqlQuery("select * from parsers where id = :id limit 1")
	@Mapper(ParserMapper.class)
  Parser findParserById(@Bind("id") long id);

  @SqlQuery("select * from parsers where resource_id = :resourceId")
	@Mapper(ParserMapper.class)
  List<Parser> findParsersByResourceId(@Bind("resourceId") long resourceId);

  @SqlUpdate("insert into parsers( resource_id, stream_id, input_parser, input_type, timeformat, data_group, time_group, number_of_points) values ( :resource_id, :stream_id, :input_parser, :input_type, :timeformat, :data_group, :time_group, :number_of_points)")
  void insertParser(
		@Bind("resource_id") long resource_id, 
		@Bind("stream_id")  long stream_id, 
		@Bind("input_parser") String input_parser,
		@Bind("input_type") String input_type,
		@Bind("timeformat") String timeformat,
		@Bind("data_group")  int data_group,
		@Bind("time_group")  int time_group,
		@Bind("number_of_points")  int number_of_points 
	);

  @SqlQuery("select id from parsers where resource_id = :resource_id and stream_id = :stream_id limit 1")
  int findParserId(@Bind("resource_id") long id, @Bind("stream_id") long stream_id);

  @SqlUpdate("update parser set label = :label, polling_period=:polling_period, polling_url=:polling_url, polling_authentication_key=:polling_authentication_key where id = :id")
  void updateParser(
		@Bind("id") long id,
		@Bind("input_parser") String input_parser,
		@Bind("intput_type") String input_type,
		@Bind("timeformat")  String timeformat,
		@Bind("data_group")  int data_group,
		@Bind("time_group")  int time_group,
		@Bind("number_of_points")  int number_of_points 
	);

  @SqlUpdate("delete from parsers where id = :id")
  void deleteParser(@Bind("id") long id);


	// DataPoints
  @SqlQuery("select * from data_point_double where id = :id limit 10")
	@Mapper(DataPointMapper.class)
  DataPoint findPointById(@Bind("id") long id);

  @SqlQuery("select * from data_point_double where stream_id = :stream_id limit :limit")
	@Mapper(DataPointMapper.class)
  List<DataPoint> findPointsByStreamId(@Bind("stream_id") long stream_id, @Bind("limit") int limit);

  @SqlUpdate("insert into data_point_double(stream_id, data, timestamp) values (:stream_id, :data, :timestamp)")
  void insertDataPoint(@Bind("stream_id") long stream_id, @Bind("data") double data, @Bind("timestamp") long timestamp);


}
