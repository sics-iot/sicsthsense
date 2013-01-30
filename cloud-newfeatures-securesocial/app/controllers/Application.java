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
import securesocial.core.Identity;
import securesocial.core.java.SecureSocial;

//@Security.Authenticated(Secured.class)
public class Application extends Controller {
  
//  @SecureSocial.SecuredAction
//	public static Result index() {
//		Identity user = (Identity) ctx().args.get(SecureSocial.USER_KEY);
//		Call destination = routes.Application.home();
//	}
  
	@SecureSocial.SecuredAction
  public static Result home() {
    return ok(homePage.render());
  }
  
	@SecureSocial.SecuredAction
  public static Result login() {
    return redirect(routes.Application.home());
  }
  
	@SecureSocial.SecuredAction
  public static Result search() {
    return ok(searchPage.render());
  }
  
	@SecureSocial.SecuredAction
  public static Result manage() {
		Identity user = (Identity) ctx().args.get(SecureSocial.USER_KEY);
    return ok(managePage.render(EndPoint.getByUser(CtrlUser.getUser())));
  }
  
  // -- Javascript routing
	@SecureSocial.SecuredAction
  public static Result javascriptRoutes() {
      response().setContentType("text/javascript");
      return ok(
      		play.Routes.javascriptRouter("jsRoutes",
          		controllers.routes.javascript.CtrlResource.follow(),
          		controllers.routes.javascript.CtrlResource.unfollow(),
          		controllers.routes.javascript.CtrlResource.toggleFollow(),
          		controllers.routes.javascript.CtrlResource.isFollowing(),
          		controllers.routes.javascript.CtrlResource.isPublicAccess(),
          		//controllers.routes.javascript.CtrlResource.removePublicAccess(),
          		//controllers.routes.javascript.CtrlResource.specifyPublicAccess(),
          		controllers.routes.javascript.CtrlResource.togglePublicAccess(),
          		controllers.routes.javascript.CtrlEndPoint.follow(),
          		controllers.routes.javascript.CtrlEndPoint.unfollow(),
          		controllers.routes.javascript.CtrlEndPoint.toggleFollow(),
          		controllers.routes.javascript.CtrlEndPoint.isFollowing()
          )
      );
  }
    
}
