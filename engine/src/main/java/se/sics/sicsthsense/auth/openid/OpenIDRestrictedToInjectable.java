
/* Description:
 * TODO:
 * */
package se.sics.sicsthsense.auth.openid;

import com.google.common.base.Optional;
import com.google.common.collect.Sets;
import com.sun.jersey.api.core.HttpContext;
import com.sun.jersey.server.impl.inject.AbstractHttpContextInjectable;
import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.Response;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import se.sics.sicsthsense.model.security.Authority;
import se.sics.sicsthsense.EngineConfiguration;
// bad behaviour!
import se.sics.sicsthsense.core.User;

/**
 * <p>Injectable to provide the following to {@link OpenIDRestrictedToProvider}:</p>
 * <ul>
 * <li>Performs decode from HTTP request</li>
 * <li>Carries OpenID authentication data</li>
 * </ul>
 *
 * @since 0.0.1
 */
class OpenIDRestrictedToInjectable<T> extends AbstractHttpContextInjectable<T> {

  private static final Logger log = LoggerFactory.getLogger(OpenIDRestrictedToInjectable.class);

  private final Authenticator<OpenIDCredentials, T> authenticator;
  private final String realm;
  private final Set<Authority> requiredAuthorities;

  /**
   * @param authenticator The Authenticator that will compare credentials
   * @param realm The authentication realm
   * @param requiredAuthorities The required authorities as provided by the RestrictedTo annotation
   */
  OpenIDRestrictedToInjectable(
    Authenticator<OpenIDCredentials, T> authenticator,
    String realm,
    Authority[] requiredAuthorities) {
    this.authenticator = authenticator;
    this.realm = realm;
    this.requiredAuthorities = Sets.newHashSet(Arrays.asList(requiredAuthorities));
  }

  public Authenticator<OpenIDCredentials, T> getAuthenticator() {
    return authenticator;
  }

  public String getRealm() {
    return realm;
  }

  public Set<Authority> getRequiredAuthorities() {
    return requiredAuthorities;
  }

  @Override
  public T getValue(HttpContext httpContext) {
      log.warn("get value");

    try {

      // Get the Authorization header
      final Map<String,Cookie> cookieMap = httpContext.getRequest().getCookies();
      if (!cookieMap.containsKey(EngineConfiguration.SESSION_TOKEN_NAME)) {
				log.warn("Error with authenticating from cookie!");
				User publicUser = new User();
				publicUser.getAuthorities().add(Authority.ROLE_PUBLIC);
				Optional<T> opt = Optional.of((T)publicUser);
				return opt.get();
        //throw new WebApplicationException(Response.Status.UNAUTHORIZED);
      }

      UUID sessionToken = UUID.fromString(cookieMap.get(EngineConfiguration.SESSION_TOKEN_NAME).getValue());

      if (sessionToken != null) {

        final OpenIDCredentials credentials = new OpenIDCredentials(sessionToken, requiredAuthorities);

        final Optional<T> result = authenticator.authenticate(credentials);
        if (result.isPresent()) {
          return result.get();
        }
      }
    } catch (IllegalArgumentException e) {
      log.debug("Error decoding credentials",e);
    } catch (AuthenticationException e) {
      log.warn("Error authenticating credentials",e);
      throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
    }

      log.warn("Error with authenticating!");
    // Must have failed to be here
    throw new WebApplicationException(Response.Status.UNAUTHORIZED);
  }

}
