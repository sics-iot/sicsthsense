package com.sics.sicsthsense;

import org.skife.jdbi.v2.*; // For DBI

import com.yammer.dropwizard.Service;
import com.yammer.dropwizard.config.Bootstrap;
import com.yammer.dropwizard.config.Environment;
import com.yammer.dropwizard.jdbi.*;
import com.yammer.dropwizard.db.*;

import com.sics.sicsthsense.resources.*;
import com.sics.sicsthsense.jdbi.*;
import com.sics.sicsthsense.core.*;

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

		User user = storage.findUserById(1);
		System.out.println(user.getUsername());

		environment.addResource(new UserResource(storage));
		environment.addResource(new ResourceResource());
		environment.addResource(new StreamResource());
	}

}
