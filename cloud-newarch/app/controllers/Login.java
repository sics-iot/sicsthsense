package controllers;

//import models.Project;
//import models.Task;
//import models.User;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
				routes.Login.openIDCallback().absoluteURL(request()), attributes).map(
				new Function<String, Result>() {
					public Result apply(String url) {
						return redirect(url);
					}
				});
		return async(promised);
	}

	public static Result openIDCallback() {
		UserInfo userInfo = OpenID.verifiedId().get();
		User user = User.getByEmail(userInfo.attributes.get("email"));
		Call destination = routes.Application.home();
		if (user == null) {
			user = User.create(new User(userInfo.attributes.get("email"),
					userInfo.attributes.get("email"), userInfo.attributes
							.get("firstName"), userInfo.attributes.get("lastName"),
					userInfo.attributes.get("location")));
			destination = routes.CtrlUser.edit();
			Logger.info("created");
		}
		user.updateLastLogin();
//		user.currentSessionToken = generateSessionToken(user);
		Logger.info("ok");
		//TODO encrypt some session parameters e.g. id
		session("id", user.id.toString());
		//session("login_time", Long.toString(user.lastLogin.getTime()));
		return redirect(destination);
	}

//	private static String loginApplicationSecret = "SecretKey";
//	private static String generateSessionToken(User user) {
//		String secret = user.getEmail() + Long.toString(user.lastLogin.getTime()) + Login.loginApplicationSecret;
//		try {
//			return MessageDigest.getInstance("SHA-512").digest(secret.getBytes()).toString();
//		} catch (NoSuchAlgorithmException e) {
//			throw new RuntimeException(e);
//		} catch (UnsupportedEncodingException e) {
//			throw new RuntimeException(e);
//		}
//	}
	
	public static Result logout() {
		session().clear();
		return redirect(routes.Application.home());
	}

}
