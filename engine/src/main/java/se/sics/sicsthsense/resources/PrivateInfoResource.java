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

/* Description:
 * TODO:
 * */
package se.sics.sicsthsense.resources;

import javax.ws.rs.core.Context;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import io.dropwizard.jersey.caching.CacheControl;
import com.codahale.metrics.annotation.Timed;

import se.sics.sicsthsense.auth.annotation.RestrictedTo;
import se.sics.sicsthsense.model.security.Authority;
import se.sics.sicsthsense.model.BaseModel;
import se.sics.sicsthsense.core.User;
import se.sics.sicsthsense.views.PublicFreemarkerView;


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
