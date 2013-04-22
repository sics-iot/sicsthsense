package controllers;

import java.util.ArrayList;
import java.util.List;

import play.*;

import com.github.cleverage.elasticsearch.IndexQuery;
import com.github.cleverage.elasticsearch.IndexResults;
import com.github.cleverage.elasticsearch.IndexService;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.facet.FacetBuilders;
import org.elasticsearch.search.facet.terms.TermsFacet;

import play.core.Router.Routes;
import play.libs.F.*;
import play.libs.*;
import play.mvc.*;
import play.data.*;
import controllers.*;

import views.html.*;
import models.*;
import index.*; // for search namespace

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
		DynamicForm dynamicForm = Form.form().bindFromRequest();
		String q = dynamicForm.field("q").value();
		Logger.warn("q: "+q);
		/* Should we use streams or resources? or both?
		*/
		List<Resource> matches = new ArrayList<Resource>();

		IndexQuery<Indexer> indexQuery = Indexer.find.query();
		indexQuery.setBuilder(QueryBuilders.multiMatchQuery("description",q));
		IndexResults<Indexer> indexResults = null;
		try {
			indexResults = Indexer.find.search(indexQuery);
		} catch (Exception e) {
			Logger.warn("Search execution failed");
		}

		if (indexResults != null) {
			for (Indexer result: indexResults.results) {
				if (result.id == null) { Logger.error("Index id was null!"); continue; }
				Resource resource = Resource.getById(result.id);
				if (resource == null) {continue;} // shows a disconncet between ESindex and MySQL
				//Logger.warn("found resource: "+resource.label);
				matches.add(resource);
			}
		}

		//Resources.availableResources(currentUser);
    return ok(searchPage.render(matches,Stream.availableStreams(currentUser),q));
  }
  
  public static Result admin() {
  	User currentUser = Secured.getCurrentUser();
		// check admin
		if (currentUser.isAdmin()) {
			return ok(adminPage.render());
		}
    return redirect(routes.Application.home());
  }
  
  public static Result explore() {
  	User currentUser = Secured.getCurrentUser();
		List<Resource> available = Resource.availableResources(currentUser);
    return ok(searchPage.render(available,Stream.availableStreams(currentUser),null));
  }
  
  public static Result streams() {
  	User currentUser = Secured.getCurrentUser();
    return ok(streamsPage.render(currentUser.streamList));
  }

  public static Result resources() {
  	User currentUser = Secured.getCurrentUser();
    return ok(resourcesPage.render(currentUser.resourceList,null));
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
