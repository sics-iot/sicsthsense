package com.sics.sicsthsense.resources;

import java.util.List;
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

import com.sics.sicsthsense.core.*;
import com.sics.sicsthsense.jdbi.*;
import com.sics.sicsthsense.auth.annotation.RestrictedTo;
import com.sics.sicsthsense.model.security.Authority;

@Path("/users/{userId}/resources/{resourceId}/streams")
@Produces(MediaType.APPLICATION_JSON)
public class StreamResource {
	private StorageDAO storage;
  private final AtomicLong counter;

	public StreamResource(StorageDAO storage) {
		this.storage = storage;
		this.counter = new AtomicLong();
	}

	@GET
	@Timed
	public List<Stream> getStreams(@RestrictedTo(Authority.ROLE_PUBLIC) User visitor, @PathParam("id") long userId, @PathParam("resourceId") long resourceId) {
			//return new Message(counter.incrementAndGet(), userId+" "+resourceId+" "+streamId);
			System.out.println("Getting user/resource/streams "+userId+" "+resourceId);
			List<Stream> streams = storage.findStreamsByResourceId(resourceId);
			return streams;
	}

	@GET
	@Path("/{streamId}")
	@Timed
	public Stream getStream(@RestrictedTo(Authority.ROLE_PUBLIC) User visitor, @PathParam("id") long userId, @PathParam("resourceId") long resourceId, @PathParam("streamId") long streamId) {
			//return new Message(counter.incrementAndGet(), userId+" "+resourceId+" "+streamId);
			System.out.println("Getting user/resource/stream: "+userId+" "+resourceId+" "+streamId);
			Stream stream = storage.findStreamById(streamId);
			return stream;
	}

	@POST
	public String postStream() {
		return "posted";
	}

}
