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
	public List<Stream> getStreams(@PathParam("id") String userId, @PathParam("resourceId") String resourceId, @Auth User user) {
			//return new Message(counter.incrementAndGet(), userId+" "+resourceId+" "+streamId);
			System.out.println("Getting user/resource/streams "+userId+" "+resourceId);
			List<Stream> streams = storage.findStreamsByResourceId(Integer.parseInt(resourceId));
			return streams;
	}

	@GET
	@Path("/{streamId}")
	@Timed
	public Stream getStream(@PathParam("id") String userId, @PathParam("resourceId") String resourceId, @PathParam("streamId") String streamId, @Auth User user) {
			//return new Message(counter.incrementAndGet(), userId+" "+resourceId+" "+streamId);
			System.out.println("Getting user/resource/stream: "+userId+" "+resourceId+" "+streamId);
			Stream stream = storage.findStreamById(Integer.parseInt(streamId));
			return stream;
	}

	@POST
	public String postStream() {
		return "posted";
	}

}

