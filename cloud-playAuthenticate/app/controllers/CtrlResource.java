package controllers;

//import models.Project;
//import models.Task;
//import models.User;
import java.util.HashMap;
import java.util.List;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ObjectNode;

import actions.CheckPermissions;
import actions.CheckPermissionsAction;

import com.ning.http.client.Request;

import play.*;

import play.core.Router.Routes;
import play.libs.F.*;
import play.libs.*;
import play.mvc.*;
import play.mvc.Http.Context;
import play.mvc.Http.RequestBody;
import play.mvc.Http.Session;
import play.data.*;

import models.*;
import views.html.*;
import play.data.Form;

@Security.Authenticated(Secured.class)
public class CtrlResource extends Controller {
  
  static private Form<Resource> resourceForm = Form.form(Resource.class);
  
 // @CheckPermissions(type = Resource.class)
  public static Result get(Long id) {
    Resource resource = Resource.get(id);
    Result result = null;
    if(resource != null) {
    	if(CheckPermissionsAction.canAccessResource(session("id"), id)) {
    		result = ok(
    				resourcePage.render(resource, 
    						CheckPermissionsAction.ownsResource(session("id"), id)
    						));
    	} else {
    		result = CheckPermissionsAction.onUnauthorized();
    	}
    } else {
    	result = notFound();
    }
    return result;
  }
  
  //@play.db.ebean.Transactional
  public static Result delete(Long id) {
    if(!CheckPermissionsAction.ownsResource(session("id"), id)) return CheckPermissionsAction.onUnauthorized();
    
    Resource.delete(id);
    //TODO: Improve? Redirecting to manage page since the referrer was deleted!
    //return redirect(request().getHeader("referer"));
    return redirect(routes.Application.manage());
  }
  
  public static Result setInputParser(Long id, String inputParser) {
	  if(!CheckPermissionsAction.ownsResource(session("id"), id)) return CheckPermissionsAction.onUnauthorized();
	  Resource.setParser(id, inputParser);
	  return redirect(request().getHeader("referer"));
  }
  
  public static Result setPeriod(Long id, Long period) {
    if(!CheckPermissionsAction.ownsResource(session("id"), id)) return CheckPermissionsAction.onUnauthorized();
    Resource.setPeriod(id, 60*period);
    return redirect(request().getHeader("referer"));
  }
  
  public static Result setLabelName(Long id, String label) {
    if(!CheckPermissionsAction.ownsResource(session("id"), id)) return CheckPermissionsAction.onUnauthorized();
    Resource.setLabelName(id, label);
    return redirect(request().getHeader("referer"));
  }
  
  public static Result clearStream(Long id) {
    if(!CheckPermissionsAction.ownsResource(session("id"), id)) return CheckPermissionsAction.onUnauthorized(); 
    Resource.clearStream(id);
    return redirect(request().getHeader("referer"));
  }

  public static Result follow(Long id) {
    CtrlUser.getUser().followResource(Resource.get(id));
    return ok();
  }
  
  public static Result unfollow(Long id) {
    CtrlUser.getUser().unfollowResource(Resource.get(id));
    return ok();
  }
  
  public static Result toggleFollow(Long id) {
  	Resource resource = Resource.get(id);
  	if( CtrlUser.getUser().followsResource(resource) ){
  		CtrlUser.getUser().unfollowResource(resource);
  	} else {
  		CtrlUser.getUser().followResource(resource);
  	}
    return ok();
  }
  
  public static Result isFollowing(Long id) {
  	Resource resource = Resource.get(id);
  	if( CtrlUser.getUser().followsResource(resource) ){
  		return ok("1");
  	} else {
  		return ok("0");
  	}
  }
  
  public static Result setPublicAccess(Long id) {
    if(!CheckPermissionsAction.ownsResource(session("id"), id)) return CheckPermissionsAction.onUnauthorized(); 
    Resource.get(id).setPublicAccess(true);
    return ok();
  }
  
  public static Result removePublicAccess(Long id) {
    if(!CheckPermissionsAction.ownsResource(session("id"), id)) return CheckPermissionsAction.onUnauthorized(); 
  	Resource.get(id).setPublicAccess(false);
    return ok();
  }
  
  public static Result specifyPublicAccess(Long id, Boolean acc) {
    if(!CheckPermissionsAction.ownsResource(session("id"), id)) return CheckPermissionsAction.onUnauthorized(); 
  	Resource.get(id).setPublicAccess(acc);
    return ok();
  }
  
  public static Result togglePublicAccess(Long id) {
    if(!CheckPermissionsAction.ownsResource(session("id"), id)) return CheckPermissionsAction.onUnauthorized(); 
  	Resource.get(id).setPublicAccess(!Resource.get(id).isPublicAccess());
    return ok();
  }
  
  public static Result isPublicAccess(Long id) {
  	if( Resource.get(id).isPublicAccess() ){
  		return ok("1");
  	} else {
  		return ok("0");
  	}
  }
}
