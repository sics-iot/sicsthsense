package controllers;

//import models.Project;
//import models.Task;
//import models.User;
import java.net.URI;
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

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ObjectNode;

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
      EndPoint.register(CtrlUser.getUser(), submitted.label, submitted.url);
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
      if(CtrlUser.getUser().id != current.getUser().id) return forbidden();
      submitted.id = id;
      submitted.uid = current.uid;
      try { submitted.update(); }
      catch (Exception e) { }
      return redirect(routes.CtrlEndPoint.get(id));
    }
  }
   
  public static Result get(Long id) {
    EndPoint endPoint = EndPoint.get(id);
    if(endPoint != null)     return ok(endPointPage.render(endPoint, null, Secured.ownsEndPoint(session("id"), id)));
    else                     return notFound();
  }
  
  public static Result getByLabel(String userName, String label) {
    EndPoint endPoint = EndPoint.getByLabel(User.getByUserName(userName), label);
    if(endPoint != null)     return ok(endPointPage.render(endPoint, null, Secured.ownsEndPoint(session("id"), endPoint.id)));
    else                     return notFound();
  }
  
  public static Result edit(Long id) {
    if(!Secured.ownsEndPoint(session("id"), id)) return forbidden();
    EndPoint endPoint = EndPoint.get(id);
    if(endPoint != null)     return ok(endPointPage.render(endPoint, epForm, true));
    else                     return notFound();
  }
  
  public static Result delete(Long id) {
    if(!Secured.ownsEndPoint(session("id"), id)) return forbidden();
    EndPoint.delete(id);
    return redirect(routes.Application.manage());
  }
  
  public static Result discover(Long id) {
    if(!Secured.ownsEndPoint(session("id"), id)) return forbidden();
    final EndPoint endPoint = EndPoint.get(id);
    if(endPoint == null) return notFound();
    return async(
      Akka.future(
        new Callable<Result>() {
          public Result call() {
            try {
              String url = Utils.concatPath(endPoint.url,"/discover");
              JsonNode json = WS.url(url).get().get().asJson();
              endPoint.uid = json.findPath("uid").getTextValue();
              for(JsonNode node: json.findPath("resources")) {
                try {
                  Resource.add(node.getTextValue(), endPoint);
                } catch (Exception e) {}
              }
              endPoint.update();
            } catch (Exception e) { }
            return redirect(routes.CtrlEndPoint.get(endPoint.id));
          }
        }
      )
    );
  }
  
  public static Result addResource(Long id) {
    if(!Secured.ownsEndPoint(session("id"), id)) return forbidden();

    Form<Resource> theForm = resourceForm.bindFromRequest();
    if(theForm.hasErrors()) {
      return badRequest("Bad request");
    } else {
      Resource submitted = theForm.get();
      Resource.add(submitted.path, EndPoint.get(id));
      return redirect(request().getHeader("referer"));
    } 
  }
  
  public static Result follow(Long id) {
    CtrlUser.getUser().followEndPoint(EndPoint.get(id));
    return ok();
  }
  
  public static Result unfollow(Long id) {
    CtrlUser.getUser().unfollowEndPoint(EndPoint.get(id));
    return ok();
  }
  
}
