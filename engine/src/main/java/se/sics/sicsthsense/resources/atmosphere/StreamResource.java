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

import org.atmosphere.annotation.Broadcast;
import org.atmosphere.cpr.Broadcaster;
import org.atmosphere.jersey.Broadcastable;
import org.atmosphere.jersey.SuspendResponse;

import java.util.List;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicLong;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.DELETE;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.QueryParam;
import javax.ws.rs.PathParam;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.common.base.Optional;
import com.codahale.metrics.annotation.Timed;
import io.dropwizard.auth.Auth;
import io.dropwizard.jersey.params.IntParam;
import io.dropwizard.jersey.params.LongParam;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import se.sics.sicsthsense.Utils;
import se.sics.sicsthsense.core.*;
import se.sics.sicsthsense.jdbi.*;
import se.sics.sicsthsense.auth.annotation.RestrictedTo;
import se.sics.sicsthsense.model.security.Authority;

@Path("/{userId}/{resources: r[a-z]*}/{resourceId}/{streams: s[a-z]*}") // match r* and s* (for /resources/ and /streams/)
@Produces(MediaType.APPLICATION_JSON)
public class StreamResource {
	private StorageDAO storage;
	private final AtomicLong counter;
	private final Logger logger = LoggerFactory.getLogger(StreamResource.class);
	private ObjectMapper jsonmapper;
//	private CsvMapper csvmapper;
//	private CsvSchema schema;
  private @PathParam("resourceId") Broadcaster topic;

	public StreamResource() {
		this.storage = DAOFactory.getInstance();
		this.counter = new AtomicLong();
		jsonmapper = new ObjectMapper();
//		csvmapper = new CsvMapper();
//		schema = csvmapper.schemaFor(DataPoint.class).withHeader();; // schema from Pojo definition
	}

	@GET
	@Timed
	public Response getStreams(@PathParam("userId") long userId, @PathParam("resourceId") String resourceName, @QueryParam("key") String key) {
		//long resourceId = Long.parseLong(topic.getID());
		logger.info("Getting user/resource/streams "+userId+" "+resourceName);
		User user = storage.findUserById(userId);
		Resource resource = Utils.findResourceByIdName(storage,resourceName);
		Utils.checkHierarchy(storage,user,resource);
		if (!user.isAuthorised(key) && !resource.isAuthorised(key)) {
			return Utils.resp(Status.FORBIDDEN, new JSONMessage("Error: Not authorised to get streams"), logger);
		}

		List<Stream> streams = storage.findStreamsByResourceId(resource.getId());
		for (Stream stream: streams) {
			stream.triggers = storage.findTriggersByStreamId(stream.getId());
			stream.setLabel(storage.findPathByStreamId(stream.getId()));
		}
		//return streams;
		return Utils.resp(Status.OK, streams, logger);
	}

	@GET
	@Path("/{streamId}")
	@Timed
	public Response getStream( @PathParam("userId") long userId, @PathParam("resourceId") String resourceName, @PathParam("streamId") String streamName, @QueryParam("key") @DefaultValue("") String key) {
		logger.info("Getting user/resource/stream: "+userId+"/"+resourceName+"/"+streamName);
		User user = storage.findUserById(userId);
		Resource resource = Utils.findResourceByIdName(storage,resourceName);
		Stream stream     = Utils.findStreamByIdName(storage,streamName);
		Utils.checkHierarchy(storage,user,resource,stream);
		if (!user.isAuthorised(key) && !resource.isAuthorised(key) && !stream.isAuthorised(key)) {
			return Utils.resp(Status.FORBIDDEN, new JSONMessage("Error: Not authorised to get stream"), logger);
		}

		// add back in the antecedents
		List<Long> antecedents = storage.findAntecedents(stream.getId());
		for(Long antId: antecedents) {
			Stream antStream = storage.findStreamById(antId);
			//logger.info("Antecedent: "+antId);
			if (antStream==null) {continue;}
			// Check ability to access antecedent!
			if (antStream.isReadable(storage,key)) {
				stream.antecedents.add(antId);
			}
		}

		// and triggers
		stream.triggers = storage.findTriggersByStreamId(stream.getId());
		stream.setLabel(storage.findPathByStreamId(stream.getId()));

		return Utils.resp(Status.OK, stream, logger);
	}

	/*
	@POST
	public long postStream(@PathParam("userId") long userId, @PathParam("resourceId") long resourceId, Stream stream, @QueryParam("key") String key) {
		logger.info("Creating stream!:"+stream.toString());
		checkHierarchy(userId,resourceId);
		User user = storage.findUserById(userId);
		if (!user.getToken().equals(key)) {throw new WebApplicationException(Status.FORBIDDEN);}

		stream.setResource_id(resourceId);
		stream.setOwner_id(userId);
		long streamId = insertStream(stream);
		return streamId;
	}*/
	@POST
	public Response postStream(@PathParam("userId") long userId, @PathParam("resourceId") String resourceName, Stream stream, @QueryParam("key") String key) throws Exception {
		logger.info("Creating stream!:"+stream);
		User user         = storage.findUserById(userId);
		Resource resource = Utils.findResourceByIdName(storage,resourceName);
		Utils.checkHierarchy(storage,user,resource);
		if (!user.isAuthorised(key) && !resource.isAuthorised(key)) {
			return Utils.resp(Status.FORBIDDEN, new JSONMessage("Error: Not authorised to POST to stream"), logger);
		}
		long streamId=-1;

		// initialise the stream correctly
		stream.setResource_id(resource.getId());
		stream.setOwner_id(userId);
		streamId = Utils.insertStream(storage,stream);

		//create antecedant streams correctly!
		if (stream.antecedents !=null) {
			logger.info("Antecedant streams: ");
			for(Long antId: stream.antecedents) {
				logger.info("Antecedent: "+antId);
				// check ability to access antecedent!
				// XXX
				if (antId==null) {return Utils.resp(Status.BAD_REQUEST, new JSONMessage("Error: Antecedent Stream ID is not valid!"), logger);}
				Utils.insertDependent(storage,antId.longValue(),streamId);
			}
		}
		if (stream.triggers!=null) {
			logger.info("Trigger processing..");
			for(Trigger t: stream.triggers) {
				Utils.insertTrigger(storage, streamId, t.getUrl(), t.getOperator(), t.getOperand(), t.getPayload());
			}
		}

        stream = storage.findStreamById(streamId); // need fresh DB version
		return Utils.resp(Status.OK, stream, logger);
	}

	void authoriseStreamKey(String key1, String key2) {
	}

	@DELETE
	@Path("/{streamId}/{data: d[a-z]*}")
	@Produces({MediaType.APPLICATION_JSON,MediaType.TEXT_PLAIN})
	public Response deleteStream(@PathParam("userId") long userId, @PathParam("resourceId") String resourceName, @PathParam("streamId") String streamName, @QueryParam("key") String key) {
		logger.info("Deleting stream!:"+streamName);
		User user = storage.findUserById(userId);
		Resource resource = Utils.findResourceByIdName(storage,resourceName);
		Stream stream =	    Utils.findStreamByIdName(storage,streamName);
		Utils.checkHierarchy(storage,user,resource);
		if (!user.isAuthorised(key) && !resource.isAuthorised(key) && !stream.isAuthorised(key)) {
			return Utils.resp(Status.FORBIDDEN, new JSONMessage("Error: Not authorised to DELETE stream"), logger);
		}
        Utils.deleteStream(storage,stream);

		return Utils.resp(Status.OK, new JSONMessage("Stream deleted"), null);
    }

	@GET
	@Path("/{streamId}/{data: d[a-z]*}")
	@Produces({MediaType.APPLICATION_JSON,MediaType.TEXT_PLAIN})
	@Timed
	public Response getData(@PathParam("userId") long userId, @PathParam("resourceId") String resourceName, @PathParam("streamId") String streamName, @QueryParam("limit") @DefaultValue("-1") IntParam limit, @QueryParam("from") @DefaultValue("-1") LongParam from, @QueryParam("until") @DefaultValue("-1") LongParam until, @QueryParam("format") @DefaultValue("json") String format, @QueryParam("key") String key) {
		List<DataPoint> rv; // return value before conversion
		User user         = storage.findUserById(userId);
		Resource resource = Utils.findResourceByIdName(storage,resourceName);
		Stream stream     = Utils.findStreamByIdName(storage,streamName);
		Utils.checkHierarchy(storage,user,resource,stream);
		if (!stream.getPublic_access()) { // need to authenticate
			//logger.warn("Stream isnt public access!");
			if (!user.isAuthorised(key) && !stream.isAuthorised(key) && !resource.isAuthorised(key)) {
				return Utils.resp(Status.FORBIDDEN, new JSONMessage("Error: Not authorised to POST to stream"), logger);
			}
		}
		//logger.info("Getting stream: "+streamId);
		boolean limitSet=true;
		int limitValue = limit.get();
		if (limit.get()==-1) {
			limitSet=false;
			limitValue=50; // give default value
		}


		if (from.get() != -1) { // from is set
			if (until.get() != -1) { // until is set
				rv = storage.findPointsByStreamIdSince(stream.getId(), from.get(), until.get());
			} else { // until is not set
				if (limitSet) { // limit was set
					rv = storage.findPointsByStreamIdSinceLimit(stream.getId(), from.get(), limitValue);
				} else { // limit was not set, only from
					rv = storage.findPointsByStreamIdSince(stream.getId(), from.get());
				}
			}
		} else { // just get the most recent LIMIT points
			rv = storage.findPointsByStreamId(stream.getId(), limitValue);
			Collections.reverse(rv);
		}

		try {
			if ("csv".equals(format)) {
				//return Utils.resp(Status.OK, csvmapper.writer(schema).writeValueAsString(rv), null);
				return Utils.resp(Status.BAD_REQUEST, new JSONMessage("Error: Can't parse data!"), logger);
			} else { // default dump to JSON
				return Utils.resp(Status.OK, jsonmapper.writeValueAsString(rv), null);
			}
		} catch (Exception e) {
				return Utils.resp(Status.BAD_REQUEST, new JSONMessage("Error: Can't parse data!"), logger);
		}
	}

	@POST
	@Broadcast
	@Path("/{streamId}/{data: d[a-z]*}")
	@Consumes({MediaType.APPLICATION_JSON})
	@Timed
	public Response postData(@PathParam("userId") long userId, @PathParam("resourceId") String resourceName, @PathParam("streamId") String streamName, DataPoint datapoint, @QueryParam("key") String key) throws Exception {
		User user         = storage.findUserById(userId);
		Resource resource = Utils.findResourceByIdName(storage,resourceName);
		Stream stream     = Utils.findStreamByIdName(storage,streamName);
		Utils.checkHierarchy(storage, user, resource, stream);
		if (!user.isAuthorised(key) && !resource.isAuthorised(key) && !stream.isAuthorised(key)) {
			return Utils.resp(Status.FORBIDDEN, new JSONMessage("User is not owner and has incorrect key on resource/stream!"), logger);
		}
		logger.info("Inserting data into stream: "+streamName);
		datapoint.setStreamId(stream.getId()); // keep consistency
		Utils.insertDataPoint(storage, datapoint); // insert first to fail early
		topic.broadcast(datapoint.toString());

		return Utils.resp(Status.OK, new JSONMessage("Data successfully posted"), null);
	}


}
