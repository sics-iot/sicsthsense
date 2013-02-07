package service;

import models.User;
import play.Application;

import com.feth.play.module.pa.user.AuthUser;
import com.feth.play.module.pa.user.AuthUserIdentity;
import com.feth.play.module.pa.service.UserServicePlugin;

public class MyUserServicePlugin extends UserServicePlugin {

	/*
	 * The UserService interface works with AuthUser objects. The combination of
	 * getId and getProvider from AuthUser can be used to identify an user.
	 * 
	 * The getLocalIdentity function gets called on any login to check whether
	 * the session user still has a valid corresponding local user. Returns the
	 * local identifying object if the auth provider/id combination has been
	 * linked to a local user account already or null if not. The save method of
	 * the UserServicePlugin is called, when the user logs in for the first time
	 * (i.e. getLocalIdentity returned null for this AuthUser). This method
	 * should store the user to the database and return an object identifying
	 * the user. The update method is called when a user logs in. You might make
	 * profile updates here with data coming from the login provider or bump a
	 * last-logged-in date. The merge function should merge two different local
	 * user accounts to one account. Returns the user to generate the session
	 * information from. The link function links a new account to an existing
	 * local user. Returns the auth user to log in with.
	 */
	public MyUserServicePlugin(final Application app) {
		super(app);
	}

	@Override
	public Object save(final AuthUser authUser) {
		final boolean isLinked = User.existsByAuthUserIdentity(authUser);
		if (!isLinked) {
			return User.create(authUser).id;
		} else {
			// we have this user already, so return null
			return null;
		}
	}

	@Override
	public Object getLocalIdentity(final AuthUserIdentity identity) {
		// For production: Caching might be a good idea here...
		// ...and dont forget to sync the cache when users get deactivated/deleted
		final User u = User.findByAuthUserIdentity(identity);
		if(u != null) {
			return u.id;
		} else {
			return null;
		}
	}

	@Override
	public AuthUser merge(final AuthUser newUser, final AuthUser oldUser) {
		if (!oldUser.equals(newUser)) {
			User.merge(oldUser, newUser);
		}
		return oldUser;
	}

	@Override
	public AuthUser link(final AuthUser oldUser, final AuthUser newUser) {
		User.addLinkedAccount(oldUser, newUser);
		return null;
	}

}
