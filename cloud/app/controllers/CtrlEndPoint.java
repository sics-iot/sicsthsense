package controllers;

//import models.Project;
//import models.Task;
//import models.User;
import java.util.HashMap;
import java.util.Map;

import play.*;

import play.core.Router.Routes;
import play.libs.F.*;
import play.libs.*;
import play.mvc.*;
import play.data.*;

import models.*;
import views.html.*;
import java.util.concurrent.Callable;

@Security.Authenticated(Secured.class)
public class CtrlEndPoint extends Controller {
  
  static private Form<EndPoint> epForm = form(EndPoint.class);
  static private Form<Resource> resourceForm = form(Resource.class);
  
  public static Result add() {
    Form<EndPoint> theForm = epForm.bindFromRequest();
    if(theForm.hasErrors()) {
      return badRequest("Bad request");
    } else {
      EndPoint submitted = theForm.get();
      EndPoint.register(CtrlUser.getUser(), submitted.url);
      return redirect(routes.Application.manage());
    }
  }
  
  public static Result submit(Long id) {
    Form<EndPoint> theForm = epForm.bindFromRequest();
    if(theForm.hasErrors()) {
      return badRequest("Bad request");
    } else {
      EndPoint current = EndPoint.get(id);
      EndPoint submitted = theForm.get();
      submitted.id = id;
      submitted.uid = current.uid;
      submitted.update();
      return redirect(routes.CtrlEndPoint.get(id));
    }
  }
   
  public static Result get(Long id) {
    EndPoint endPoint = EndPoint.get(id);
    if(endPoint != null)     return ok(ViewEndPoint.render(endPoint, null));
    else                     return notFound();
  }
  
  public static Result edit(Long id) {
    EndPoint endPoint = EndPoint.get(id);
    if(endPoint != null)     return ok(ViewEndPoint.render(endPoint, epForm));
    else                     return notFound();
  }
  
  public static Result delete(Long id) {
    EndPoint.delete(id);
    return redirect(routes.Application.manage());
  }
  
  public static Result discover(Long id) {
    final EndPoint endPoint = EndPoint.get(id);
    if(endPoint == null) return notFound();
    
    return async(
      Akka.future(
        new Callable<Result>() {
          public Result call() {
            Boolean ret = endPoint.discover();
            return redirect(routes.CtrlEndPoint.get(endPoint.id)); 
          }
        }
      )
    );
    
  }
  
  public static Result addResource(Long id) {
    Form<Resource> theForm = resourceForm.bindFromRequest();
    if(theForm.hasErrors()) {
      return badRequest("Bad request");
    } else {
      Resource submitted = theForm.get();
      Resource.add(submitted.path, EndPoint.get(id));
      return redirect(request().getHeader("referer"));
    }
  }
  
}
