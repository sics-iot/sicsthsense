package com.sics.sicsthsense.auth.openid;

import java.util.UUID;

import com.google.common.base.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yammer.dropwizard.auth.AuthenticationException;
import com.yammer.dropwizard.auth.Authenticator;

import com.sics.sicsthsense.core.InMemoryUserCache;
import com.sics.sicsthsense.core.User;

/**
 * <p>Authenticator to provide the following to application:</p>
 * <ul>
 * <li>Verifies the provided credentials are valid</li>
 * </ul>
 *
 * @since 0.0.1
 */
public class OpenIDAuthenticator implements Authenticator<OpenIDCredentials, User> {
	public  final User publicUser;
	private final Logger logger = LoggerFactory.getLogger(OpenIDAuthenticator.class);

	public OpenIDAuthenticator(User publicUser) {
		this.publicUser = publicUser;
	}

  @Override
  public Optional<User> authenticate(OpenIDCredentials credentials) throws AuthenticationException {
		logger.info("Authenticating user");

    // Get the User referred to by the API key
    Optional<User> user = InMemoryUserCache
      .INSTANCE
      .getBySessionToken(credentials.getSessionToken());
    if (!user.isPresent()) {
			if (!publicUser.hasAllAuthorities(credentials.getRequiredAuthorities())) {
				return Optional.absent();
			}
			return Optional.<User>of(publicUser);
      //return Optional.absent();
    }

    // Check that their authorities match their credentials
    if (!user.get().hasAllAuthorities(credentials.getRequiredAuthorities())) {
      return Optional.absent();
    }
    return user;
  }

}
