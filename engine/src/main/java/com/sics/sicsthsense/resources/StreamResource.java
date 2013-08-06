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
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
	private final StorageDAO storage;
  private final AtomicLong counter;
	private final Logger logger = LoggerFactory.getLogger(StreamResource.class);

	public StreamResource(StorageDAO storage) {
		this.storage = storage;
		this.counter = new AtomicLong();
	}

	public void checkHierarchy(long userId, long resourceId) {
			Resource resource = storage.findResourceById(resourceId);
			if (resource == null) {
				logger.error("Resource "+resourceId+" does not exist!");
				throw new WebApplicationException(Status.NOT_FOUND);
			}
			if (resource.getOwner_id() != userId) {
				logger.error("User "+userId+" does not own resource "+resourceId);
				throw new WebApplicationException(Status.NOT_FOUND);
			}
	}
	public void checkHierarchy(long userId, long resourceId, long streamId) {
			Resource resource = storage.findResourceById(resourceId);
			if (resource == null) {
				logger.error("Resource "+resourceId+" does not exist!");
				throw new WebApplicationException(Status.NOT_FOUND);
			}
			if (resource.getOwner_id() != userId) {
				logger.error("User "+userId+" does not own resource "+resourceId);
				throw new WebApplicationException(Status.NOT_FOUND);
			}
			Stream stream = storage.findStreamById(streamId);
			if (stream.getResource_id() != resourceId) {
				logger.error("Resource "+resourceId+" does not own stream "+streamId);
				throw new WebApplicationException(Status.NOT_FOUND);
			}
	}

	@GET
	@Timed
	public List<Stream> getStreams(@RestrictedTo(Authority.ROLE_PUBLIC) User visitor, @PathParam("userId") long userId, @PathParam("resourceId") long resourceId) {
			logger.info("Getting user/resource/streams "+userId+" "+resourceId);
			checkHierarchy(userId,resourceId);
			List<Stream> streams = storage.findStreamsByResourceId(resourceId);
			return streams;
	}

	@GET
	@Path("/{streamId}")
	@Timed
	public Stream getStream(@RestrictedTo(Authority.ROLE_PUBLIC) User visitor, @PathParam("userId") long userId, @PathParam("resourceId") long resourceId, @PathParam("streamId") long streamId) {
			logger.info("Getting user/resource/stream: "+userId+"/"+resourceId+"/"+streamId+" for "+visitor.getId());
			checkHierarchy(userId,resourceId,streamId);
			Stream stream = storage.findStreamById(streamId);
			return stream;
	}

	@GET
	@Path("/{streamId}/data")
	@Produces({MediaType.APPLICATION_JSON})
	@Timed
	public DataPoint getData(@RestrictedTo(Authority.ROLE_PUBLIC) User visitor, @PathParam("userId") long userId, @PathParam("resourceId") long resourceId, @PathParam("streamId") long streamId) {
		logger.info("Getting stream:"+streamId);
		//Stream stream = storage.findStreamById(streamId);
/*		if (visitor.getId() != userId) {
			throw new WebApplicationException(Status.FORBIDDEN);
		}*/
		//return new DataPoint(-1);
		return storage.findPointByStreamId(streamId);
	}


	@POST
	public String postStream() {
		return "posted";
	}

}
