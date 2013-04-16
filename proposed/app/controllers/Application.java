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

	static private Form<Resource> resourceForm = Form.form(Resource.class);
	static private Form<Stream> streamForm = Form.form(Stream.class);
  
  public static Result home() {
  	User currentUser = Secured.getCurrentUser();
  	List<Stream> lastUpdatedPublic = Stream.getLastUpdatedStreams(currentUser, 10);
    return ok(homePage.render(currentUser.followedStreams, lastUpdatedPublic));
  }
  
  public static Result search() {
  	User currentUser = Secured.getCurrentUser();
		//String q = Controller.params.get("q");
		//Logger.warn("q: "+q);
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

  public static Result files() {
  	User currentUser = Secured.getCurrentUser();
    return ok(filesPage.render(FileSystem.lsDir(currentUser,"/")));
  }
  
	// Liam: this exists to do interesting things with Location...
  public static Result viewStream(Long id) {
  	User currentUser = Secured.getCurrentUser();
		Stream stream = Stream.get(id);
		Form<Stream> form = streamForm.fill(stream);
		/*
		if (true) {
			form.field("latitude") = Form.Field();
		}*/
    return ok(streamPage.render(currentUser.streamList,stream,form));
  }
  
  public static Result attachFunction() {
  	User currentUser = Secured.getCurrentUser();
    return ok(attachFunctionPage.render(currentUser.resourceList));
  }

	/*
	// deprecated
  public static Result resources() {
  	User currentUser = Secured.getCurrentUser();
    return ok(resourcesPage.render(currentUser.resourceList, resourceForm));
  }*/
  
  // -- Javascript routing
  public static Result javascriptRoutes() {
      response().setContentType("text/javascript");
      return ok(
      		play.Routes.javascriptRouter("jsRoutes",
          		controllers.routes.javascript.Application.home(),
          		controllers.routes.javascript.CtrlResource.deleteParser(),
          		controllers.routes.javascript.CtrlResource.addParser(),
          		controllers.routes.javascript.CtrlStream.delete(),
          		controllers.routes.javascript.CtrlStream.clear(),
          		controllers.routes.javascript.CtrlStream.deleteByKey(),
          		controllers.routes.javascript.CtrlStream.clearByKey(),
          		controllers.routes.javascript.CtrlStream.setPublicAccess(),
          		controllers.routes.javascript.CtrlStream.setPublicSearch(),
          		controllers.routes.javascript.CtrlStream.isPublicAccess(),
          		controllers.routes.javascript.CtrlStream.isPublicSearch(),
          		controllers.routes.javascript.CtrlUser.followStream(),
          		controllers.routes.javascript.CtrlUser.isFollowingStream(),
          		controllers.routes.javascript.CtrlFile.lsDir()
          )
      );
  }
    
}
