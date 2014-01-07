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
import java.util.Collections;
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
import com.yammer.dropwizard.jersey.params.LongParam;

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
	public List<Stream> getStreams(@PathParam("userId") long userId, @QueryParam("token") String token) {
		long resourceId = Long.parseLong(topic.getID());
		logger.info("Getting user/resource/streams "+userId+" "+resourceId);
		checkHierarchy(userId,resourceId);
		User user = storage.findUserById(userId);
		if (!token.equals(user.getToken())) {throw new WebApplicationException(Status.FORBIDDEN);}

		List<Stream> streams = storage.findStreamsByResourceId(resourceId);
		return streams;
	}

	@GET
	@Path("/{streamId}")
	@Timed
	public Stream getStream( @PathParam("userId") long userId, @PathParam("resourceId") long resourceId, @PathParam("streamId") long streamId, @QueryParam("token") String token) {
		logger.info("Getting user/resource/stream: "+userId+"/"+resourceId+"/"+streamId);
		checkHierarchy(userId,resourceId,streamId);
		User user = storage.findUserById(userId);
		if (!token.equals(user.getToken())) {throw new WebApplicationException(Status.FORBIDDEN);}

		Stream stream = storage.findStreamById(streamId);
		return stream;
	}

	@POST
	public long postStream(@PathParam("userId") long userId, @PathParam("resourceId") long resourceId, Stream stream, @QueryParam("token") String token) {
		logger.info("Creating stream!:"+stream.toString());
		checkHierarchy(userId,resourceId);
		User user = storage.findUserById(userId);
		if (!user.getToken().equals(token)) {throw new WebApplicationException(Status.FORBIDDEN);}

		stream.setResource_id(resourceId);
		stream.setOwner_id(userId);
		long streamId = insertStream(stream);
		return streamId;
	}
	void authoriseStreamKey(String key1, String key2) {
	}

	@GET
	@Path("/{streamId}/data")
	@Produces({MediaType.APPLICATION_JSON})
	@Timed
	public List<DataPoint> getData(@PathParam("userId") long userId, @PathParam("resourceId") long resourceId, @PathParam("streamId") long streamId, @QueryParam("limit") @DefaultValue("50") IntParam limit, @QueryParam("from") @DefaultValue("-1") LongParam from, @QueryParam("until") @DefaultValue("-1") LongParam until, @QueryParam("token") String token, @QueryParam("secret_key") String secret_key) {
		checkHierarchy(userId,resourceId,streamId);
		User user = storage.findUserById(userId);
		Stream stream = storage.findStreamById(streamId);
		if (stream==null) {throw new WebApplicationException(Status.NOT_FOUND); }
		if (!stream.getPublic_access() && !stream.getSecret_key().equals(secret_key) && !user.getToken().equals(token)) { 
			logger.warn("User is not owner and has incorrect secret_key on stream!");
			throw new WebApplicationException(Status.FORBIDDEN);
		}

		logger.info("Getting stream: "+streamId);
		if (from.get() != -1) {
			if (until.get() != -1) {
				return storage.findPointsByStreamIdSince(streamId, from.get(), until.get());
			} else {
				return storage.findPointsByStreamIdSince(streamId, from.get());
			}
		} else {
			List<DataPoint> points = storage.findPointsByStreamId(streamId, limit.get());
			Collections.reverse(points);
			return points;
		}
	}

	@POST
	@Broadcast
	@Path("/{streamId}/data")
	@Consumes({MediaType.APPLICATION_JSON})
	@Timed
	public String postData(@PathParam("userId") long userId, @PathParam("resourceId") long resourceId, @PathParam("streamId") long streamId, DataPoint datapoint, @QueryParam("token") String token, @QueryParam("secret_key") String secret_key) {
		checkHierarchy(userId,resourceId,streamId);
		User user = storage.findUserById(userId);
		Stream stream = storage.findStreamById(streamId);
		if (stream==null) {return "Error: Stream does not exist";}
		if (!stream.getSecret_key().equals(secret_key) && !user.getToken().equals(token)) { 
			logger.warn("User is not owner and has incorrect secret_key on resource!");
			throw new WebApplicationException(Status.FORBIDDEN);
		}
		logger.info("Inserting into stream:"+streamId);
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
			//throw new WebApplicationException(Status.NOT_FOUND);
		}
	}
	public void checkHierarchy(long userId, long resourceId, long streamId) {
		checkHierarchy(userId, resourceId);
		Stream stream = storage.findStreamById(streamId);
		if (stream.getResource_id() != resourceId) {
			logger.error("Resource "+resourceId+" does not own stream "+streamId);
			//throw new WebApplicationException(Status.NOT_FOUND);
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

	public static long insertVFile(String path, long owner_id, String type, long stream_id) {
		StorageDAO storage = DAOFactory.getInstance();
		storage.insertVFile( path,owner_id,type,stream_id);
		return -1;
	}


}
