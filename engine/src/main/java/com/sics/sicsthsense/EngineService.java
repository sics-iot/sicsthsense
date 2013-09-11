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

/* Description: The main source file that constructs the rest of the system.
 * All Resources are made and connected here. The authorisation is also set here.
 * TODO:
 * */
package com.sics.sicsthsense;

import java.util.UUID;

import org.eclipse.jetty.server.session.SessionHandler;

import org.skife.jdbi.v2.*; // For DBI
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yammer.dropwizard.Service;
import com.yammer.dropwizard.assets.AssetsBundle;
import com.yammer.dropwizard.config.Bootstrap;
import com.yammer.dropwizard.config.Environment;
import com.yammer.dropwizard.jdbi.*;
import com.yammer.dropwizard.db.*;

import com.yammer.dropwizard.auth.*;
import com.yammer.dropwizard.auth.Authenticator;
import com.yammer.dropwizard.auth.AuthenticationException;
import com.yammer.dropwizard.auth.basic.BasicCredentials;
import com.yammer.dropwizard.auth.basic.BasicAuthProvider;
import com.yammer.dropwizard.auth.oauth.*;
import com.yammer.dropwizard.views.ViewBundle;
import com.yammer.dropwizard.views.ViewMessageBodyWriter;
import com.yammer.dropwizard.jdbi.bundles.DBIExceptionsBundle;

import scala.concurrent.duration.Duration;
import java.util.concurrent.TimeUnit;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.actor.Cancellable;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;

import com.sics.sicsthsense.resources.*;
import com.sics.sicsthsense.jdbi.*;
import com.sics.sicsthsense.core.*;
import com.sics.sicsthsense.auth.*;
import com.sics.sicsthsense.auth.openid.*;
import com.sics.sicsthsense.model.security.*;

public class EngineService extends Service<EngineConfiguration> {
	private final Logger logger = LoggerFactory.getLogger(EngineService.class);
	private PollSystem pollSystem;

	public static void main(String[] args) throws Exception {
		new EngineService().run(args);
	}

	@Override
	public void initialize(Bootstrap<EngineConfiguration> bootstrap) {
		bootstrap.setName("SicsthSense-Engine");
    // Bundles give certain commonly used features
		// Static files:
    bootstrap.addBundle(new AssetsBundle("/assets/images", "/images"));
		// jQuery
    bootstrap.addBundle(new AssetsBundle("/assets/jquery", "/jquery"));
		// help with templates
    bootstrap.addBundle(new ViewBundle());
		// Give pretty error messages when database failures occur
		bootstrap.addBundle(new DBIExceptionsBundle());
	}

	// main method to perform all the heavy lifting
	// ClassNotFoundException thrown when missing DBI driver
	@Override
	public void run(EngineConfiguration configuration, Environment environment) throws ClassNotFoundException {
		// register each resource type accessible through the API
		final DBIFactory factory = new DBIFactory();
		final DBI jdbi = factory.build(environment, configuration.getDatabaseConfiguration(), "com.mysql.jdbc.Driver");
		final StorageDAO storage = jdbi.onDemand(StorageDAO.class);

		// Akka system for automatically polling external resources
		pollSystem = new PollSystem(storage);
		pollSystem.createPollers();

		// Authentication subsystem
		environment.addProvider(new BasicAuthProvider<User>(new SimpleAuthenticator(storage), "Username/Password Authentication"));
		//environment.addProvider(new OAuthProvider<User>(new SimpleAuthenticator(), "SUPER SECRET STUFF"));
		//environment.addProvider(new BasicAuthProvider<User>(new OAuthAuthenticator(), "SUPER SECRET STUFF"));

    // Configure authenticator
		User publicUser = new User(-1, UUID.randomUUID()); // default null user
		publicUser.getAuthorities().add(Authority.ROLE_PUBLIC); // only has PUBLIC role
    OpenIDAuthenticator authenticator = new OpenIDAuthenticator(publicUser);
    environment.addProvider(new OpenIDRestrictedToProvider<User>(authenticator, "OpenID"));

    // Configure environment and resources
    environment.scanPackagesForResourcesAndProviders(PublicHomeResource.class);
    environment.addProvider(new ViewMessageBodyWriter());
		environment.addResource(new UserResource(storage));
		environment.addResource(new ResourceResource(storage, pollSystem));
		environment.addResource(new StreamResource(storage));
		environment.addResource(new ParserResource(storage));
		environment.addResource(new PublicOpenIDResource(storage));

    // Session handler to enable automatic session handling 
    environment.setSessionHandler(new SessionHandler());
	}

}
