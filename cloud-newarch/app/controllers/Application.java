package controllers;

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
  
  public static Result search() {
    return TODO;
  }
  
  public static Result manage() {
    return TODO;
  }
  
  // -- Javascript routing
  public static Result javascriptRoutes() {
      response().setContentType("text/javascript");
      return ok(
      		play.Routes.javascriptRouter("jsRoutes",
          		controllers.routes.javascript.Application.home()          		
          )
      );
  }
    
}
