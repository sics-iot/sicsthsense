/*
 * Copyright (c) 2013, Swedish Institute of Computer Science
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *		 * Redistributions of source code must retain the above copyright
 *			 notice, this list of conditions and the following disclaimer.
 *		 * Redistributions in binary form must reproduce the above copyright
 *			 notice, this list of conditions and the following disclaimer in the
 *			 documentation and/or other materials provided with the distribution.
 *		 * Neither the name of The Swedish Institute of Computer Science nor the
 *			 names of its contributors may be used to endorse or promote products
 *			 derived from this software without specific prior written permission.
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

/* Description: Jersey Resource for SicsthSense Resources. Handles config of Resources
 * contains the Parsers and Streams of the associated Resource. 
 * TODO:
 * */
package com.sics.sicsthsense.resources.atmosphere;

import java.util.List;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicLong;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.DELETE;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.QueryParam;
import javax.ws.rs.PathParam;
import javax.ws.rs.FormParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.common.base.Optional;
import com.yammer.metrics.annotation.Timed;
import com.yammer.dropwizard.auth.Auth;
import org.atmosphere.annotation.Broadcast;
import org.atmosphere.annotation.Suspend;

import com.sics.sicsthsense.core.*;
import com.sics.sicsthsense.jdbi.*;
import com.sics.sicsthsense.model.*;
import com.sics.sicsthsense.auth.*;
import com.sics.sicsthsense.auth.annotation.RestrictedTo;
import com.sics.sicsthsense.model.security.Authority;

// publicly reachable path of the resource
@Path("/{userId}/resources")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ResourceResource {
	private final StorageDAO storage;
	private final AtomicLong counter;
	private PollSystem pollSystem;
	private final Logger logger = LoggerFactory.getLogger(ResourceResource.class);
	public ParseData parseData;
	List<Parser> parsers;

	// constructor with the system's stoarge and poll system.
	public ResourceResource() {
		this.storage = DAOFactory.getInstance();
		this.pollSystem = PollSystem.getInstance();
		this.counter = new AtomicLong();
		this.parseData = new ParseData();;
	}

	@GET
	@Timed
	public List<Resource> getResources(@PathParam("userId") long userId, @QueryParam("token") String token) {
		//User visitor = new User();
		//logger.info("Getting all user "+userId+" resources for visitor "+visitor.toString());
		checkHierarchy(userId);
		User user = storage.findUserById(userId);
		List<Resource> resources = storage.findResourcesByOwnerId(userId);
		if (!user.getToken().equals(token)) { 
			throw new WebApplicationException(Status.FORBIDDEN);
			/*
			Iterator<Resource> it = resources.iterator();
			while (it.hasNext()) {
				Resource r = it.next();
				if (r.) {it.remove();}
			}*/
		}
		return resources;
	}

	@GET
	@Path("/{resourceId}")
	@Produces({MediaType.APPLICATION_JSON})
	@Timed
	public Resource getResource(@PathParam("userId") long userId, @PathParam("resourceId") long resourceId, @QueryParam("token") String token) {
		logger.info("Getting user/resource: "+userId+"/"+resourceId);
		checkHierarchy(userId);
		Resource resource = storage.findResourceById(resourceId);
		if (resource == null) {
			logger.error("Resource "+resourceId+" does not exist!");
			throw new WebApplicationException(Status.NOT_FOUND);
		}
		if (resource.getOwner_id() != userId) {
			logger.error("User "+userId+" does not own resource "+resourceId);
			throw new WebApplicationException(Status.NOT_FOUND);
		}
		User user = storage.findUserById(userId);
		if (!user.getToken().equals(token)) { throw new WebApplicationException(Status.FORBIDDEN); }
		/*
		if (!resource.isReadable(visitor)) {
			logger.warn("Resource "+resource.getId()+" is not readable to user "+visitor.getId());
//		throw new WebApplicationException(Status.FORBIDDEN);
		}*/
		return resource;
	}

	void authoriseResourceKey(String key1, String key2) {
		if (!key1.equals(key2)) { 
			logger.warn("User has incorrect secret_key on resource!");
			throw new WebApplicationException(Status.FORBIDDEN);
		}
	}

	// post new resource definition 
	@POST
	@Consumes({MediaType.APPLICATION_JSON})
	@Timed
	public long postResource( @PathParam("userId") long userId, Resource resource, @QueryParam("token") String token) {
		logger.info("Adding user/resource:"+resource.getLabel());
		checkHierarchy(userId);
		User user = storage.findUserById(userId);
		if (!token.equals(user.getToken())) {throw new WebApplicationException(Status.FORBIDDEN);}

		resource.setOwner_id(userId); // should know the owner
		long resourceId = insertResource(resource);
		if (resource.getPolling_period() > 0) {
			// remake pollers with updated Resource attribtues
			pollSystem.rebuildResourcePoller(resourceId);
		}
		return resourceId;
	}

	// put updated resource definition 
	@PUT
	@Consumes({MediaType.APPLICATION_JSON})
	@Timed
	@Path("/{resourceId}")
	//public void updateResource(@RestrictedTo(Authority.ROLE_USER) User visitor, @PathParam("userId") long userId, @PathParam("resourceId") long resourceId, Resource resource) {
	public void updateResource(@PathParam("userId") long userId, @PathParam("resourceId") long resourceId, Resource resource, @QueryParam("secret_key") String secret_key) {
		logger.info("Updating resourceId:"+resourceId);
		checkHierarchy(userId);
		Resource oldresource = storage.findResourceById(resourceId);
		authoriseResourceKey(oldresource.getSecret_key(),secret_key);
		updateResource(resourceId, resource);
	}

	@DELETE
	@Timed
	@Path("/{resourceId}")
	public void deleteResource(//@RestrictedTo(Authority.ROLE_USER) User visitor, 
			@PathParam("userId") long userId, @PathParam("resourceId") long resourceId, @QueryParam("secret_key") String secret_key) {
		logger.warn("Deleting resourceId:"+resourceId);
		checkHierarchy(userId);
		Resource resource = storage.findResourceById(resourceId);
		if (resource==null) {
			logger.error("No resource to delete: "+resourceId);
			throw new WebApplicationException(Status.NOT_FOUND);
		}
		Resource oldresource = storage.findResourceById(resourceId);
		authoriseResourceKey(oldresource.getSecret_key(),secret_key);
		// delete child streams and parsers
		List<Stream> streams = storage.findStreamsByResourceId(resourceId);
		List<Parser> parsers = storage.findParsersByResourceId(resourceId);
		for (Parser p: parsers) {storage.deleteParser(p.getId());}
		for (Stream s: streams) {storage.deleteStream(s.getId());}
		storage.deleteResource(resourceId);
		// remake pollers with updated Resource attribtues
		pollSystem.rebuildResourcePoller(resourceId);
	}

	@GET
	@Path("/{resourceId}/data")
	public String getData() {
		return "Error: Only Streams can have data read";
	}

	// Post data to the resource, and run data through its parsers
	@POST
	@Consumes({MediaType.APPLICATION_JSON})
	@Path("/{resourceId}/data")
	//public void postData(@RestrictedTo(Authority.ROLE_USER) User visitor, @PathParam("userId") long userId, @PathParam("resourceId") long resourceId, DataPoint datapoint) {
	public String postData(@PathParam("userId") long userId, @PathParam("resourceId") long resourceId, String data, @QueryParam("token") String token, @QueryParam("secret_key") String secret_key) {
		checkHierarchy(userId);
		User user = storage.findUserById(userId);
		Resource resource = storage.findResourceById(resourceId);
		if (!resource.getSecret_key().equals(secret_key) && !user.getToken().equals(token)) { 
			logger.warn("User is not owner and has incorrect secret_key on stream!");
			throw new WebApplicationException(Status.FORBIDDEN);
		}
		logger.info("Adding resource data to:"+resource.getLabel());
		// update Resource last_posted
		storage.postedResource(resourceId,System.currentTimeMillis());

		// if parsers are undefined, create them!
		List<Parser> parsers = storage.findParsersByResourceId(resourceId);
		if (parsers==null || parsers.size()==0) { 
			logger.info("No parsers defined! Trying to auto create for:"+resource.getLabel());
			try {
				// staticness is a mess...
				parseData.autoCreateJsonParsers(PollSystem.getInstance().mapper,resource,data); 
			} catch (Exception e) {
				logger.error("JSON parsing for auto creation failed!");
				return "Error: JSON parsing for auto creation failed!";
			}
		}
		//run it through the parsers
		applyParsers(resourceId, data);

		return "Success";
	}

	public void applyParsers(long resourceId, String data) {
		//logger.info("Applying all parsers to data: "+data);
		if (parsers==null) { parsers = storage.findParsersByResourceId(resourceId); }
		for (Parser parser: parsers) {
			//logger.info("applying a parser "+parser.getInput_parser());
			try {
				parseData.apply(parser,data);
			} catch (Exception e) {
				logger.error("Parsing "+data+" failed!"+e);
			}
		}
	}

	public void checkHierarchy(long userId) {
		User owner = storage.findUserById(userId);
		if (owner==null) { throw new WebApplicationException(Status.NOT_FOUND); }
	}
 

	// add a resource 
	long insertResource(Resource resource) {
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

	// add a resource 
	void updateResource(long resourceId, Resource newresource) {
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
}
