package com.sics.sicsthsense.resources;


import java.util.concurrent.atomic.AtomicLong;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;

import com.google.common.base.Optional;
import com.yammer.metrics.annotation.Timed;
import com.yammer.dropwizard.auth.Auth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sics.sicsthsense.core.*;
import com.sics.sicsthsense.jdbi.*;
import com.sics.sicsthsense.auth.annotation.RestrictedTo;
import com.sics.sicsthsense.model.security.Authority;

@Path("/users/{userId}/resources/{resourceId}/streams/{streamId}/parsers")
@Produces(MediaType.APPLICATION_JSON)
public class ParserResource {
	private final StorageDAO storage;
  private final AtomicLong counter;
	private final Logger logger = LoggerFactory.getLogger(ParserResource.class);

	public ParserResource(StorageDAO storage) {
		this.storage = storage;
		this.counter = new AtomicLong();
	}

	@GET
	@Timed
	public Message getParsers(@RestrictedTo(Authority.ROLE_PUBLIC) User visitor, @PathParam("userId") long userId, @PathParam("resourceId") long resourceId, @PathParam("streamId") long streamId) {
			return new Message(counter.incrementAndGet(), userId+" "+resourceId+" "+streamId+"  "+visitor.getUsername());
	}

	@GET
	@Path("/{parserId}")
	@Timed
	public Message getParser(@RestrictedTo(Authority.ROLE_PUBLIC) User visitor, @PathParam("userId") long userId, @PathParam("resourceId") long resourceId, @PathParam("streamId") long streamId, @PathParam("parserId") long parserId) {
			return new Message(counter.incrementAndGet(), userId+" "+resourceId+" "+streamId+"  "+visitor.getUsername());
	}

	@POST
	public String addResource() {
		return "";
	}

}

