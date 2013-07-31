package com.sics.sicsthsense.auth;

import com.google.common.base.Optional;

import com.yammer.dropwizard.auth.Authenticator;
import com.yammer.dropwizard.auth.AuthenticationException;
import com.yammer.dropwizard.auth.basic.BasicCredentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sics.sicsthsense.core.*;
import com.sics.sicsthsense.jdbi.*;

public class SimpleAuthenticator implements Authenticator<BasicCredentials, User> {
		private final StorageDAO storage;
		private final Logger logger = LoggerFactory.getLogger(SimpleAuthenticator.class);

		public SimpleAuthenticator(StorageDAO storage) {
			this.storage = storage;
		}	

    @Override
    public Optional<User> authenticate(BasicCredentials credentials) throws AuthenticationException {
			logger.info("Authenticating user "+credentials.getUsername());
			User user = storage.findUserByUsername(credentials.getUsername());
			if (user != null) {
				//if ("secret".equals(credentials.getPassword())) {
				if (user.hasPassword(credentials.getPassword())) {
					return Optional.of(user);
				} else {
					logger.error("User "+credentials.getUsername()+" wrong password!");
				}
			} else {
				logger.error("User "+credentials.getUsername()+" doesnt exist!");
			}
			return Optional.absent();
    }
}
