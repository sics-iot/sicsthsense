package controllers;

import play.*;
import play.mvc.*;
import play.mvc.Http.*;

import models.*;
import views.html.*;

public class Secured extends Security.Authenticator {

	@Override
	public String getUsername(Context ctx) {
		return getUsername(ctx.session());
	}
	
	public static String getUsername(Session session) {
		Long id = getUserId(session);
		if(id != null){
			return User.get(id).getUserName();
		}
		return null;
	}

	@Override
	public Result onUnauthorized(Context ctx) {
		return ok(loginPage.render());
	}

	public static User getCurrentUser() {
		return User.get(Secured.getUserId(Context.current()));
	}
	
	public static String getUsername() {
		return new Secured().getUsername(Context.current());
	}
	
	public static Long getUserId(Context ctx) {
		Long id = getUserId(ctx.session());
		if(id == null){
			id = getUserId(ctx.request());
		}
		return id;
	}
	
	public static Long getUserId(Session session) {
		String id = session.get("id");
		try{
		if (id != null && User.exists(Long.parseLong(id)))
			return Long.parseLong(id);
		else
			return null;
		} catch(Exception e) {
			return null;
		}
	}
	
	public static Long getUserId(Request request) {
		String userToken = request.getQueryString("user_token");
		User current = User.getByToken(userToken);
		if ( current != null ) {
			return current.getId();
		}	else
			return null;
	}
}
