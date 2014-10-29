/*
 * Copyright (c) 2013, Swedish Institute of Computer Science
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of The Swedish Institute of Computer Science nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE SWEDISH INSTITUTE OF COMPUTER SCIENCE BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. */

/* Description:
 * TODO:
 * */

package controllers;


import java.util.ArrayList;
import java.util.List;

import models.FileSystem;
import models.Resource;
import models.Stream;
import models.User;
import models.Setting;

import play.Logger;
import play.data.DynamicForm;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.adminPage;
import views.html.attachFunctionPage;
import views.html.filesPage;
import views.html.homePage;
import views.html.resourcesPage;
import views.html.searchPage;
import views.html.statisticsPage;
import views.html.streamPage;
import views.html.streamsPage;
import views.html.sitemap;
import views.html.userManagementPage;

//import org.elasticsearch.index.query.QueryBuilders;
//import com.github.cleverage.elasticsearch.IndexQuery;
//import com.github.cleverage.elasticsearch.IndexResults;
//import index.Indexer;

@Security.Authenticated(Secured.class)
public class Application extends Controller {

	static private Form<Resource> resourceForm = Form.form(Resource.class);
	static private Form<Stream> streamForm = Form.form(Stream.class);

  public static Result home() {
  	User currentUser = Secured.getCurrentUser();
  	List<Stream> lastUpdatedPublic = Stream.getLastUpdatedStreams(currentUser, 10);
    return ok(homePage.render(currentUser.followedStreams, lastUpdatedPublic, ""));
  }

  public static Result search() {
  	User currentUser = Secured.getCurrentUser();
		DynamicForm dynamicForm = Form.form().bindFromRequest();
		String q = dynamicForm.field("q").value();
		Integer p = Integer.parseInt(dynamicForm.field("p").value());
		List<Resource> matches = new ArrayList<Resource>();
		/*IndexQuery<Indexer> indexQuery = Indexer.find.query();
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
    return ok(searchPage.render(matches,Stream.availableStreams(currentUser,p),q, 0,""));
*/
		return TODO;
  }

  public static Result docs() {
    return movedPermanently("http://docs.sense.sics.se");
  }

  public static Result explore(Integer p) {
  	User currentUser = Secured.getCurrentUser();
		List<Resource> available = Resource.availableResources(currentUser);
    return ok(searchPage.render(available,Stream.availableStreams(currentUser,p),null, p.intValue(), ""));
  }

  public static Result streams() {
  	User currentUser = Secured.getCurrentUser();
    return ok(streamsPage.render(currentUser.streamList, ""));
  }

  public static Result resources() {
    return CtrlResource.resources(0);
  }
  public static Result sitemap() {
    return ok(sitemap.render(""));
  }

  public static Result files() {
  	User currentUser = Secured.getCurrentUser();
    return ok(filesPage.render(FileSystem.lsDir(currentUser,"/"), "/", ""));
  }

  public static Result viewStream(Long id) {
  	User currentUser = Secured.getCurrentUser();
		Stream stream = Stream.get(id);
		if (stream==null) {return notFound("No Stream by that ID!");}
		Form<Stream> form = streamForm.fill(stream);
    return ok(streamPage.render(currentUser.streamList, stream, form, ""));
  }

  protected static Result ajaxViewStream(Long id) {
  	User currentUser = Secured.getCurrentUser();
		Stream stream = Stream.get(id);
		Form<Stream> form = streamForm.fill(stream);
    return ok(views.html.vstream.viewStreamMainDiv.render(currentUser.streamList, stream, form, ""));
  }

  public static Result attachFunction() {
  	User currentUser = Secured.getCurrentUser();
    return ok(attachFunctionPage.render(currentUser.resourceList, ""));
  }

	// Admin functions
  public static Result admin() {
  	User currentUser = Secured.getCurrentUser();
		// check user has right - Very Important!
		if (!currentUser.isAdmin()) { return redirect(routes.Application.home()); }
		return ok(adminPage.render(""));
  }

  public static Result statistics() {
  	User currentUser = Secured.getCurrentUser();
		// check user has rights - Very Important!
		if (!currentUser.isAdmin()) { return redirect(routes.Application.home()); }
		return ok(statisticsPage.render(""));
  }

  public static Result reindex() {
  	User currentUser = Secured.getCurrentUser();
		// check user has rights - Very Important!
		if (!currentUser.isAdmin()) { return redirect(routes.Application.home()); }
		return ok(adminPage.render("Reindex not implemented yet"));
  }

  public static Result backup() {
  	User currentUser = Secured.getCurrentUser();
		// check user has rights - Very Important!
		if (!currentUser.isAdmin()) { return redirect(routes.Application.home()); }
		return ok(adminPage.render("Backup not yet implemented"));
  }

  public static Result userManagement() {
  	User currentUser = Secured.getCurrentUser();
		// check user has rights - Very Important!
		if (!currentUser.isAdmin()) { return redirect(routes.Application.home()); }
		List<User> users = User.all();
		return ok(userManagementPage.render(users,""));
  }
	public static Result deprecated() {
		return notFound("Deprecated feature! Please check documentation.");
	}

	public static boolean canPasswordLogin() {
		String value = Setting.getSetting("passwordLogin");
		if ("false".equals(value)) {
			return false; // only if set and == "false"
		}
		return true;
	}
	public static Result setPasswordLogin() {
  	User currentUser = Secured.getCurrentUser();
		// check user has rights - Very Important!
		if (!currentUser.isAdmin()) { return redirect(routes.Application.home()); }
		DynamicForm dynamicForm = Form.form().bindFromRequest();
		String passwordLogin = dynamicForm.field("passwordLogin").value();
		if (passwordLogin==null) { return redirect(routes.Application.home()); }
		Logger.warn("password: "+passwordLogin);

		Setting setting = Setting.findName("passwordLogin");
		if (passwordLogin.equals("true")) {
			setting.val = "true";
			setting.update();
			Logger.warn("true");
			return redirect(routes.Application.admin());
		} else {
			setting.val = "false";
			setting.update();
			Logger.warn("false");
			return redirect(routes.Application.admin());
		}
	}

	public static boolean canOpenIDLogin() {
		String value = Setting.getSetting("openIDLogin");
		if ("false".equals(value)) {
			return false; // only if set and == "false"
		}
		return true;
	}

	public static Result setOpenIDLogin() {
  	User currentUser = Secured.getCurrentUser();
		// check user has rights - Very Important!
		if (!currentUser.isAdmin()) { return redirect(routes.Application.home()); }
		DynamicForm dynamicForm = Form.form().bindFromRequest();
		String openidLogin = dynamicForm.field("openidLogin").value();
		if (openidLogin==null) { return redirect(routes.Application.home()); }
		Logger.warn("password: "+openidLogin);

		Setting setting = Setting.findName("openidLogin");
		if (openidLogin.equals("true")) {
			setting.val = "true";
			setting.update();
			Logger.warn("true");
			return redirect(routes.Application.admin());
		} else {
			setting.val = "false";
			setting.update();
			Logger.warn("false");
			return redirect(routes.Application.admin());
		}
	}

	public static Result setDomain() {
  	User currentUser = Secured.getCurrentUser();
		// check user has rights - Very Important!
		if (!currentUser.isAdmin()) { return redirect(routes.Application.home()); }
		DynamicForm dynamicForm = Form.form().bindFromRequest();
		String newDomain = dynamicForm.field("domain").value();
		if (newDomain==null) { return redirect(routes.Application.home()); }
		Logger.warn("password: "+newDomain);

		Setting setting = Setting.findName("domain");
		setting.val = newDomain;
		setting.update();

		return redirect(routes.Application.admin());
	}

	public static String getDomain() {
		Setting setting = Setting.findName("domain");
		if (setting.val.equalsIgnoreCase("unset")) {
			//setting.val="presense.sics.se"; // should set these defaults in a config file?
			setting.val="localhost"; // should set these defaults in a config file?
		}
		return setting.val;
	}

  // -- Javascript routing
  public static Result javascriptRoutes() {
      response().setContentType("text/javascript");
      return ok(
      		play.Routes.javascriptRouter("jsRoutes",
          		controllers.routes.javascript.Application.home(),
          		controllers.routes.javascript.CtrlResource.deleteParser(),
          		controllers.routes.javascript.CtrlResource.addParser(),
          		controllers.routes.javascript.CtrlResource.getById(),
          		controllers.routes.javascript.CtrlResource.regenerateKey(),
          		controllers.routes.javascript.CtrlStream.regenerateKey(),
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
          		controllers.routes.javascript.CtrlFile.miniBrowse(),
          		controllers.routes.javascript.CtrlFile.browse(),
          		controllers.routes.javascript.CtrlFile.delete(),
          		controllers.routes.javascript.CtrlFile.createFile(),
          		controllers.routes.javascript.CtrlFile.createDir(),
          		controllers.routes.javascript.CtrlFile.move()

          )
      );
  }
}
