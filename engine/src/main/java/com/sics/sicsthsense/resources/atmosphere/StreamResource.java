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

import org.atmosphere.annotation.Broadcast;
import org.atmosphere.cpr.Broadcaster;
import org.atmosphere.jersey.Broadcastable;
import org.atmosphere.jersey.SuspendResponse;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.QueryParam;
import javax.ws.rs.PathParam;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.common.base.Optional;
import com.yammer.metrics.annotation.Timed;
import com.yammer.dropwizard.auth.Auth;
import com.yammer.dropwizard.jersey.params.IntParam;

import com.sics.sicsthsense.core.*;
import com.sics.sicsthsense.jdbi.*;
import com.sics.sicsthsense.auth.annotation.RestrictedTo;
import com.sics.sicsthsense.model.security.Authority;

@Path("/{userId}/resources/{resourceId}/streams")
@Produces(MediaType.APPLICATION_JSON)
public class StreamResource {
	private final StorageDAO storage;
  private final AtomicLong counter;
	private final Logger logger = LoggerFactory.getLogger(StreamResource.class);
  private @PathParam("resourceId") Broadcaster topic;

	public StreamResource() {
		this.storage = DAOFactory.getInstance();
		this.counter = new AtomicLong();
	}

	@GET
	@Timed
	public List<Stream> getStreams(//@RestrictedTo(Authority.ROLE_PUBLIC) User visitor, 
					@PathParam("userId") long userId //, @PathParam("resourceId") long resourceId
					) {
		User visitor = new User();
		long resourceId = Long.parseLong(topic.getID());
			logger.info("Getting user/resource/streams "+userId+" "+resourceId);
			checkHierarchy(userId,resourceId);
			List<Stream> streams = storage.findStreamsByResourceId(resourceId);
			return streams;
	}

	@GET
	@Path("/{streamId}")
	@Timed
	public Stream getStream(//@RestrictedTo(Authority.ROLE_PUBLIC) User visitor, 
					@PathParam("userId") long userId, @PathParam("resourceId") long resourceId, @PathParam("streamId") long streamId) {
		User visitor = new User();
			logger.info("Getting user/resource/stream: "+userId+"/"+resourceId+"/"+streamId+" for "+visitor.getId());
			checkHierarchy(userId,resourceId,streamId);
			Stream stream = storage.findStreamById(streamId);
			return stream;
	}

	@GET
	@Path("/{streamId}/data")
	@Produces({MediaType.APPLICATION_JSON})
	@Timed
	public List<DataPoint> getData(
			//@RestrictedTo(Authority.ROLE_PUBLIC) User visitor, 
			@PathParam("userId") long userId, @PathParam("resourceId") long resourceId, @PathParam("streamId") long streamId, @QueryParam("limit") @DefaultValue("10") IntParam limit) {
		User visitor = new User();
		logger.info("Getting stream: "+streamId);
		//Stream stream = storage.findStreamById(streamId);
/*		if (visitor.getId() != userId) {
			throw new WebApplicationException(Status.FORBIDDEN);
		}*/
		return storage.findPointsByStreamId(streamId,limit.get());
	}

	@POST
	@Broadcast
	@Path("/{streamId}/data")
	@Consumes({MediaType.APPLICATION_JSON})
	@Timed
	public String postData( @PathParam("userId") long userId, @PathParam("resourceId") long resourceId, @PathParam("streamId") long streamId, DataPoint datapoint) {
		User visitor = new User();
		logger.info("Inserting into stream:"+streamId);
		Stream stream = storage.findStreamById(streamId);
/*		if (visitor.getId() != userId) {
			throw new WebApplicationException(Status.FORBIDDEN);
		}*/
		if (stream==null) {return "no stream";}
		datapoint.setStreamId(streamId); // keep consistency
		insertDataPoint(datapoint); // insert first to fail early
		topic.broadcast(datapoint.toString());
		return "Posted successfully!";
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

	void insertDataPoint(DataPoint datapoint) {
		if (datapoint.getTimestamp()<=0) {
			datapoint.setTimestamp(java.lang.System.currentTimeMillis());
		}
		storage.insertDataPoint(
			datapoint.getStreamId(),
			datapoint.getValue(),
			datapoint.getTimestamp()
		);
	}


	@POST
	public long postStream(@PathParam("userId") long userId, @PathParam("resourceId") long resourceId, Stream stream) {
		logger.info("Creating stream!:"+stream.toString());
		User visitor = new User();
		if (visitor.getId() != userId) { // only owners
			logger.error("Not allowed to post stream");
			//throw new WebApplicationException(Status.FORBIDDEN);
		}
		stream.setResource_id(resourceId);
		stream.setOwner_id(userId);
		long streamId = insertStream(stream);
		return streamId;
	}

	public static long insertStream(Stream stream) {
		StorageDAO storage = DAOFactory.getInstance();
		storage.insertStream(
			stream.getType(),
			stream.getLatitude(),
			stream.getLongitude(),
			stream.getDescription(),
			stream.getPublic_access(),
			stream.getPublic_search(),
			stream.getFrozen(),
			stream.getHistory_size(),
			stream.getLast_updated(),
			stream.getSecret_key(),
			stream.getOwner_id(),
			stream.getResource_id(),
			stream.getVersion()
		);
		return storage.findStreamId(stream.getResource_id(), stream.getSecret_key());
	}


}
