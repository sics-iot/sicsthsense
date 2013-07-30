package com.sics.sicsthsense.resources;

import com.google.common.base.Optional;
import com.yammer.metrics.annotation.Timed;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.QueryParam;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.*;
import javax.validation.Valid;

import java.util.concurrent.atomic.AtomicLong;

import org.skife.jdbi.v2.*;
import org.skife.jdbi.v2.sqlobject.*;

import com.sics.sicsthsense.core.*;
import com.sics.sicsthsense.jdbi.*;

@Path("/users/{userId}")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResource {
		private StorageDAO storage;
    private final AtomicLong counter;

    public UserResource(StorageDAO storage) {
			this.storage = storage;
			this.counter = new AtomicLong();
    }

		@GET
		@Timed
    public User getUser(@PathParam("userId") String userId) {
			System.out.println("getting User!! "+userId);
			User user = storage.findUserById(Integer.parseInt(userId));
			return user;
    }

		@POST
		@Timed
		public Response post(@PathParam("userId") String userId, User user) {
			//final long id = store.add(userId.get(), notification);
     	//return Response.created(UriBuilder.fromResource(NotificationResource.class).build(userId.get(), id).build();
			return Response.status(201).entity("posted alright").build();
		}

}

