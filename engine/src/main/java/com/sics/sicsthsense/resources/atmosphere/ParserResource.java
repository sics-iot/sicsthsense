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
package se.sics.sicsthsense.resources.atmosphere;


import java.util.concurrent.atomic.AtomicLong;
import java.util.List;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.DELETE;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.QueryParam;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.google.common.base.Optional;
import com.codahale.metrics.annotation.Timed;
import io.dropwizard.auth.Auth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.sics.sicsthsense.*;
import se.sics.sicsthsense.core.*;
import se.sics.sicsthsense.jdbi.*;
import se.sics.sicsthsense.auth.annotation.RestrictedTo;
import se.sics.sicsthsense.model.security.Authority;

@Path("/{userId}/resources/{resourceId}/parsers")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
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
	public Response getParsers(//@RestrictedTo(Authority.ROLE_PUBLIC) User visitor,
		@PathParam("userId") long userId, @PathParam("resourceId") long resourceId, @QueryParam("key") @DefaultValue("") String key) {
			//return new Message(counter.incrementAndGet(), userId+" "+resourceId+" "+visitor.getUsername());
			checkHierarchy(userId);
			List<Parser> parsers = storage.findParsersByResourceId(resourceId);
			return Utils.resp(Status.OK, parsers, logger);
	}

	@GET
	@Path("/{parserId}")
	@Timed
	public Response getParser( @PathParam("userId") long userId, @PathParam("resourceId") long resourceId, @PathParam("parserId") long parserId, @QueryParam("key") @DefaultValue("") String key) {
		User visitor = new User();
		checkHierarchy(userId);
		Parser parser = storage.findParserById(parserId);
		return Utils.resp(Status.OK, parser, logger);
	}

	@POST
	@Consumes({MediaType.APPLICATION_JSON})
	@Timed
	public Response postParser(@PathParam("userId") long userId, @PathParam("resourceId") long resourceId,  Parser parser, @QueryParam("key") @DefaultValue("") String key) {
		logger.info("Creating parser!:"+parser.toString());
		checkHierarchy(userId,resourceId);
		User visitor = new User();
		if (visitor.getId() != userId) { // only owners
			logger.error("Not allowed to post parser");
			//throw new WebApplicationException(Status.FORBIDDEN);
		}
		parser.setResource_id(resourceId);
		long parserId = insertParser(storage, parser);
		return Utils.resp(Status.OK, parserId, logger);
	}

	// put updated resource definition
	@PUT
	@Path("/{parserId}")
	@Consumes({MediaType.APPLICATION_JSON})
	@Timed
	public Response putParser(@PathParam("userId") long userId, @PathParam("resourceId") long resourceId, @PathParam("parserId") long parserId, Parser parser, @QueryParam("key") @DefaultValue("") String key) {
		User visitor = new User();
		logger.info("Updating parserId:"+parserId);
		checkHierarchy(userId,resourceId);
		if (visitor.getId() != userId) { // only owners
			return Utils.resp(Status.FORBIDDEN, new JSONMessage("Not allowed to modify parser: "+parserId), logger);
		}
		parser.setResource_id(resourceId);
		updateParser(parserId, parser);
		return Utils.resp(Status.OK, new JSONMessage("Updated Parser"), logger);
	}

	@DELETE
	@Path("/{parserId}")
	@Timed
	public Response deleteParser(@PathParam("userId") long userId, @PathParam("resourceId") long resourceId, @PathParam("parserId") long parserId, @QueryParam("key") @DefaultValue("") String key) {
		User visitor = new User();
		logger.warn("Deleting parserId:"+parserId);
		checkHierarchy(userId,resourceId);
		Parser parser = storage.findParserById(parserId);
		if (parser==null) { return Utils.resp(Status.NOT_FOUND, new JSONMessage("No parser to delete: "+parserId), logger); }
		if (visitor.getId() != userId) { return Utils.resp(Status.FORBIDDEN, new JSONMessage("Error: Not authorised to delete parser: "+parserId), logger); }
		storage.deleteParser(parserId);
		return Utils.resp(Status.OK, new JSONMessage("Deleted Parser"), logger);
	}

	public void checkHierarchy(long userId) {
		User owner = storage.findUserById(userId);
		if (owner==null) { throw new WebApplicationException(Status.NOT_FOUND); }
	}
	public void checkHierarchy(long userId, long resourceId) {
		checkHierarchy(userId);
		Resource resource = storage.findResourceById(resourceId);
		if (resource==null) { throw new WebApplicationException(Status.NOT_FOUND); }
	}

	// add a resource
	// static to allow external ParseData to insertParser() as well
	public static long insertParser(StorageDAO storage, Parser parser) {
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
		// XXX bad behaviour! Parsers are not unique to resourceid&streamid
		return storage.findParserId(parser.getResource_id(), parser.getStream_id()); // return the new parser ID
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
