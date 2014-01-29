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

import models.Stream;
import models.User;
import play.Logger;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.accountPage;
import views.html.accountsPage;

@Security.Authenticated(Secured.class)
public class CtrlUser extends Controller {

	static private Form<User> userForm = Form.form(User.class);

	static public User getUser() {
		return Secured.getCurrentUser();
	}

	public static Result get() {
		return ok(accountPage.render(getUser(), null, "", ""));
	}

	public static Result getByName(String username) {
		User user = User.getByUserName(username);
		if (user == null) {
			return notFound("User not found");
		}
		return ok(accountPage.render(user, null, "", ""));
	}

	public static Result edit() {
		return ok(accountPage.render(getUser(), userForm, "", ""));
	}

	// submitting a modified user detail form
	public static Result submit() {
		Form<User> theForm = userForm.bindFromRequest();
		if (theForm.hasErrors()) {
			return badRequest("Form errors: " + theForm.errors().toString());
		} else {
			User submitted = theForm.get();
			User current = getUser();
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
			return ok(accountPage.render(getUser(), userForm, "", "Token reset"));
		}
		return ok(accountPage.render(getUser(), userForm, "", "Token not reset"));
	}
	
	@Security.Authenticated(Secured.class)
	public static Result resetPassword(Boolean confirmed) {
		User currentUser = Secured.getCurrentUser();
		if (confirmed) {
			String newPassword = currentUser.resetPassword();
			currentUser.update();
			// and register in database
			return ok(accountPage.render(getUser(), userForm, newPassword, "Password has been reset."));
		}
		return ok(accountPage.render(getUser(), userForm, "", "Password has not been reset"));

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

	public static Result view(String username) {
		final User user = User.getByUserName(username);
		if(user == null) return notFound();
		return ok(accountsPage.render(user,userForm,"",""));
	}
}
