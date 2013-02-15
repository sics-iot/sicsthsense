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

	@Override
	public Result onUnauthorized(Context ctx) {
		return ok(loginPage.render());
	}

	public static String getUsername() {
		return new Secured().getUsername(Context.current());
	}
	
	public static String getUsername(Session session) {
		String id = session.get("id");
		if (id != null && User.exists(Long.parseLong(id)))
			return id;
		else
			return null;
	}
}
