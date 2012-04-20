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
    
  public static Result yourThings() {
    return ok(views.html.yourThings.render(Thing.all()));
  }
 
  public static Result thing(String id) {
    Thing thing = Thing.get(id);
    if(thing != null) {
      System.out.println("\n\n\n\nOK\n\n\n");
      System.out.println(thing.resources);
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
