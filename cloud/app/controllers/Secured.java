package controllers;

import play.*;
import play.mvc.*;
import play.mvc.Http.*;

import models.*;
import views.html.*;

public class Secured extends Security.Authenticator {
    
    @Override
    public String getUsername(Context ctx) {
        String id = ctx.session().get("id");
        if(id != null && User.exists(Long.parseLong(id)))    return id;
        else                                                 return null;
    }
    
    
    @Override
    public Result onUnauthorized(Context ctx) {
      return ok(loginPage.render());
    }
    
    public static boolean ownsResource(String idStr, Long resourceId) {
      return Long.parseLong(idStr) == Resource.get(resourceId).getUser().id;
    }
    
    public static boolean ownsEndPoint(String idStr, Long endPointId) {
      return Long.parseLong(idStr) == EndPoint.get(endPointId).getUser().id;
    }
    
}
