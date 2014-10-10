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
package se.sics.sicsthsense;

import java.util.List;
import java.util.UUID;
import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.Response.ResponseBuilder;

import se.sics.sicsthsense.resources.*;
import se.sics.sicsthsense.jdbi.*;
import se.sics.sicsthsense.core.*;
import se.sics.sicsthsense.auth.*;
import se.sics.sicsthsense.model.*;
import se.sics.sicsthsense.auth.openid.*;
import se.sics.sicsthsense.model.security.*;

public class Utils {

	public static Response resp(Response.Status status, Object message, Logger logger) {
		if (logger!=null && message instanceof String) { logger.info((String)message); }
		return Response.status(status).entity(message).build();
	}

	public static void checkHierarchy(long userId) {
		final StorageDAO storage = DAOFactory.getInstance();
		User user = storage.findUserById(userId);
		checkHierarchy(user);
	}
	public static void checkHierarchy(User user) {
		if (user==null) { throw new WebApplicationException(Status.NOT_FOUND); }
	}
	public static void checkHierarchy(long userId, long resourceId) {
		final StorageDAO storage = DAOFactory.getInstance();
		//final Logger logger = LoggerFactory.getLogger(Utils.class);
		User user = storage.findUserById(userId);
		Resource resource = storage.findResourceById(resourceId);
		checkHierarchy(user,resource);
	}
	public static void checkHierarchy(User user, Resource resource) {
		final StorageDAO storage = DAOFactory.getInstance();
		final Logger logger = LoggerFactory.getLogger(Utils.class);
		if (user==null) { throw new WebApplicationException(Status.NOT_FOUND); }
		if (resource==null) { throw new WebApplicationException(Status.NOT_FOUND); }
		if (resource.getOwner_id() != user.getId()) {
			logger.error("User "+user.getId()+" does not own resource "+resource.getId());
			throw new WebApplicationException(Status.NOT_FOUND);
		}
	}

	public static Resource findResourceByIdName(String resourceName) {
		return findResourceByIdName(resourceName, -1);
	}
	// return a resource after searching for its ID or URL-encoded name
	public static Resource findResourceByIdName(String resourceName, long userId) {
		final StorageDAO storage = DAOFactory.getInstance();
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

	public static Stream findStreamByIdName(String streamName) {
		return findStreamByIdName(streamName, -1);
	}
	// return a resource after searching for its ID or URL-encoded name
	public static Stream findStreamByIdName(String streamName, long userId) {
		final StorageDAO storage = DAOFactory.getInstance();
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
	public static long insertResource(Resource resource) {
		final StorageDAO storage = DAOFactory.getInstance();
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
	public static long insertResourceLog(ResourceLog rl) {
		final StorageDAO storage = DAOFactory.getInstance();
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
	public static void updateResource(long resourceId, Resource newresource) {
		final StorageDAO storage = DAOFactory.getInstance();
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

	public static void checkHierarchy(long userId, long resourceId, long streamId) {
		final StorageDAO storage = DAOFactory.getInstance();
		User user = storage.findUserById(userId);
		Resource resource = storage.findResourceById(resourceId);
		Stream stream = storage.findStreamById(streamId);
		checkHierarchy(user,resource,stream);
	}
	public static void checkHierarchy(User user, Resource resource, Stream stream) {
		final StorageDAO storage = DAOFactory.getInstance();
		final Logger logger = LoggerFactory.getLogger(Utils.class);
		checkHierarchy(user, resource);
		if (stream==null) {
			logger.error("Stream could not be found!");
			throw new WebApplicationException(Status.NOT_FOUND);
		}
		if (stream.getResource_id() != resource.getId()) {
			logger.error("Resource "+resource.getId()+" does not own stream "+stream.getId());
			throw new WebApplicationException(Status.NOT_FOUND);
		}
	}

	public static void insertDataPoint(DataPoint datapoint) {
		final StorageDAO storage = DAOFactory.getInstance();
		final Logger logger = LoggerFactory.getLogger(Utils.class);
        Stream stream = storage.findStreamById(datapoint.getStreamId());
		if (datapoint.getTimestamp()<=0) {
			datapoint.setTimestamp(java.lang.System.currentTimeMillis());
		}
		storage.insertDataPoint(
			datapoint.getStreamId(),
			datapoint.getValue(),
			datapoint.getTimestamp()
		);
        stream.notifyDependents();
        stream.testTriggers(datapoint);
		storage.updatedStream(datapoint.getStreamId(),java.lang.System.currentTimeMillis());
	}

	public static long insertStream(Stream stream) {
		final StorageDAO storage = DAOFactory.getInstance();
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
		//already performed by other code
		//storage.insertVFile("/stream/"+String.valueOf(streamID),stream.getOwner_id(),"D",streamID);
		return streamID;
	}

	// create the dependency relationship between streams
	public static void insertDependent(long stream, long dependent) {
		final StorageDAO storage = DAOFactory.getInstance();
		storage.insertDependent(stream,dependent);
	}
	
	public static void insertTrigger(long stream_id, String url, String operator, double operand, String payload) {
		final StorageDAO storage = DAOFactory.getInstance();
		storage.insertTrigger(stream_id, url, operator, operand, payload);
	}

	public static long insertVFile(String path, long owner_id, String type, long stream_id) {
		final StorageDAO storage = DAOFactory.getInstance();
		storage.insertVFile( path,owner_id,type,stream_id);
		return -1;
	}


	public static void applyParsers(Resource resource, String data, long timestamp) {
		final StorageDAO storage = DAOFactory.getInstance();
		final Logger logger = LoggerFactory.getLogger(Utils.class);
		ParseData parseData = new ParseData(); // should really be static somewhere
		boolean parsedSuccessfully=true;

		//#long resourceId
		String parseError = "";
		List<Parser> parsers = storage.findParsersByResourceId(resource.getId());
		//logger.info("Applying all parsers to data: "+data);
		if (parsers==null) { parsers = storage.findParsersByResourceId(resource.getId()); }
		for (Parser parser: parsers) {
			//logger.info("applying a parser "+parser.getInput_parser());
			try {
				parseData.apply(parser,data,timestamp);
			} catch (Exception e) {
				parsedSuccessfully=false;
				logger.error("Parsing "+data+" failed!"+e);
				parseError +="Parsing "+data+" failed!"+e;
			}
		}
		// append interaction to resource log!
	//	logger.info("Updating log!");
		ResourceLog rl = ResourceLog.createOrUpdate(resource.getId());
		//TODO: update the actual log message!
		rl.update(parsedSuccessfully, false, "received POST", System.currentTimeMillis());
		rl.save();
	}


}
