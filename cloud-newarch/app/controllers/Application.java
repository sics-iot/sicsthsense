package controllers;

import java.util.ArrayList;
import java.util.List;

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
  	User currentUser = Secured.getCurrentUser();
  	//Ugly work around to get Streams only
  	List<Stream> streams = new ArrayList<Stream>();
  	for(UserOwnedResource res : currentUser.ownedResources ) {
  		if( res instanceof Stream) {
  			streams.add((Stream)res);
  		}
  	}	
    return ok(managePage.render(streams));
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
