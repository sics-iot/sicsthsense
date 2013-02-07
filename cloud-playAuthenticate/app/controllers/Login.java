package controllers;

//import models.Project;
//import models.Task;
//import models.User;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import models.User;

import play.*;

import play.core.Router.Routes;
import play.libs.F.*;
import play.libs.*;
import play.libs.OpenID.UserInfo;
import play.libs.WS.WSRequestHolder;
import play.mvc.*;
import play.mvc.Http.Context;

import views.html.*;

public class Login extends Controller {
  
	public static Result login() {
		return ok(loginPage.render());
	}
	  
  public static Result authenticate(String openid_identifier) {
   return TODO; 
  }
  
  public static Result openIDCallback() {
	   return TODO; 
  }
  
  public static Result logout() {
    session().clear();
    return redirect(routes.Application.login()); 
  }
    
}
