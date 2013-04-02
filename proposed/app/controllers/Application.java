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

	static private Form<Source> sourceForm = Form.form(Source.class);
  
  public static Result home() {
  	User currentUser = Secured.getCurrentUser();
    return ok(homePage.render(currentUser.followedStreams));
  }
  
  public static Result search() {
  	User currentUser = Secured.getCurrentUser();
		List<Stream> availableStreams = Stream.availableStreams(currentUser);
    return ok(searchPage.render(availableStreams));
  }
  
  public static Result explore() {
  	User currentUser = Secured.getCurrentUser();
		List<Stream> availableStreams = Stream.availableStreams(currentUser);
    return ok(searchPage.render(availableStreams));
  }
  
  public static Result streams() {
  	User currentUser = Secured.getCurrentUser();
    return ok(streamsPage.render(currentUser.streamList));
  }
  
  public static Result attachFunction() {
  	User currentUser = Secured.getCurrentUser();
    return ok(attachFunctionPage.render(currentUser.sourceList));
  }

	/*
	// deprecated
  public static Result sources() {
  	User currentUser = Secured.getCurrentUser();
    return ok(sourcesPage.render(currentUser.sourceList, sourceForm));
  }*/
  
  // -- Javascript routing
  public static Result javascriptRoutes() {
      response().setContentType("text/javascript");
      return ok(
      		play.Routes.javascriptRouter("jsRoutes",
          		controllers.routes.javascript.Application.home(),
          		controllers.routes.javascript.CtrlSource.deleteParser(),
          		controllers.routes.javascript.CtrlSource.addParser(),
          		controllers.routes.javascript.CtrlStream.delete(),
          		controllers.routes.javascript.CtrlStream.clear(),
          		controllers.routes.javascript.CtrlStream.deleteByKey(),
          		controllers.routes.javascript.CtrlStream.clearByKey(),
          		controllers.routes.javascript.CtrlStream.setPublicAccess(),
          		controllers.routes.javascript.CtrlStream.setPublicSearch(),
          		controllers.routes.javascript.CtrlStream.isPublicAccess(),
          		controllers.routes.javascript.CtrlStream.isPublicSearch(),
          		controllers.routes.javascript.CtrlUser.followStream(),
          		controllers.routes.javascript.CtrlUser.isFollowingStream()
          )
      );
  }
    
}
