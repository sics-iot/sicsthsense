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
package com.sics.sicsthsense.resources.atmosphere;


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
import javax.ws.rs.core.MediaType;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;

import com.google.common.base.Optional;
import com.yammer.metrics.annotation.Timed;
import com.yammer.dropwizard.auth.Auth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sics.sicsthsense.core.*;
import com.sics.sicsthsense.jdbi.*;
import com.sics.sicsthsense.auth.annotation.RestrictedTo;
import com.sics.sicsthsense.model.security.Authority;

@Path("/users/{userId}/resources/{resourceId}/parsers")
@Produces(MediaType.APPLICATION_JSON)
public class ParserResource {
	private final StorageDAO storage;
  private final AtomicLong counter;
	private final Logger logger = LoggerFactory.getLogger(ParserResource.class);

	public ParserResource() {
		this.storage = DAOFactory.getInstance();
		this.counter = new AtomicLong();
	}

	@GET
	@Timed
	public Message getParsers(@RestrictedTo(Authority.ROLE_PUBLIC) User visitor, @PathParam("userId") long userId, @PathParam("resourceId") long resourceId) {
			return new Message(counter.incrementAndGet(), userId+" "+resourceId+" "+visitor.getUsername());
	}

	@GET
	@Path("/{parserId}")
	@Timed
	public Message getParser( @PathParam("userId") long userId, @PathParam("resourceId") long resourceId, @PathParam("parserId") long parserId) {
		User visitor = new User();
			return new Message(counter.incrementAndGet(), userId+" "+resourceId+" "+visitor.getUsername());
	}

	@POST
	@Consumes({MediaType.APPLICATION_JSON})
	@Timed
	public String postParser(@PathParam("userId") long userId, @PathParam("resourceId") long resourceId,  Parser parser) {
		User visitor = new User();
		insertParser(parser);
		return "";
	}

	// put updated resource definition 
	@PUT
	@Path("/{parserId}")
	@Consumes({MediaType.APPLICATION_JSON})
	@Timed
	public void putParser(@PathParam("userId") long userId, @PathParam("resourceId") long resourceId, @PathParam("parserId") long parserId, Parser parser) {
		User visitor = new User();
		logger.info("Updating parserId:"+parserId);
		if (visitor.getId() != userId) { // only owners
			logger.error("Not allowed to modify parser: "+parserId);
			throw new WebApplicationException(Status.FORBIDDEN);
		}
		updateParser(parserId, parser);
	}

	@DELETE
	@Path("/{parserId}")
	@Timed
	public void deleteParser(@PathParam("userId") long userId, @PathParam("resourceId") long resourceId, @PathParam("parserId") long parserId) {
		User visitor = new User();
		logger.warn("Deleting parserId:"+parserId);
		Parser parser = storage.findParserById(parserId);
		if (parser==null) {
			logger.error("No parser to delete: "+parserId);
			throw new WebApplicationException(Status.FORBIDDEN);
		}
		if (visitor.getId() != userId) { // only owners
			logger.error("Not allowed to delete parser: "+parserId);
			throw new WebApplicationException(Status.FORBIDDEN);
		}
		storage.deleteParser(parserId);
	}

	// add a resource 
	void insertParser(Parser parser) {
		logger.info("Adding "+parser);
		storage.insertParser(
			parser.getResource_id(),
			parser.getStream_id(),
			parser.getInput_parser(),
			parser.getInput_type(),
			parser.getTimeformat(),
			parser.getData_group(),
			parser.getTime_group(),
			1
		);
	}

	// update a resource 
	void updateParser(long parserId, Parser parser) {
		logger.info("Updating parserID "+parserId+" to: "+parser);
		Parser oldParser = storage.findParserById(parserId);
		if (oldParser==null) {
			logger.error("No parser to modify: "+parserId);
			throw new WebApplicationException(Status.FORBIDDEN);
		}

		storage.updateParser(
			parserId,
			parser.getInput_parser(),
			parser.getInput_type(),
			parser.getTimeformat(),
			parser.getData_group(),
			parser.getTime_group(),
			1 // number_of_points
		);
		// remake pollers with updated Resource attribtues
		//pollSystem.rebuildResourcePoller(oldParser.getResource_id());
	}

}
