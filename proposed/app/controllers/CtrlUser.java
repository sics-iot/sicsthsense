package controllers;

//import models.Project;
//import models.Task;
//import models.User;
import java.util.HashMap;
import java.util.List;

import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

import play.*;

import play.core.Router.Routes;
import play.libs.F.*;
import play.libs.*;
import play.mvc.*;
import play.data.*;

import models.*;
import views.html.*;
import play.data.Form;

@Security.Authenticated(Secured.class)
public class CtrlUser extends Controller {
  
  static private Form<User> userForm = Form.form(User.class);
  
  static public User getUser() {
    return Secured.getCurrentUser();
  }
    
  public static Result get() {
    return ok(accountPage.render(getUser(), null));
  }
  
  public static Result getByName(String userName) {
    User user = User.getByUserName(userName);
    if(user == null) return notFound("User not found");
    return ok(accountPage.render(user, null));
  }
  
  public static Result edit() {
    return ok(accountPage.render(getUser(), userForm));
  }

	public static Result submit() {
		Form<User> theForm = userForm.bindFromRequest();
		if (theForm.hasErrors()) {
			return badRequest("Form errors: " + theForm.errors().toString());
		} else {
			User current = getUser();
			User submitted = theForm.get();
			submitted.id = current.id;
			submitted.email = current.email;
			try {
				//submitted.update();
				current.updateUser(submitted);

			} catch (Exception e) {
				return badRequest("Bad request");
			}
			return redirect(routes.CtrlUser.get());
		}
	}
    
}
