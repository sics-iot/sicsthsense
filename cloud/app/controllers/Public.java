package controllers;

//import models.Project;
//import models.Task;
//import models.User;
import java.util.HashMap;

import models.EndPoint;

import play.*;

import play.core.Router.Routes;
import play.libs.F.*;
import play.libs.*;
import play.mvc.*;

import views.html.*;

public class Public extends Controller {
  
  public static Result search() {
    return ok(search.render(EndPoint.all()));
  }
  
  public static Result about() {
    return ok(about.render());
  }
    
}
