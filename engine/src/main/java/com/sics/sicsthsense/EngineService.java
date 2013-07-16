package com.sics.sicsthsense;

import org.skife.jdbi.v2.*; // For DBI

import com.yammer.dropwizard.Service;
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

import com.sics.sicsthsense.resources.*;
import com.sics.sicsthsense.jdbi.*;
import com.sics.sicsthsense.core.*;
import com.sics.sicsthsense.auth.*;

public class EngineService extends Service<EngineConfiguration> {

	public static void main(String[] args) throws Exception {
		new EngineService().run(args);
	}

	@Override
	public void initialize(Bootstrap<EngineConfiguration> bootstrap) {
		bootstrap.setName("SicsthSense-Engine");
		//bootstrap.addBundle(new AssetsBundle("/assets/", "/"));
	}

	// ClassNotFoundException thrown when missing DBI driver
	@Override
	public void run(EngineConfiguration configuration, Environment environment) throws ClassNotFoundException {
		// register each resource type accessible through the API
		final DBIFactory factory = new DBIFactory();
		final DBI jdbi = factory.build(environment, configuration.getDatabaseConfiguration(), "com.mysql.jdbc.Driver");
		final StorageDAO storage = jdbi.onDemand(StorageDAO.class);

		//environment.addProvider(new BasicAuthProvider<User>(new SimpleAuthenticator(), "SUPER SECRET STUFF"));
		//environment.addProvider(new OAuthProvider<User>(new SimpleAuthenticator(), "SUPER SECRET STUFF"));
		environment.addResource(new UserResource(storage));
		environment.addResource(new ResourceResource(storage));
		environment.addResource(new StreamResource(storage));
		environment.addResource(new ParserResource(storage));
	}

}
