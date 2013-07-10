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
import com.sics.sicsthsense.jdbi.*;

@Path("/users/{userId}/{resourceId}")
@Produces(MediaType.APPLICATION_JSON)
public class ResourceResource {
	private StorageDAO storage;
	private final AtomicLong counter;

	public ResourceResource(StorageDAO storage) {
		this.storage = storage;
		this.counter = new AtomicLong();
	}

	@GET
	@Timed
	public Resource getResource(@PathParam("userId") String userId, @PathParam("resourceId") String resourceId) {
		//return new Message(counter.incrementAndGet(), userId+" "+resourceId);
		System.out.println("Getting user/resource: "+userId+" "+resourceId);
		Resource resource = storage.findResourceById(Integer.parseInt(resourceId));
		return resource;
	}

	@POST
	@Timed
	public void postResource(@PathParam("userId") String userId, Resource resource) {
		System.out.println("Adding user/resource:"+resource.getLabel());
		insertResource(resource);
	}
	
	void insertResource(Resource resource) {
		storage.insertResource( resource.getOwner_id(), 
	resource.getLabel(),
	resource.getPolling_period(), 
	resource.getLast_polled(), 
	resource.getPolling_url(), 
	resource.getPolling_authentication_key(), 
	resource.getDescription(), 
	resource.getParent_id(), 
	resource.getSecret_key(), 
	resource.getVersion(), 
	resource.getLast_posted() 
	);}
}

