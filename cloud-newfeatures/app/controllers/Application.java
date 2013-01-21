package controllers;

//import models.Project;
//import models.Task;
//import models.User;
import java.util.HashMap;

import models.EndPoint;

import play.*;

import play.core.Router.Routes;
import play.libs.F.*;
import play.libs.*;
import play.mvc.*;
import play.data.*;

import views.html.*;
import models.*;

@Security.Authenticated(Secured.class)
public class Application extends Controller {
  
  public static Result home() {
    return ok(homePage.render());
  }
  
  public static Result login() {
    return redirect(routes.Application.home());
  }
  
  public static Result search() {
    return ok(searchPage.render());
  }
  
  public static Result manage() {
    return ok(managePage.render(EndPoint.getByUser(CtrlUser.getUser())));
  }
  
  // -- Javascript routing
  
  public static Result javascriptRoutes() {
      response().setContentType("text/javascript");
      return ok(
      		play.Routes.javascriptRouter("jsRoutes",
          		controllers.routes.javascript.CtrlResource.follow(),
          		controllers.routes.javascript.CtrlResource.unfollow(),
          		controllers.routes.javascript.CtrlResource.toggleFollow(),
          		controllers.routes.javascript.CtrlResource.isFollowing(),
          		controllers.routes.javascript.CtrlEndPoint.follow(),
          		controllers.routes.javascript.CtrlEndPoint.unfollow(),
              controllers.routes.javascript.CtrlEndPoint.toggleFollow(),
          		controllers.routes.javascript.CtrlEndPoint.isFollowing()
          )
      );
  }
    
}
