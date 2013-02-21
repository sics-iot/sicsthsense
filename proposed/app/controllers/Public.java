package controllers;

import play.mvc.*;

import views.html.*;

public class Public extends Controller {
  
  public static Result about() {
    return ok(aboutPage.render());
  }
    
}
