
/* Description: Simple error template resource
 * TODO:
 * */
package se.sics.sicsthsense.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import io.dropwizard.jersey.caching.CacheControl;
import com.codahale.metrics.annotation.Timed;

import se.sics.sicsthsense.model.BaseModel;
import se.sics.sicsthsense.views.PublicFreemarkerView;

/**
 * <p>Resource to provide the following to application:</p>
 * <ul>
 * <li>Provision of error pages</li>
 * </ul>
 *
 * @since 0.0.1
 */
@Path("/error")
@Produces(MediaType.TEXT_HTML)
public class PublicErrorResource {

  /**
   * Provide the 401 Unauthorized page
   *
   * @return A localised view containing HTML
   */
  @GET
  @Path("/401")
  @Timed
  @CacheControl(noCache = true)
  public PublicFreemarkerView view401() {
    // Populate the model
    BaseModel model = new BaseModel();
    return new PublicFreemarkerView<BaseModel>("error/401.ftl",model);
  }

  /**
   * Provide the 404 Not Found page
   *
   * @return A localised view containing HTML
   */
  @GET
  @Path("/404")
  @Timed
  @CacheControl(noCache = true)
  public PublicFreemarkerView view404() {
    // Populate the model
    BaseModel model = new BaseModel();
    return new PublicFreemarkerView<BaseModel>("error/404.ftl",model);
  }

  /**
   * Provide the 500 Internal Server Error page
   *
   * @return A localised view containing HTML
   */
  @GET
  @Path("/500")
  @Timed
  @CacheControl(noCache = true)
  public PublicFreemarkerView view500() {
    // Populate the model
    BaseModel model = new BaseModel();
    return new PublicFreemarkerView<BaseModel>("error/500.ftl",model);
  }
}
