/*
 * Copyright (c) 2013, Swedish Institute of Computer Science
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *		 * Redistributions of source code must retain the above copyright
 *			 notice, this list of conditions and the following disclaimer.
 *		 * Redistributions in binary form must reproduce the above copyright
 *			 notice, this list of conditions and the following disclaimer in the
 *			 documentation and/or other materials provided with the distribution.
 *		 * Neither the name of The Swedish Institute of Computer Science nor the
 *			 names of its contributors may be used to endorse or promote products
 *			 derived from this software without specific prior written permission.
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

import java.util.concurrent.atomic.AtomicLong;
import java.util.Date;

import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.servlet.*;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.QueryParam;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.*;
import javax.ws.rs.WebApplicationException;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.skife.jdbi.v2.*;
import org.skife.jdbi.v2.sqlobject.*;
import com.google.common.base.Optional;
import com.codahale.metrics.annotation.Timed;
import io.dropwizard.jdbi.*;
import io.dropwizard.db.*;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

import se.sics.sicsthsense.*;
import se.sics.sicsthsense.core.*;
import se.sics.sicsthsense.jdbi.*;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResource {
	private StorageDAO storage;
	private final AtomicLong counter;
	private final Logger logger = LoggerFactory.getLogger(UserResource.class);

	public UserResource() {
		this.storage = DAOFactory.getInstance();
		this.counter = new AtomicLong();
		this.storage = storage;
	}

	@GET
	@Timed
	public Response listUsers() {
		logger.warn("Cant list users!");
		return Utils.resp(Status.FORBIDDEN, new JSONMessage("Error: Can not list users"), logger);
	}

	@GET
	@Timed
	@Path("/{userId}")
	public Response getUser(@PathParam("userId") long userId, @QueryParam("key") String key) {
		//System.out.println("getting User!! "+userId);
		User user = storage.findUserById(userId);
		if (!user.isAuthorised(key)) { return Utils.resp(Status.FORBIDDEN, new JSONMessage("Error: Key does not match! "+key), logger); }
		return Utils.resp(Status.OK, user, logger);
	}

	@POST
	@Timed
	public Response post(User user) throws Exception {
		//logger.info("making a new user: "+user.toString());
		if (user.getEmail()==null || user.getEmail()=="")	      { return Utils.resp(Status.BAD_REQUEST, new JSONMessage("Error: new User email not set!"), logger); }
		if (storage.findUserByUsername(user.getUsername())!=null) { return Utils.resp(Status.BAD_REQUEST, new JSONMessage("Error: Duplicate username: "+user.getUsername()+"!"), logger); }
		if (storage.findUserByEmail(user.getEmail())!=null)	      { return Utils.resp(Status.BAD_REQUEST, new JSONMessage("Error: Duplicate email: "+user.getEmail()+"!"), logger); }
		User newuser = new User();

		newuser.update(user);
		logger.info("Adding new user: "+newuser.toString());

		storage.insertUser(
			newuser.getUsername(),
			newuser.getEmail(),
			newuser.getFirstName(),
			newuser.getLastName(),
			newuser.getToken(),
			new String(Hex.encodeHex(DigestUtils.md5(newuser.getPassword())))
		);
		return Utils.resp(Status.OK, storage.findUserByUsername(newuser.getUsername()), logger);
	}

	@PUT
	@Timed
	@Path("/{userId}")
	public Response put(@PathParam("userId") long userId, User newuser, @QueryParam("key") String key) throws Exception {
		// should check permissions...
		User user = storage.findUserById(userId);
		if (!user.getToken().equals(key)) {throw new WebApplicationException(Status.FORBIDDEN);}

		newuser.setId(userId); // ensure we dont change other others!
		user.update(newuser);
		storage.updateUser(
			user.getId(),
			user.getUsername(),
			user.getFirstName(),
			user.getLastName(),
			user.getEmail()
		);
		return Utils.resp(Status.OK, user, logger);
	}
}
