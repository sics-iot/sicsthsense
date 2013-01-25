package actions;

import models.EndPoint;
import models.Resource;
import models.User;
import play.Logger;
import play.mvc.Action;
import play.mvc.Result;
import play.mvc.Http.Context;

public class CheckPermissionsAction extends Action<CheckPermissions> {

	public boolean accessResource(Context ctx, Long resourceId) {
    	String id = ctx.session().get("id");
    	return canAccessResource(id, resourceId); 
    }
	
	@Override
	public Result call(Context ctx) throws Throwable {
	//		/resources/:id	
		if(configuration.type() == Resource.class) {
			long resID;
			String[] id = ctx.request().queryString().get("id");
			if(id != null ) {
				resID = Long.parseLong(id[0]);
				Logger.info("ctx.request().queryString().get(id) = " + id[0]);
				return ( accessResource(ctx, resID) ? delegate.call(ctx) : onUnauthorized(ctx));
			}
		} 
			Logger.info("CheckPermissionsAction called for an object that is not a resource: " + ctx);
			return delegate.call(ctx);
	}

	public static boolean ownsEndPoint(String idStr, Long endPointId) {
	  return Long.parseLong(idStr) == EndPoint.get(endPointId).getUser().id;
	}

	public static boolean ownsResource(String idStr, Long resourceId) {
	  return Long.parseLong(idStr) == Resource.get(resourceId).getUser().id;
	}
	
	public static boolean canAccessResource(String idStr, Long resourceId) {
		if(idStr != null ){
			long userID = Long.parseLong(idStr);
    	User user = User.get(userID);
    	Resource resource = Resource.get(resourceId);
    	//userID == Resource.get(resourceId).getUser().id
    	return ( 
    			resource != null 
    			&& ( user != null && ownsResource(idStr, resourceId) ) 
    			|| resource.isPublicAccess() 
    			|| resource.isShare( user ) 
    			);
    } else { 
    	return false;
    }
	}
	
	public Result onUnauthorized(Context ctx) {
		//String id = ctx.request().username();
		return unauthorized(views.html.errorPage.render("Unauthorized! This is not shared with you!"));
		//return unauthorized("## You can not read or modify a resource that is not shared with you!");
	}
	
	public static Result onUnauthorized() {		
		//Context.current() will return the context associated with the 
		//current thread
		return (new CheckPermissionsAction().onUnauthorized(Context.current()));
	}

}
