package controllers;

import models.Thing;
import play.mvc.Controller;
import play.mvc.Result;

public class RestThing extends Controller {
  
  public static Result register(String url) {
    Thing.register(url);
    return ok("Registering: " + url);
  }
    
  public static Result remove(String id) {
    if(Thing.remove(id)) {
      return ok("Thing removed");
    } else {
      return notFound("Thing not found");
    }  
  }
  
}
