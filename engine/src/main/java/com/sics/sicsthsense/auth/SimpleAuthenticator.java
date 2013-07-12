package com.sics.sicsthsense.auth;

import com.yammer.dropwizard.auth.Authenticator;
import com.yammer.dropwizard.auth.AuthenticationException;
import com.yammer.dropwizard.auth.basic.BasicCredentials;

import com.google.common.base.Optional;

import com.sics.sicsthsense.core.*;

public class SimpleAuthenticator implements Authenticator<BasicCredentials, User> {
    @Override
    public Optional<User> authenticate(BasicCredentials credentials) throws AuthenticationException {
        if ("secret".equals(credentials.getPassword())) {
            //return Optional.of(new User(credentials.getUsername()));
            return Optional.of(new User());
        }
        return Optional.absent();
    }
}
