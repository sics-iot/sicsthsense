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
		return ok(accountPage.render(getUser(), null, ""));
	}

	public static Result getByName(String userName) {
		User user = User.getByUserName(userName);
		if (user == null)
			return notFound("User not found");
		return ok(accountPage.render(user, null, ""));
	}

	public static Result edit() {
		return ok(accountPage.render(getUser(), userForm, ""));
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
				// submitted.update();
				current.updateUser(submitted);

			} catch (Exception e) {
				return badRequest("Bad request");
			}
			return redirect(routes.CtrlUser.get());
		}
	}
	
	@Security.Authenticated(Secured.class)
	public static Result resetToken(Boolean confirmed) {
		User currentUser = Secured.getCurrentUser();
		if (confirmed) {
			currentUser.generateToken();
			currentUser.update();
		}
		return ok(accountPage.render(getUser(), userForm, ""));
	}
	
	@Security.Authenticated(Secured.class)
	public static Result resetPassword(Boolean confirmed) {
		User currentUser = Secured.getCurrentUser();
		if (confirmed) {
			String newPassword = currentUser.resetPassword();
			currentUser.update();
			// and register in database
			return ok(accountPage.render(getUser(), userForm, newPassword));
		}
		return ok(accountPage.render(getUser(), userForm, ""));

	}

	
	@Security.Authenticated(Secured.class)
	public static Result followStream(Long id, Boolean follow) {
		Logger.info("Follow stream request: " + follow);
		final User user = Secured.getCurrentUser();
		// if(user == null) return notFound();
		Stream stream = Stream.get(id);
		if (follow) {
			user.followStream(stream);
		} else {
			user.unfollowStream(stream);
		}
		Logger.info("Follow stream result: " + user.isfollowingStream(stream));
		return ok(Boolean.toString(user.isfollowingStream(stream)));
	}
	
	@Security.Authenticated(Secured.class)
	public static Result isFollowingStream(Long id) {
		final User user = Secured.getCurrentUser();
		if(user == null) return notFound();
		Stream stream = Stream.get(id);
		return ok(Boolean.toString(user.isfollowingStream(stream)));
	}

}
