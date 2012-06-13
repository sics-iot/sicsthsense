package controllers;

//import models.Project;
//import models.Task;
//import models.User;
import java.util.HashMap;
import java.util.Map;

import models.User;

import play.*;

import play.core.Router.Routes;
import play.libs.F.*;
import play.libs.*;
import play.libs.OpenID.UserInfo;
import play.mvc.*;
import play.mvc.Http.Context;

import views.html.*;

public class Login extends Controller {
   
  public static Result authenticate(String openid_identifier) {
    Map<String, String> attributes = new HashMap<String, String>();
    attributes.put("email", "http://axschema.org/contact/email");
    attributes.put("firstName", "http://axschema.org/namePerson/first");
    attributes.put("lastName", "http://axschema.org/namePerson/last");
    return async( 
        OpenID.redirectURL(openid_identifier, routes.Login.openIDCallback().absoluteURL(request()), attributes).map (
            new Function<String, Result>() {
              public Result apply(String url) {
                return redirect(url);
              }
            }
        )
     );
  }
  
  public static Result openIDCallback() {
    UserInfo userInfo = OpenID.verifiedId().get();
    User user = User.getByEmail(userInfo.attributes.get("email"));
    if(user == null) {
      user = User.create(new User(userInfo.attributes.get("email"),
          userInfo.attributes.get("email"),
          userInfo.attributes.get("firstName"),
          userInfo.attributes.get("lastName"),
          userInfo.attributes.get("location")
          ));

      Logger.info("created");
    }
    Logger.info("ok");
    session("id", user.id.toString());
    return redirect(routes.Application.home());
  }
  
  public static Result logout() {
    session().clear();
    return redirect(routes.Application.home()); 
  }
    
}
