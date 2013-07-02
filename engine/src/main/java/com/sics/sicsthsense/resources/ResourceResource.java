package com.sics.sicsthsense.resources;

import com.google.common.base.Optional;
import com.yammer.metrics.annotation.Timed;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import java.util.concurrent.atomic.AtomicLong;

import com.sics.sicsthsense.core.*;

@Path("/users/{id}/{resourceId}")
@Produces(MediaType.APPLICATION_JSON)
public class ResourceResource {
    private final AtomicLong counter;

    public ResourceResource() {
        this.counter = new AtomicLong();
    }

		@GET
		@Timed
    public Message getResource(@PathParam("id") String userId, @PathParam("resourceId") String resourceId) {
        return new Message(counter.incrementAndGet(), userId+" "+resourceId);
    }

		@POST
		public void addResource() {
		}

}

