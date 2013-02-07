package controllers;

import java.util.HashMap;

import com.feth.play.module.pa.PlayAuthenticate;
import com.feth.play.module.pa.user.AuthUserIdentity;
//import com.feth.play.module.pa.controllers.routes.Authenticate;

import models.EndPoint;
import models.User;

import play.*;

import play.core.Router.Routes;
import play.libs.F.*;
import play.libs.*;
import play.mvc.*;
import play.mvc.Http.Session;
import play.data.*;

import views.html.*;
import models.*;
import controllers.routes;

public class Application extends Controller {
  
	public static final String FLASH_MESSAGE_KEY = "message";
	public static final String FLASH_ERROR_KEY = "error";
	
	public static Result oAuthDenied(final String providerKey) {
		com.feth.play.module.pa.controllers.Authenticate.noCache(response());
		flash(FLASH_ERROR_KEY,
				"You need to accept the OAuth connection in order to use this website!");
		return redirect(routes.Application.login());
	}

	public static User getLocalUser(final Session session) {
		final User localUser = User.findByAuthUserIdentity(PlayAuthenticate
				.getUser(session));
		return localUser;
	}
	
	@Security.Authenticated(Secured.class)	
  public static Result home() {
    return ok(homePage.render());
  }
  
	@Security.Authenticated(Secured.class)
  public static Result index() {
    return redirect(routes.Application.home());
  }
  
  public static Result login() {
	return ok(loginPage.render());
  }
  
  public static Result logout() {
    //session().clear();
    //<a href="@com.feth.play.module.pa.controllers.routes.Authenticate.logout">Logout</a>
    return redirect(com.feth.play.module.pa.controllers.routes.Authenticate.logout()); 
  }
  
  @Security.Authenticated(Secured.class)
  public static Result search() {
    return ok(searchPage.render());
  }
  @Security.Authenticated(Secured.class)
  public static Result manage() {
    return ok(managePage.render(EndPoint.getByUser(CtrlUser.getUser())));
  }  
  // -- Javascript routing
  @Security.Authenticated(Secured.class)
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
