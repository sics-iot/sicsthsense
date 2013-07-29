package com.sics.sicsthsense;

import java.util.UUID;

import org.skife.jdbi.v2.*; // For DBI
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

import com.sics.sicsthsense.resources.*;
import com.sics.sicsthsense.jdbi.*;
import com.sics.sicsthsense.core.*;
import com.sics.sicsthsense.auth.*;
import com.sics.sicsthsense.auth.openid.*;
import com.sics.sicsthsense.model.security.*;

public class EngineService extends Service<EngineConfiguration> {

	public static void main(String[] args) throws Exception {
		new EngineService().run(args);
	}

	@Override
	public void initialize(Bootstrap<EngineConfiguration> bootstrap) {
		bootstrap.setName("SicsthSense-Engine");
    // Bundles
    bootstrap.addBundle(new AssetsBundle("/assets/images", "/images"));
    bootstrap.addBundle(new AssetsBundle("/assets/jquery", "/jquery"));
    bootstrap.addBundle(new ViewBundle());
	}

	// ClassNotFoundException thrown when missing DBI driver
	@Override
	public void run(EngineConfiguration configuration, Environment environment) throws ClassNotFoundException {
		// register each resource type accessible through the API
		final DBIFactory factory = new DBIFactory();
		final DBI jdbi = factory.build(environment, configuration.getDatabaseConfiguration(), "com.mysql.jdbc.Driver");
		final StorageDAO storage = jdbi.onDemand(StorageDAO.class);

		environment.addProvider(new BasicAuthProvider<User>(new SimpleAuthenticator(), "SUPER SECRET STUFF"));
		//environment.addProvider(new OAuthProvider<User>(new SimpleAuthenticator(), "SUPER SECRET STUFF"));
		//environment.addProvider(new BasicAuthProvider<User>(new OAuthAuthenticator(), "SUPER SECRET STUFF"));

    // Configure authenticator
		OpenIDUser publicUser = new OpenIDUser(null, UUID.randomUUID());
		publicUser.getAuthorities().add(Authority.ROLE_PUBLIC);
    OpenIDAuthenticator authenticator = new OpenIDAuthenticator(publicUser);
    environment.addProvider(new OpenIDRestrictedToProvider<OpenIDUser>(authenticator, "OpenID"));


    // Configure environment
    environment.scanPackagesForResourcesAndProviders(PublicHomeResource.class);

    environment.addProvider(new ViewMessageBodyWriter());

		environment.addResource(new UserResource(storage));
		environment.addResource(new ResourceResource(storage));
		environment.addResource(new StreamResource(storage));
		environment.addResource(new ParserResource(storage));
		environment.addResource(new PublicOpenIDResource());

    // Session handler to enable automatic session handling 
    environment.setSessionHandler(new SessionHandler());
	}

}
