package controllers;

//import models.Project;
//import models.Task;
//import models.User;
import java.util.HashMap;
import java.util.List;

import org.codehaus.jackson.JsonNode;

import com.ning.http.client.Request;

import play.*;

import play.core.Router.Routes;
import play.libs.F.*;
import play.libs.*;
import play.mvc.*;
import play.mvc.Http.Context;
import play.mvc.Http.RequestBody;
import play.mvc.Http.Session;
import play.data.*;

import models.*;
import views.html.*;

@Security.Authenticated(Secured.class)
public class CtrlResource extends Controller {
  
  static private Form<Resource> resourceForm = form(Resource.class);
  
  public static Result get(Long id) {
    Resource resource = Resource.get(id);
    if(resource != null)     return ok(ViewResource.render(resource, Secured.ownsResource(session("id"), id)));
    else                     return notFound();
  }
  
  public static Result delete(Long id) {
    if(!Secured.ownsResource(session("id"), id)) return forbidden();
    Resource.delete(id);
    return redirect(request().getHeader("referer"));
  }
  
  public static Result setPeriod(Long id, Long period) {
    if(!Secured.ownsResource(session("id"), id)) return forbidden();
    Resource.setPeriod(id, 60*period);
    return redirect(request().getHeader("referer"));
  }
  
  public static Result clearStream(Long id) {
    if(!Secured.ownsResource(session("id"), id)) return forbidden(); 
    Resource.clearStream(id);
    return redirect(request().getHeader("referer"));
  }
  
}
