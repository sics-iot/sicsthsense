package com.sics.sicsthsense.resources;


import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import java.util.concurrent.atomic.AtomicLong;

import com.google.common.base.Optional;
import com.yammer.metrics.annotation.Timed;
import com.yammer.dropwizard.auth.Auth;

import com.sics.sicsthsense.core.*;
import com.sics.sicsthsense.jdbi.*;
import com.sics.sicsthsense.auth.annotation.RestrictedTo;
import com.sics.sicsthsense.model.security.Authority;

@Path("/users/{userId}/resources/{resourceId}/streams/{streamId}/parsers")
@Produces(MediaType.APPLICATION_JSON)
public class ParserResource {
	private StorageDAO storage;
	private final AtomicLong counter;

	public ParserResource(StorageDAO storage) {
		this.storage = storage;
		this.counter = new AtomicLong();
	}

	@GET
	@Path("/{parserId}")
	@Timed
	public Message getResource(@RestrictedTo(Authority.ROLE_PUBLIC) User visitor, @PathParam("id") long userId, @PathParam("resourceId") long resourceId, @PathParam("streamId") long streamId) {
			return new Message(counter.incrementAndGet(), userId+" "+resourceId+" "+streamId+"  "+visitor.getUserName());
	}

	@POST
	public String addResource() {
		return "";
	}

}

