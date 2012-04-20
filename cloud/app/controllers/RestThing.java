package controllers;

import models.Thing;
import play.mvc.Controller;
import play.mvc.Result;

public class RestThing extends Controller {
  
  public static Result register(String url) {
    if(Thing.register(url)) {
      return ok("Registered: " + url);
    } else {
      return notFound("Registration failed");
    }
  }
    
  public static Result remove(String id) {
    if(Thing.remove(id)) {
      return ok("Thing removed");
    } else {
      return notFound("Thing not found");
    }  
  }
  
}
