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
package se.sics.sicsthsense;

import java.util.UUID;

import org.skife.jdbi.v2.*; // For DBI
import org.skife.jdbi.v2.exceptions.*; // For lack of connection Exception
import org.eclipse.jetty.server.session.SessionHandler;

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

import org.atmosphere.cpr.AtmosphereServlet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import akka.actor.Props;
import scala.concurrent.duration.Duration;
import java.util.concurrent.TimeUnit;
import akka.actor.UntypedActor;
import akka.actor.Cancellable;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;

import se.sics.sicsthsense.resources.*;
import se.sics.sicsthsense.jdbi.*;
import se.sics.sicsthsense.core.*;
import se.sics.sicsthsense.auth.*;
import se.sics.sicsthsense.auth.openid.*;
import se.sics.sicsthsense.model.security.*;

public class EngineService extends Service<EngineConfiguration> {
	private final Logger logger = LoggerFactory.getLogger(EngineService.class);
	private PollSystem pollSystem;

	public static void main(String[] args) throws Exception {
		new EngineService().run(args);
	}

	@Override
	public void initialize(Bootstrap<EngineConfiguration> bootstrap) {
		bootstrap.setName("SicsthSense-Engine");
    // Bundles
    bootstrap.addBundle(new AssetsBundle("/assets/images", "/images"));
    bootstrap.addBundle(new AssetsBundle("/assets/jquery", "/jquery"));
    bootstrap.addBundle(new AssetsBundle("/assets/atmos", "/atmos"));
		bootstrap.addBundle(new AssetsBundle("/assets/", "/"));

    bootstrap.addBundle(new ViewBundle());
		bootstrap.addBundle(new DBIExceptionsBundle());
	}

	public void addServlet(Environment environment) {
		AtmosphereServlet atmosphereServlet = new AtmosphereServlet();
		atmosphereServlet.framework().addInitParameter( "com.sun.jersey.config.property.packages", "se.sics.sicsthsense.resources.atmosphere");
		atmosphereServlet.framework().addInitParameter( "org.atmosphere.cpr.broadcasterCacheClass", "org.atmosphere.cache.UUIDBroadcasterCache");
		atmosphereServlet.framework().addInitParameter( "org.atmosphere.cpr.broadcastFilterClasses", "org.atmosphere.client.TrackMessageSizeFilter");
		atmosphereServlet.framework().addInitParameter( "org.atmosphere.client.TrackMessageSizeFilter", "org.atmosphere.container.Tomcat7Servlet30SupportWithWebSocket");
		atmosphereServlet.framework().addInitParameter( "org.atmosphere.websocket.messageContentType", "application/json");
	//	atmosphereServlet.framework().addInitParameter( "org.atmosphere.websocket.messageContentType", "true");
		//atmosphereServlet.framework().addInitParameter( "org.atmosphere.plugin.xmpp.XMPPBroadcaster.authorization", "admin@example.com:password");
		//atmosphereServlet.framework().addInitParameter( "org.atmosphere.plugin.xmpp.XMPPBroadcaster.server", "http://localhost");
		//atmosphereServlet.framework().addInitParameter( "com.sun.jersey.config.feature.Trace", "true");

		environment.addServlet(atmosphereServlet, "/users/*");
		environment.addServlet(atmosphereServlet, "/u/*");
	}

	// ClassNotFoundException thrown when missing DBI driver
	@Override
	public void run(EngineConfiguration configuration, Environment environment) throws ClassNotFoundException {
		// register each resource type accessible through the API
		DAOFactory.build(configuration, environment);
		StorageDAO storage = DAOFactory.getInstance();
		//if (storage==null) {System.out.println("No Storage engine!");}
		pollSystem = PollSystem.build(storage);
		try {
			pollSystem.createPollers();
		} catch (UnableToObtainConnectionException e) {
			System.out.println("Error: Unable to obtain connection to SQL Server!\nExiting...");
			System.exit(1);
		}
		environment.addProvider(new BasicAuthProvider<User>(new SimpleAuthenticator(storage), "Username/Password Authentication"));
		//environment.addProvider(new OAuthProvider<User>(new SimpleAuthenticator(), "SUPER SECRET STUFF"));
		//environment.addProvider(new BasicAuthProvider<User>(new OAuthAuthenticator(), "SUPER SECRET STUFF"));

    // Configure authenticator
		User publicUser = new User();
		publicUser.setUsername("__publicUser");
		publicUser.getAuthorities().add(Authority.ROLE_PUBLIC);
    OpenIDAuthenticator authenticator = new OpenIDAuthenticator(publicUser);
    environment.addProvider(new OpenIDRestrictedToProvider<User>(authenticator, "OpenID"));

		environment.addResource(new PublicHomeResource());
    // Attach Atmosphere servlet
		addServlet(environment);

    // Session handler to enable automatic session handling 
    environment.setSessionHandler(new SessionHandler());
	}

}
