/*
 * Copyright (c) 2015, Swedish Institute of Computer Science
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *	   * Redistributions of source code must retain the above copyright
 *		 notice, this list of conditions and the following disclaimer.
 *	   * Redistributions in binary form must reproduce the above copyright
 *		 notice, this list of conditions and the following disclaimer in the
 *		 documentation and/or other materials provided with the distribution.
 *	   * Neither the name of The Swedish Institute of Computer Science nor the
 *		 names of its contributors may be used to endorse or promote products
 *		 derived from this software without specific prior written permission.
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
package se.sics.sicsthsense;

import java.util.List;
import java.util.UUID;
import java.util.Set;
import java.util.HashSet;
import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.MediaType;

import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import se.sics.sicsthsense.resources.*;
import se.sics.sicsthsense.jdbi.*;
import se.sics.sicsthsense.core.*;
import se.sics.sicsthsense.auth.*;
import se.sics.sicsthsense.model.*;
import se.sics.sicsthsense.auth.openid.*;
import se.sics.sicsthsense.model.security.*;

public class Utils {
	final static ObjectMapper mapper = new ObjectMapper();
	static {
		mapper.configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true);
	}

	// more informed JSON responses when POSTing a new entity
	/*
	public static Response resp(Response.Status status, User user, Logger logger) {
		String message;
		try { message = mapper.writeValueAsString(user); }
		  catch (Exception e) { message = "Internal Error: "+e.toString(); }
		if (logger!=null) { logger.info(message); }
		return Response.status(status).entity(message).type(MediaType.APPLICATION_JSON).build();
	}
	public static Response resp(Response.Status status, Resource resource, Logger logger) {
		String message;
		try { message = mapper.writeValueAsString(resource); }
		  catch (Exception e) { message = "Internal Error: "+e.toString(); }
		if (logger!=null) { logger.info(message); }
		return Response.status(status).entity(message).type(MediaType.APPLICATION_JSON).build();
	}
	public static Response resp(Response.Status status, Stream stream, Logger logger) {
		String message;
		try { message = mapper.writeValueAsString(stream); }
		  catch (Exception e) { message = "Internal Error: "+e.toString(); }
		if (logger!=null) { logger.info(message); }
		return Response.status(status).entity(message).type(MediaType.APPLICATION_JSON).build();
	}*/
	public static Response resp(Response.Status status, Object message, Logger logger) {
		if (logger!=null && message instanceof String) { logger.info((String)message); }
		return Response.status(status).entity(message).build();
	}

	public static void checkHierarchy(StorageDAO storage, long userId) {
		User user = storage.findUserById(userId);
		checkHierarchy(user);
	}
	public static void checkHierarchy(User user) {
		if (user==null) { throw new WebApplicationException(Status.NOT_FOUND); }
	}
	public static void checkHierarchy(StorageDAO storage, long userId, long resourceId) {
		User user		  = storage.findUserById(userId);
		Resource resource = storage.findResourceById(resourceId);
		checkHierarchy(storage,user,resource);
	}
	public static void checkHierarchy(StorageDAO storage, User user, Resource resource) {
		final Logger logger = LoggerFactory.getLogger(Utils.class);
		if (user==null) { throw new WebApplicationException(Status.NOT_FOUND); }
		if (resource==null) { throw new WebApplicationException(Status.NOT_FOUND); }
		if (resource.getOwner_id() != user.getId()) {
			logger.error("User "+user.getId()+" does not own resource "+resource.getId());
			throw new WebApplicationException(Status.NOT_FOUND);
		}
	}

	public static Resource findResourceByIdName(StorageDAO storage, String resourceName) {
		return findResourceByIdName(storage, resourceName, -1);
	}
	// return a resource after searching for its ID or URL-encoded name
	public static Resource findResourceByIdName(StorageDAO storage, String resourceName, long userId) {
		final Logger logger = LoggerFactory.getLogger(Utils.class);
		Resource resource = null;

		try { // see if we can turn the Name into an Id, and if that Id exists in the DB
			resource = storage.findResourceById(Long.parseLong(resourceName));
		} catch (NumberFormatException e) { resource=null; }

		if (resource==null) { // treat the Name as a resource label
			try {
				String label = new URI(resourceName).toString();
				if (userId==-1) { resource = storage.findResourceByLabel(label); }
				else { resource = storage.findResourceByLabel(label, userId); }
			} catch (java.net.URISyntaxException e) {
				logger.error("Can't turn URL-encoded resource label into valid String: "+e);
			}
		}
		return resource;
	}

	public static Stream findStreamByIdName(StorageDAO storage, String streamName) {
		return findStreamByIdName(storage, streamName, -1);
	}
	// return a resource after searching for its ID or URL-encoded name
	public static Stream findStreamByIdName(StorageDAO storage, String streamName, long userId) {
		final Logger logger = LoggerFactory.getLogger(Utils.class);
		Stream stream = null;
		//logger.error("Finding stream: "+streamName);

		try { // see if we can turn the Name into an Id, and if that Id exists in the DB
			stream = storage.findStreamById(Long.parseLong(streamName));
		} catch (NumberFormatException e) {
			//logger.error("could not long() stream: "+streamName);
			stream=null;
		}

		if (stream==null) { // treat the Name as a stream label
			String name;
			try { name = new URI(streamName).toString(); }
			catch (java.net.URISyntaxException e) { logger.error("Can't turn URL-encoded stream label into valid String: "+e); return null;}
			//logger.error("finding stream name: /"+name);
			final long streamId = storage.findStreamIdByPath("/"+name);
			if (streamId==0) {
				logger.error("Stream Name lookup failed! /"+name);
				return null;
			} else {
				stream = storage.findStreamById(streamId);
			}
		}
		return stream;
	}

	// add a resource
	public static long insertResource(StorageDAO storage, Resource resource) {
		// should check if label exists!

		storage.insertResource(
			resource.getLabel(),
			resource.getVersion(),
			resource.getOwner_id(),
			-1,
			resource.getPolling_url(),
			resource.getPolling_authentication_key(),
			resource.getPolling_period(),
			resource.getSecret_key(),
			resource.getDescription()
		);
		return storage.findResourceId(resource.getLabel());
	}
	public static long insertResourceLog(StorageDAO storage, ResourceLog rl) {
		// should check if label exists!

		storage.insertResourceLog(
			rl.resourceId,
			rl.creationTimestamp,
			rl.responseTimestamp,
			rl.parsedSuccessfully,
			rl.isPoll,
			rl.body,
			rl.method,
			rl.headers,
			rl.message,
			1
		);
		//return storage.findResourceId(resource.getLabel());
		return -1;
	}

	// add a resource
	public static void updateResource(StorageDAO storage, long resourceId, Resource newresource) {
		final Logger logger = LoggerFactory.getLogger(Utils.class);
		final PollSystem pollSystem = PollSystem.getInstance();

		logger.info("Updating resourceID "+resourceId+" to: "+newresource);

		Resource resource = storage.findResourceById(resourceId);
		resource.update(newresource);

		storage.updateResource(
			resourceId, // the resource ID from the PUT'd URL
			resource.getLabel(),
			resource.getVersion(),
			resource.getOwner_id(),
			-1,
			resource.getPolling_url(),
			resource.getPolling_authentication_key(),
			resource.getPolling_period(),
			resource.getSecret_key(),
			resource.getDescription(),
			resource.getLast_polled(),
			resource.getLast_posted()
		);
		// remake pollers with updated Resource attribtues
		pollSystem.rebuildResourcePoller(resourceId);
	}


	public static void authoriseResourceKey(String key1, String key2) {
		final Logger logger = LoggerFactory.getLogger(Utils.class);
		if (!key1.equals(key2)) {
			logger.warn("User has incorrect key on resource!");
			throw new WebApplicationException(Status.FORBIDDEN);
		}
	}


// Streams

	public static void checkHierarchy(StorageDAO storage, long userId, long resourceId, long streamId) {
		User user		  = storage.findUserById(userId);
		Resource resource = storage.findResourceById(resourceId);
		Stream stream	  = storage.findStreamById(streamId);
		checkHierarchy(storage, user,resource,stream);
	}
	public static void checkHierarchy(StorageDAO storage, User user, Resource resource, Stream stream) {
		final Logger logger = LoggerFactory.getLogger(Utils.class);
		checkHierarchy(storage, user, resource);
		if (stream==null) {
			logger.error("Stream could not be found!");
			throw new WebApplicationException(Status.NOT_FOUND);
		}
		if (stream.getResource_id() != resource.getId()) {
			logger.error("Resource "+resource.getId()+" does not own stream "+stream.getId());
			throw new WebApplicationException(Status.NOT_FOUND);
		}
	}

	public static void insertDataPoint(StorageDAO storage, DataPoint datapoint) throws Exception {
		final Logger logger = LoggerFactory.getLogger(Utils.class);
		Stream stream = storage.findStreamById(datapoint.getStreamId());
		if (datapoint.getTimestamp()<=0) { datapoint.setTimestamp(java.lang.System.currentTimeMillis()); }
		storage.insertDataPoint(
			datapoint.getStreamId(),
			datapoint.getValue(),
			datapoint.getTimestamp()
		);
		//logger.info("inserted datapoint @ "+datapoint.toString());
		storage.updatedStream(datapoint.getStreamId(),java.lang.System.currentTimeMillis());
		//stream.notifyDependents(); // taken care of during parsing
		//stream.testTriggers(datapoint);
	}

	public static long insertStream(StorageDAO storage, Stream stream) {
		final Logger logger = LoggerFactory.getLogger(Utils.class);
		storage.insertStream(
			stream.getType(),
			stream.getLatitude(),
			stream.getLongitude(),
			stream.getDescription(),
			stream.getPublic_access(),
			stream.getPublic_search(),
			stream.getFrozen(),
			stream.getHistory_size(),
			stream.getLast_updated(),
			stream.getSecret_key(),
			stream.getOwner_id(),
			stream.getResource_id(),
			stream.getFunction(),
			stream.getVersion()
		);
		long streamID = storage.findStreamId(stream.getResource_id(), stream.getSecret_key());
		/*
		if (stream.getLabel()!=null && stream.getLabel()!="") {
		storage.insertVFile(stream.getLabel(),stream.getOwner_id(),"D",streamID);
		} else {
			storage.insertVFile("Stream"+String.valueOf(streamID),stream.getOwner_id(),"D",streamID);
		}*/
		return streamID;
	}

	// create the dependency relationship between streams
	public static void insertDependent(StorageDAO storage, long stream, long dependent) {
		storage.insertDependent(stream,dependent);
	}

	public static void insertTrigger(StorageDAO storage, long stream_id, String url, String operator, double operand, String payload) {
		storage.insertTrigger(stream_id, url, operator, operand, payload);
	}

	public static long insertVFile(StorageDAO storage, String path, long owner_id, String type, long stream_id) {
		storage.insertVFile( path,owner_id,type,stream_id);
		return -1;
	}

	public static void deleteStream(StorageDAO storage, Stream stream) {

		// delete dependants,vfiles and parsers on the Stream
		List<Long> vfiles = storage.findPathIdsByStreamId(stream.getId());
		for (Long id: vfiles) { storage.deleteVFile(id); }
		List<Long> parsers = storage.findParserIdsByStreamId(stream.getId());
		for (Long id: parsers) { storage.deleteParser(id); }
		List<Long> dependents = storage.findDependents(stream.getId());
		for (Long id: dependents) { storage.deleteDependent(id); }
		List<Long> triggers = storage.findTriggerIdsByStreamId(stream.getId());
		for (Long id: triggers) { storage.deleteTrigger(id); }

		storage.deleteStream(stream.getId());
	}

	public static void deleteResource(StorageDAO storage, Resource resource) {
		final PollSystem pollSystem = PollSystem.getInstance();
		// delete child streams and parsers
		List<Stream> streams = storage.findStreamsByResourceId(resource.getId());
		List<Parser> parsers = storage.findParsersByResourceId(resource.getId());
		for (Stream s: streams) {Utils.deleteStream(storage,s);}
		for (Parser p: parsers) {storage.deleteParser(p.getId());}
		storage.deleteResourceLogByResourceId(resource.getId());
		storage.deleteResource(resource.getId());
		// remake pollers with updated Resource attribtues
		pollSystem.rebuildResourcePoller(resource.getId());
	}


	public static void applyParsers(StorageDAO storage, Resource resource, String data, long timestamp) {
		final Logger logger = LoggerFactory.getLogger(Utils.class);
		ParseData parseData = new ParseData(storage); // should really be static somewhere
		boolean parsedSuccessfully=true;

		String parseError = "";
		Set<Long> toUpdate = new HashSet<Long>(); // give these stream notifcation after update
		List<Parser> parsers = storage.findParsersByResourceId(resource.getId());
		//logger.info("Applying all parsers to data: "+data);
		if (parsers==null) { parsers = storage.findParsersByResourceId(resource.getId()); }
		for (Parser parser: parsers) {
			//logger.info("applying a parser "+parser.getInput_parser());
			try {
				parseData.apply(parser,data,timestamp);
				toUpdate.add(parser.getStream_id());
			} catch (Exception e) {
				parsedSuccessfully=false;
				e.printStackTrace();
				logger.error("Parsing "+data+" failed!"+e);
				parseError +="Parsing "+data+" failed!"+e;
			}
		}
		// bunch all notifications here!
		try { for (Long stream_id: toUpdate) {
			//logger.warn(" dependents:"+stream_id);
			Stream.notifyDependents(storage,stream_id.longValue());
		}
		} catch (Exception e) {
			logger.error("Children not accepting notification! "+e);
			e.printStackTrace();
		}

		// append interaction to resource log!
	//	logger.info("Updating log!");
		ResourceLog rl = ResourceLog.createOrUpdate(storage,resource.getId());
		//TODO: update the actual log message!
		rl.update(parsedSuccessfully, false, "received POST", System.currentTimeMillis());
		rl.save();
	}


}
