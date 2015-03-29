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
package se.sics.sicsthsense.jdbi;

import java.util.List;

import org.skife.jdbi.v2.*;
import org.skife.jdbi.v2.sqlobject.*;
import org.skife.jdbi.v2.sqlobject.customizers.*;

import io.dropwizard.jdbi.*;
import io.dropwizard.db.*;

import se.sics.sicsthsense.core.*;

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

  @SqlQuery("select id from users where username = :username limit 1")
  long findUserIdByUsername(@Bind("username") String username);

  @SqlQuery("select * from users where email = :email limit 1")
	@Mapper(UserMapper.class)
  User findUserByEmail(@Bind("email") String email);

  @SqlQuery("select id from users where email = :email limit 1")
  long findUserIdByEmail(@Bind("email") String email);

  @SqlQuery("select username from users where id = :id limit 1")
  String findUsernameById(@Bind("id") long id);

  @SqlUpdate("insert into users (username, email, first_name, last_name, creation_date, version, token, password) VALUES (:username, :email, :first_name, :last_name, NOW(), 1, :token, :password)")
  void insertUser(
		@Bind("username")   String username,
		@Bind("email")      String email,
		@Bind("first_name") String first_name,
		@Bind("last_name")  String last_name,
		@Bind("token")      String token,
		@Bind("password")		String password
	);

  @SqlUpdate("update resources set username = :username, first_name=:first_name, last_name=:last_name, email where id = :id")
  void updateUser(
		@Bind("id")         long id,
		@Bind("username")   String username,
		@Bind("first_name") String first_name,
		@Bind("last_name")  String last_name,
		@Bind("email")      String email
		//@Bind("password")		String password
	);


	// Resources
  @SqlQuery("select * from resources where owner_id = :id")
	@Mapper(ResourceMapper.class)
  List<Resource> findResourcesByOwnerId(@Bind("id") long id);

  @SqlQuery("select * from resources where id = :id limit 1")
	@Mapper(ResourceMapper.class)
  Resource findResourceById(@Bind("id") long id);

  @SqlQuery("select * from resources where label = :label limit 1")
	@Mapper(ResourceMapper.class)
  Resource findResourceByLabel(@Bind("label") String label);

  @SqlQuery("select * from resources where label = :label and owner_id = :owner_id limit 1")
	@Mapper(ResourceMapper.class)
  Resource findResourceByLabel(@Bind("label") String label, @Bind("owner_id") long owner_id);

  @SqlQuery("select * from resources where polling_period>0")
	@Mapper(ResourceMapper.class)
  List<Resource> findPolledResources();

  @SqlQuery("select id from resources where label = :label limit 1")
  long findResourceId(@Bind("label") String label);

  @SqlUpdate("insert into resources(owner_id, label, polling_period, polling_url, polling_authentication_key, description, parent_id, secret_key, version) values (:owner_id, :label, :polling_period, :polling_url, :polling_authentication_key, :description, NULL, :secret_key, :version)")
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
		@Bind("parent_id") long parent_id,
		@Bind("polling_url") String polling_url,
		@Bind("polling_authentication_key") String polling_authentication_key,
		@Bind("polling_period") long polling_period,
		@Bind("secret_key")  String secret_key,
		@Bind("description") String description,
		@Bind("last_polled") long last_polled,
		@Bind("last_posted") long last_posted
	);

  @SqlUpdate("update resources set last_polled=:time where id = :id")
  void polledResource(@Bind("id") long id, @Bind("time") long time);

  @SqlUpdate("update resources set last_posted=:time where id = :id")
  void postedResource(@Bind("id") long id, @Bind("time") long time);

  @SqlUpdate("delete from resources where id = :id")
  void deleteResource(@Bind("id") long id);


	// Streams
  @SqlQuery("select * from streams where resource_id = :resourceId")
	@Mapper(StreamMapper.class)
  List<Stream> findStreamsByResourceId(@Bind("resourceId") long resourceId);

  @SqlQuery("select * from streams where id = :id limit 1")
	@Mapper(StreamMapper.class)
  Stream findStreamById(@Bind("id") long id);

  @SqlQuery("select streams.id from streams inner join vfiles on vfiles.linked_stream_id=streams.id where vfiles.path=':name'")
  long findStreamIdByName(@Bind("name") String name);

  @SqlQuery("select * from streams inner join vfiles on vfiles.linked_stream_id=streams.id where vfiles.path=':name'")
	@Mapper(StreamMapper.class)
  Stream findStreamByName(@Bind("name") String name);

  @SqlQuery("select * from streams inner join vfiles on vfiles.linked_stream_id=streams.id where vfiles.path=':name'")
	@Mapper(StreamMapper.class)
  Stream findStreamByName(@Bind("name") String name, @Bind("userId") long userId);

  @SqlQuery("select id from streams where resource_id = :resource_id and secret_key = :secret_key limit 1")
  long findStreamId(@Bind("resource_id") long id, @Bind("secret_key") String secret_key);

  @SqlUpdate("insert into streams( type, latitude, longitude, description, public_access, public_search, frozen, history_size, last_updated, secret_key, owner_id, resource_id, function, version) values (  :type, :latitude, :longitude, :description, :public_access, :public_search, :frozen, :history_size, :last_updated, :secret_key, :owner_id, :resource_id, :function, :version)")
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
		@Bind("function")			String function,
		@Bind("version")      int version
	);

  @SqlUpdate("update streams set last_updated=:time where id = :id")
  void updatedStream(@Bind("id") long id, @Bind("time") long time);

  @SqlUpdate("delete from streams where id = :id")
  void deleteStream(@Bind("id") long id);


  @SqlQuery("select stream_id from dependents where dependent_id = :dependent_id")
	List<Long> findAntecedents(@Bind("dependent_id") long dependent_id);

  @SqlQuery("select dependent_id from dependents where stream_id = :stream_id")
	List<Long> findDependents(@Bind("stream_id") long stream_id);

	// Dependent / Antecedent relation
  @SqlUpdate("insert into dependents(stream_id, dependent_id) values (:stream_id, :dependent_id)")
  void insertDependent(
		@Bind("stream_id")     long stream_id,
		@Bind("dependent_id")	 long dependent_id
	);

  @SqlUpdate("delete from dependents where dependent_id=:dependent_id")
  void deleteDependent(
		@Bind("dependent_id")	 long dependent_id
	);


	// Triggers
  @SqlQuery("select * from triggers where stream_id = :stream_id")
	@Mapper(TriggerMapper.class)
	List<Trigger> findTriggersByStreamId(@Bind("stream_id") long stream_id);

  @SqlQuery("select id from triggers where stream_id = :stream_id")
	@Mapper(TriggerMapper.class)
	List<Long> findTriggerIdsByStreamId(@Bind("stream_id") long stream_id);

  @SqlUpdate("insert into triggers(stream_id, url, operator, operand, payload) values (:stream_id, :url, :operator, :operand, :payload)")
  void insertTrigger(
		@Bind("stream_id")  long stream_id,
		@Bind("url")		String url,
		@Bind("operator")	String operator,
		@Bind("operand")	double operand,
		@Bind("payload")	String payload
	);

  @SqlUpdate("delete from triggers where id = :id")
  void deleteTrigger(
		@Bind("id")	 long dependent_id
	);


	// VFiles
  @SqlQuery("select path from vfiles where linked_stream_id = :stream_id limit 1")
	String findPathByStreamId(@Bind("stream_id") long stream_id);

  @SqlQuery("select id from vfiles where linked_stream_id = :stream_id")
	List<Long> findPathIdsByStreamId(@Bind("stream_id") long stream_id);

  @SqlQuery("select linked_stream_id from vfiles where path = :path limit 1")
	long findStreamIdByPath(@Bind("path") String path);

  @SqlUpdate("insert into vfiles(path, owner_id, type, linked_stream_id ) values (:path, :owner_id, :type, :linked_stream_id )")
  void insertVFile(
		@Bind("path")       String path,
		@Bind("owner_id")		long owner_id,
		@Bind("type")       String type,
		@Bind("linked_stream_id")   long linked_stream_id
	);
  @SqlUpdate("delete from vfiles where id = :id")
  void deleteVFile(
		@Bind("id")	 long id
	);
  @SqlUpdate("delete from vfiles where stream_id = :stream_id")
  void deleteStreamsVFile(
		@Bind("stream_id")	 long stream_id
	);


	// Parsers
  @SqlQuery("select * from parsers where id = :id limit 1")
	@Mapper(ParserMapper.class)
  Parser findParserById(@Bind("id") long id);

  @SqlQuery("select * from parsers where resource_id = :resourceId")
	@Mapper(ParserMapper.class)
  List<Parser> findParsersByResourceId(@Bind("resourceId") long resourceId);

  @SqlQuery("select id from parsers where stream_id = :streamId")
  List<Long> findParserIdsByStreamId(@Bind("streamId") long streamId);

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
  long findParserId(@Bind("resource_id") long id, @Bind("stream_id") long stream_id);

  @SqlUpdate("update parsers set label = :label, polling_period=:polling_period, polling_url=:polling_url, polling_authentication_key=:polling_authentication_key where id = :id")
  void updateParser(
		@Bind("id") long id,
		@Bind("input_parser") String input_parser,
		@Bind("intput_type")  String input_type,
		@Bind("timeformat")   String timeformat,
		@Bind("data_group")   int data_group,
		@Bind("time_group")   int time_group,
		@Bind("number_of_points")  int number_of_points
	);

  @SqlUpdate("delete from parsers where id = :id")
  void deleteParser(@Bind("id") long id);


	//ResourceLog
  @SqlQuery("SELECT * FROM resource_log WHERE resource_id = :resource_id  LIMIT 1")
	@Mapper(ResourceLogMapper.class)
  ResourceLog findResourceLogByResourceId(@Bind("resource_id") long resource_id);

  @SqlQuery("SELECT * FROM resource_log WHERE id = :id  LIMIT 1")
	@Mapper(ResourceLogMapper.class)
  ResourceLog findResourceLogById(@Bind("id") long id);

  @SqlUpdate("insert into resource_log (resource_id, creation_timestamp, response_timestamp, parsed_successfully, is_poll, body, method, headers, message, version) values (:resource_id, :creation_timestamp, :response_timestamp, :parsed_successfully, :is_poll, :body, :method, :headers, :message, :version)")
  void insertResourceLog(
		@Bind("resource_id") long resource_id,
		@Bind("creation_timestamp") long creation_timestamp,
		@Bind("response_timestamp") long response_timestamp,
		@Bind("parsed_successfully") boolean parsed_successfully,
		@Bind("is_poll") boolean is_poll,
		@Bind("body")   String body,
		@Bind("method") String method ,
		@Bind("headers") String headers,
		@Bind("message") String message,
		@Bind("version") int version
	);

  @SqlUpdate("update resource_log set creation_timestamp=:creation_timestamp, response_timestamp=:response_timestamp, parsed_successfully=:parsed_successfully, is_poll=:is_poll, body=:body, method=:method, headers=:headers, message=:message, version=:version WHERE resource_id=:resource_id")
  void updateResourceLog(
		@Bind("resource_id") long resource_id,
		@Bind("creation_timestamp") long creation_timestamp,
		@Bind("response_timestamp") long response_timestamp,
		@Bind("parsed_successfully") boolean parsed_successfully,
		@Bind("is_poll") boolean is_poll,
		@Bind("body")   String body,
		@Bind("method") String method,
		@Bind("headers") String headers,
		@Bind("message") String message,
		@Bind("version") int version
	);

  @SqlUpdate("delete from resource_log where id = :id")
  void deleteResourceLog(@Bind("id") long id);

  @SqlUpdate("delete from resource_log where resource_id = :resource_id")
  void deleteResourceLogByResourceId(@Bind("resource_id") long resource_id);


	// DataPoints
  @SqlQuery("SELECT * FROM data_point_double WHERE id = :id ORDER BY id DESC  LIMIT 10")
	@Mapper(DataPointMapper.class)
  DataPoint findPointById(@Bind("id") long id);

  @SqlQuery("SELECT * FROM data_point_double WHERE stream_id = :stream_id ORDER BY id DESC limit :limit")
	@Mapper(DataPointMapper.class)
  List<DataPoint> findPointsByStreamId(@Bind("stream_id") long stream_id, @Bind("limit") int limit);

  @SqlQuery("SELECT * FROM data_point_double WHERE stream_id = :stream_id AND timestamp > :from")
	@Mapper(DataPointMapper.class)
  List<DataPoint> findPointsByStreamIdSince(@Bind("stream_id") long stream_id, @Bind("from") long from);

  @SqlQuery("SELECT * FROM data_point_double WHERE stream_id = :stream_id AND timestamp > :from AND timestamp < :until")
	@Mapper(DataPointMapper.class)
  List<DataPoint> findPointsByStreamIdSince(@Bind("stream_id") long stream_id, @Bind("from") long from, @Bind("until") long until);

  @SqlQuery("SELECT * FROM data_point_double WHERE stream_id = :stream_id AND timestamp > :from LIMIT :limit")
	@Mapper(DataPointMapper.class)
  List<DataPoint> findPointsByStreamIdSinceLimit(@Bind("stream_id") long stream_id, @Bind("from") long from, @Bind("limit") int limit);

  @SqlUpdate("REPLACE INTO data_point_double(stream_id, data, timestamp) VALUES (:stream_id, :data, :timestamp)")
  void insertDataPoint(@Bind("stream_id") long stream_id, @Bind("data") double data, @Bind("timestamp") long timestamp);

}
