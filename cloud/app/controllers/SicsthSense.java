package controllers;

import java.net.*;

import models.*;
import play.*;

import play.api.templates.Html;
import play.libs.F.Promise;
import play.libs.WS;
import play.libs.WS.Response;
import play.mvc.*;

import views.html.*;

public class SicsthSense extends Controller {
    
  public static Result home() {
    return ok(views.html.home.render());
  }
  
  public static Result search() {
    return ok(views.html.search.render(Thing.all()));
  }
    
  public static Result register(String url) {
    if(Thing.register(url)) {
    } else {
      flash("status", "Failed to register: " + url);
    }
    return redirect("/yourThings");
  }
  
  public static Result yourThings() {
    return ok(views.html.yourThings.render(Thing.all(), flash("status")));
  }
 
  public static Result thing(String id) {
    Thing thing = Thing.get(id);
    if(thing != null) {
      return ok(views.html.thing.render(thing, thing.resources.split("\n")));
    } else {
      return notFound(views.html.notfound.render(request().path() + ": Thing not found"));
    }
  }
  
  public static Result account() {
    return ok(views.html.account.render());
  }
 
  public static Result help() {
    return ok(views.html.help.render());
  }
  
}
