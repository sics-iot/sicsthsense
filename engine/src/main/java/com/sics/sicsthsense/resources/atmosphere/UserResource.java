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
package com.sics.sicsthsense.resources.atmosphere;

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
import javax.ws.rs.core.Response.*;
import javax.ws.rs.WebApplicationException;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.skife.jdbi.v2.*;
import org.skife.jdbi.v2.sqlobject.*;
import com.google.common.base.Optional;
import com.yammer.metrics.annotation.Timed;
import com.yammer.dropwizard.jdbi.*;
import com.yammer.dropwizard.db.*;

import com.sics.sicsthsense.core.*;
import com.sics.sicsthsense.jdbi.*;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResource {
		private StorageDAO storage;
		private final AtomicLong counter;
		private final Logger logger = LoggerFactory.getLogger(UserResource.class);

		public UserResource() {
			this.counter = new AtomicLong();
			storage = DAOFactory.getInstance();
		}

		@GET
		@Timed
		public String listUsers() {
			return "Can not list users...";
		}

		@GET
		@Timed
		@Path("/{userId}")
		public User getUser(@PathParam("userId") long userId, @QueryParam("key") String key) {
			System.out.println("getting User!! "+userId);
			User user = storage.findUserById(userId);
			//System.out.println("key "+key);
			//System.out.println("user key "+user.getToken());
			if (!user.getToken().equals(key)) {throw new WebApplicationException(Status.FORBIDDEN);}
			return user;
		}

		@POST
		@Timed
		public User post(User user) throws Exception {
			logger.info("making a new user: "+user.toString());

			if (user.getEmail()==null || user.getEmail()=="") {
				logger.info("new User email not set!");
				//return "Error: No user 'email' attribute set!";
				throw new WebApplicationException(Status.BAD_REQUEST); 
			}
			if (storage.findUserByUsername(user.getUsername())!=null) {
				String errorMsg ="Error: Duplicate username: "+user.getUsername()+"!";
				logger.error(errorMsg);
				throw new WebApplicationException(Status.BAD_REQUEST); 
			}
			if (storage.findUserByEmail(user.getEmail())!=null) {
				String errorMsg = "Error: Duplicate email: "+user.getEmail()+"!";
				logger.error(errorMsg);
				throw new WebApplicationException(Status.BAD_REQUEST); 
			}
			User newuser = new User();

			newuser.update(user);
			logger.info("Adding new user: "+newuser.toString());

			storage.insertUser(
				newuser.getUsername(),
				newuser.getEmail(),
				newuser.getFirstName(),
				newuser.getLastName(),
				newuser.getToken()
			);
			return storage.findUserByUsername(newuser.getUsername());
		}

		@PUT
		@Timed
		@Path("/{userId}")
		public User put(@PathParam("userId") long userId, User newuser, @QueryParam("key") String key) throws Exception {
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
			return user;
		}

}
