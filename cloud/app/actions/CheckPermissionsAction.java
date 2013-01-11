package actions;

import controllers.Secured;
import models.Resource;
import models.User;
import play.Logger;
import play.mvc.Action;
import play.mvc.Result;
import play.mvc.Http.Context;

public class CheckPermissionsAction extends Action<CheckPermissions> {
	public boolean accessResource(Context ctx, Long resourceId) {
        long userID;
    	String id = ctx.session().get("id");
        if(id != null ){
        	userID = Long.parseLong(id);
        	User user = User.get(userID);
        	Resource resource = Resource.get(resourceId);
        	//userID == Resource.get(resourceId).getUser().id
        	return resource != null && ( ( (user != null) && Secured.ownsResource(id, resourceId) ) || resource.isPublicAccess() || resource.isShare( user ) );
        } else { 
        	return false;
        }
    }
	
	@Override
	public Result call(Context ctx) throws Throwable {
	//		/resources/:id	
		if(configuration.type() == Resource.class) {
			long resID;
			String[] id = ctx.request().queryString().get("id");
			if(id != null ) {
				resID = Long.parseLong(id[0]);
//				String idStr = "";
//				for(String s : id) {
//					idStr += s;
//				}
				Logger.info("ctx.request().queryString().get(id) = " + id[0]);
				return ( accessResource(ctx, resID) ? delegate.call(ctx) : unauthorized("## You can not read or modify a resource that is not shared with you!"));
			}
		} 
			Logger.info("CheckPermissionsAction called for an object that is not a resource: " + ctx);
			return delegate.call(ctx);
					
	}

}
