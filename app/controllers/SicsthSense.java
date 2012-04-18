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

  public static Result index() {
    return ok(views.html.index.render());
  }

  public static Result things() {
    return ok(views.html.things.render(Thing.all()));
  }

  public static Result registerThing(String id, String addr) {
    if(Thing.register(id, addr)) {
      return ok("Registered " + id);  
    } else {
      return badRequest(id + " already exists");
    }
  }

  public static Result getThingAddr(String id) {
    Thing thing = Thing.get(id);
    if(thing != null) {
      return ok(Thing.get(id).addr);
    } else {
      return notFound("Thing " + id + " not found");
    }
  }

  public static Result getThing(String id) {
    Thing thing = Thing.get(id);
    if(thing != null) {
      return ok(views.html.thing.render(thing));
    } else {
      return notFound("Thing " + id + " not found");
    }
  }

  public static Result removeThing(String id) {
    if(Thing.remove(id)) {
      return ok("Removed " + id);  
    } else {
      return notFound("Thing " + id + " not found");
    }  
  }
}
