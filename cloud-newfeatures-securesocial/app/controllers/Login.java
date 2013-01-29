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
   
  public static Result authenticate(String openid_identifier) {
    Map<String, String> attributes = new HashMap<String, String>();
    attributes.put("email", "http://axschema.org/contact/email");
    attributes.put("firstName", "http://axschema.org/namePerson/first");
    attributes.put("lastName", "http://axschema.org/namePerson/last");
    Promise<Result> promised = null;
		promised = OpenID.redirectURL(openid_identifier,
				routes.Login.openIDCallback().absoluteURL(request()),
				attributes).map(new Function<String, Result>() {
			public Result apply(String url) {
				return redirect(url);
			}
		});
		
		//XXX: There's a bug in OpenID library, and this code does not solve it
		//Should try another library or move to Play 2.1
//		try {
//			promised.recover(new Function<Throwable, Result>() {
//				public Result apply(Throwable T) throws Throwable {
//					Logger.info(T.getMessage());
//					session().clear();
//					return redirect(routes.Application.home());
//				}
//			});
//		} catch (Throwable e) {
//			Logger.info(e.getMessage());
//			session().clear();
//			promised = Akka.future(new Callable<Result>() {
//				public Result call() {
//					return redirect(routes.Application.home());
//				}
//			});
//		}

		return async(promised);
  }
  
  public static Result openIDCallback() {
    UserInfo userInfo = OpenID.verifiedId().get();
    User user = User.getByEmail(userInfo.attributes.get("email"));
    Call destination = routes.Application.home();
    if(user == null) {
      user = User.create(new User(userInfo.attributes.get("email"),
          userInfo.attributes.get("email"),
          userInfo.attributes.get("firstName"),
          userInfo.attributes.get("lastName"),
          userInfo.attributes.get("location")
          ));
      destination = routes.CtrlUser.edit();
      Logger.info("created");
    }
    Logger.info("ok");
    session("id", user.id.toString());
    return redirect(destination);
  }
  
  public static Result logout() {
    session().clear();
    return redirect(routes.Application.home()); 
  }
    
}
