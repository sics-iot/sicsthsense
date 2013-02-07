package controllers;

import com.feth.play.module.pa.PlayAuthenticate;
import com.feth.play.module.pa.user.AuthUser;

import play.*;
import play.mvc.*;
import play.mvc.Http.*;

import models.*;
import views.html.*;

public class Secured extends Security.Authenticator {
    
//    @Override
//    public String getUsername(Context ctx) {
//        String id = ctx.session().get("id");
//        if(id != null && User.exists(Long.parseLong(id)))    return id;
//        else                                                 return null;
//    }
   
	@Override
	public String getUsername(final Context ctx) {
		final AuthUser u = PlayAuthenticate.getUser(ctx.session());

		if (u != null) {
			return u.getId();
		} else {
			return null;
		}
	}
	
	@Override
	public Result onUnauthorized(final Context ctx) {
		ctx.flash().put(Application.FLASH_MESSAGE_KEY, "Unauthorized! You need to log in!");
		return redirect(routes.Application.login());
	}
}
