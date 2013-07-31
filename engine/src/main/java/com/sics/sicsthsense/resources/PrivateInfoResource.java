package com.sics.sicsthsense.resources;

import javax.ws.rs.core.Context;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import com.yammer.dropwizard.jersey.caching.CacheControl;
import com.yammer.metrics.annotation.Timed;

import com.sics.sicsthsense.auth.annotation.RestrictedTo;
import com.sics.sicsthsense.model.security.Authority;
import com.sics.sicsthsense.model.BaseModel;
import com.sics.sicsthsense.core.User;
import com.sics.sicsthsense.views.PublicFreemarkerView;


/**
 * <p>Resource to provide the following to application:</p>
 * <ul>
 * <li>Provision of configuration for public home page</li>
 * </ul>
 *
 * @since 0.0.1
 */
@Path("/private")
@Produces(MediaType.TEXT_HTML)
public class PrivateInfoResource {
  @Context
  protected HttpHeaders httpHeaders;

  /**
   * @return The private home view if authenticated
   */
  @GET
  @Path("/home")
  @Timed
  @CacheControl(noCache = true)
  public PublicFreemarkerView viewHome( @RestrictedTo(Authority.ROLE_PUBLIC) User publicUser) {
    BaseModel model = new BaseModel();
    return new PublicFreemarkerView<BaseModel>("private/home.ftl", model);
  }

  /**
   * @return The private home view if authenticated
   */
  @GET
  @Path("/dashboard")
  @Timed
  @CacheControl(noCache = true)
  public PublicFreemarkerView viewDashboard( @RestrictedTo(Authority.ROLE_USER) User user) {
    //BaseModel model2 = modelBuilder.newBaseModel(httpHeaders);
    //User user = model.getUser();

		System.out.println("User: "+user.getEmail());

    BaseModel model = new BaseModel();
		model.setUser(user); // make the user available
    return new PublicFreemarkerView<BaseModel>("private/dashboard.ftl", model);
  }

  /**
   * @return The private admin view if authenticated
   */
  @GET
  @Path("/admin")
  @Timed
  @CacheControl(noCache = true)
  public PublicFreemarkerView viewAdmin( @RestrictedTo(Authority.ROLE_ADMIN) User adminUser) {
    BaseModel model = new BaseModel();
    return new PublicFreemarkerView<BaseModel>("private/admin.ftl", model);
  }

}
