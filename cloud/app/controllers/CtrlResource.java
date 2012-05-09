package controllers;

//import models.Project;
//import models.Task;
//import models.User;
import java.util.HashMap;

import play.*;

import play.core.Router.Routes;
import play.libs.F.*;
import play.libs.*;
import play.mvc.*;
import play.data.*;

import models.*;
import views.html.*;

@Security.Authenticated(Secured.class)
public class CtrlResource extends Controller {
  
  static private Form<Resource> resourceForm = form(Resource.class);
  
  public static Result get(Long id) {
    Resource resource = Resource.get(id);
    if(resource != null)     return ok(ViewResource.render(resource));
    else                     return notFound();
  }
  
  public static Result delete(Long id) {
    Resource.delete(id);
    return redirect(request().getHeader("referer"));
  }
  
  public static Result setPeriod(Long id, Long period) {
    Resource.setPeriod(id, period);
    return redirect(request().getHeader("referer"));
  }
  
  public static Result clearStream(Long id) {
    Resource.clearStream(id);
    return redirect(request().getHeader("referer"));
  }

}
