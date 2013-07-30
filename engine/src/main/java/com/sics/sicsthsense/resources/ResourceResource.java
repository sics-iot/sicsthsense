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

import com.google.common.base.Optional;
import com.yammer.metrics.annotation.Timed;
import com.yammer.dropwizard.auth.Auth;

import com.sics.sicsthsense.core.*;
import com.sics.sicsthsense.jdbi.*;
import com.sics.sicsthsense.auth.*;
import com.sics.sicsthsense.auth.annotation.RestrictedTo;
import com.sics.sicsthsense.model.security.Authority;

@Path("/users/{userId}/resources")
@Produces(MediaType.APPLICATION_JSON)
public class ResourceResource {
	private StorageDAO storage;
	private final AtomicLong counter;

	public ResourceResource(StorageDAO storage) {
		this.storage = storage;
		this.counter = new AtomicLong();
	}

	@GET
	@Produces({MediaType.APPLICATION_JSON})
	@Timed
	public List<Resource> getResources(@RestrictedTo(Authority.ROLE_PUBLIC) User visitor, @PathParam("userId") String userId) {
		System.out.println("Getting all user "+userId+" resources");
		System.out.println("For visitor  "+visitor.toString());

		List<Resource> resources = storage.findResourcesByOwnerId(Integer.parseInt(userId));
		return resources;
	}

	@GET
	@Path("/{resourceId}")
	@Produces({MediaType.APPLICATION_JSON})
	@Timed
	public Resource getResource(@RestrictedTo(Authority.ROLE_PUBLIC) User visitor, @PathParam("userId") String userId, @PathParam("resourceId") String resourceId) {
		//return new Message(counter.incrementAndGet(), userId+" "+resourceId);
		System.out.println("Getting user/resource: "+userId+" "+resourceId);

		Resource resource = storage.findResourceById(Integer.parseInt(resourceId));
		if (!storage.authorised(visitor,resource)) {
			throw new WebApplicationException(Status.FORBIDDEN);
		}
		return resource;
	}

	@POST
	@Timed
	public void postResource(@RestrictedTo(Authority.ROLE_USER) User visitor, @PathParam("userId") String userId, Resource resource) {
		System.out.println("Adding user/resource:"+resource.getLabel());
		insertResource(resource);
	}
	
	void insertResource(Resource resource) {
		storage.insertResource( 
	resource.getOwner_id(), 
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

