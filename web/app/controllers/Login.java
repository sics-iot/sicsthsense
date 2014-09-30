/*
 * Copyright (c) 2013, Swedish Institute of Computer Science
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of The Swedish Institute of Computer Science nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE SWEDISH INSTITUTE OF COMPUTER SCIENCE BE LIABLE 
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. */

/* Description:
 * TODO:
 * */

package controllers;

import java.util.HashMap;
import java.util.Map;

import models.User;
import play.Logger;
import play.data.DynamicForm;
import play.data.Form;
import play.libs.F.Function;
import play.libs.F.Promise;
import play.libs.OpenID;
import play.libs.OpenID.UserInfo;
import play.mvc.Call;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.registerPage;

public class Login extends Controller {
	static private Form<User> registerForm = Form.form(User.class);
	
	public static Result authenticate(String openid_identifier) {
		if (!Application.canOpenIDLogin()) {return notFound("This feature is disabled.");}
		Map<String, String> attributes = new HashMap<String, String>();
		attributes.put("email", "http://axschema.org/contact/email");
		attributes.put("firstName", "http://axschema.org/namePerson/first");
		attributes.put("lastName", "http://axschema.org/namePerson/last");
		Promise<Result> promised = null;
//		try {
			promised = OpenID.redirectURL(openid_identifier,
				routes.Login.openIDCallback().absoluteURL(request()), attributes).map(
				new Function<String, Result>() {
					public Result apply(String url) {
						return redirect(url);
					}
				});
			return async(promised);
//		} catch (java.lang.Exception e) {
//			return ok("Not online for OpenID verification, consider username/password authentication");
//		}
	}
	
  public static Result signup() {
    return ok(registerPage.render(null,""));
  }
  
	public static Result authenticatePassword(String username, String password, String signtype) {
		if (!Application.canPasswordLogin()) {return notFound("This feature is disabled.");}

		if (signtype.equals("Sign up")) {
			return ok(registerPage.render(null,username));
		}
		Logger.warn(signtype);

		// check database
		User user = User.getByUserName(username);
		if (user==null)     { return notFound("User doesnt exist!"); }
		if (user.password==null || !user.password.equals(User.hash(password))) {
			return notFound("Password incorrect: " +user.password+" != "+User.hash(password)+ " : "+password);
		}
		return doLogin(user, routes.Application.home());
	}

	// new user through the password system
	public static Result registerPassword() {
		//DynamicForm dynamicForm = new DynamicForm();
		//dynamicForm.bindFromRequest();
		DynamicForm dynamicForm = Form.form().bindFromRequest();
		String username = dynamicForm.field("username").value();
		String password = dynamicForm.field("password").value();
		String password2 = dynamicForm.field("password2").value();
		if (username==null || password==null) {
			return ok(registerPage.render("Error: must specify username/password!",username));
		}
		if (!password.equals(password2)) {
			return ok(registerPage.render("Error: Passwords didn't match!",username));
		}

		if (User.getByUserName(username)!=null) {
			return ok(registerPage.render("Username already exists!",""));
		}
		//validate username/pass
		User user = new User();
		user.username=username;
		user.setEmail(username);
		user.setPassword(password);
		//and register in database
		//user.save(); --wrong.. does not create everything
		User.create(user);
		return doLogin(user, routes.CtrlUser.edit());
	}

	public static Result openIDCallback() {
		UserInfo userInfo = OpenID.verifiedId().get();
		User user = User.getByEmail(userInfo.attributes.get("email"));
		Call destination = routes.Application.home();
		if (user == null) {
			user = User.create(new User(userInfo.attributes.get("email"),
					userInfo.attributes.get("email"), userInfo.attributes
							.get("firstName"), userInfo.attributes.get("lastName")
							));
			destination = routes.CtrlUser.edit();
			Logger.info("created");
		}
		return doLogin(user, destination);
	}

	private static Result doLogin(User user, Call destination) {
		user.updateLastLogin();
		Logger.info("Logged in: " + user.email);
		// TODO encrypt some session parameters e.g. id
		// TODO user.currentSessionToken = generateSessionToken(user);
		// session("login_time", Long.toString(user.lastLogin.getTime()));
		session("id", user.id.toString());
		if (user.getToken() == null)
			return CtrlUser.resetToken(true);
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

	// Become another user, only allowed if admin
	public static Result swapUser() {
  	User currentUser = Secured.getCurrentUser();
		if (!currentUser.isAdmin()) {return unauthorized("You are not an Admin!");}

		DynamicForm dynamicForm = Form.form().bindFromRequest();
		String username = dynamicForm.field("newUser").value();
		User user = User.getByUserName(username);
		if (user==null) {return notFound("Username "+username+" does not exist!");}

		Call destination = routes.Application.home();
		return doLogin(user, destination);
	}
	
	public static Result logout() {
		session().clear();
		return redirect(routes.Application.home());
	}

}
