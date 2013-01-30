/**
 * Copyright 2012 Jorge Aliss (jaliss at gmail dot com) - twitter: @jaliss
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package service;

import play.Application;
import play.Logger;
import play.libs.OpenID;
import play.libs.OpenID.UserInfo;
import play.mvc.Call;
import scala.Option;
import securesocial.core.Identity;
import securesocial.core.UserId;
import securesocial.core.java.BaseUserService;

import securesocial.core.java.Token;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import models.User;
import controllers.routes;

/**
 * A Sample In Memory user service in Java
 * 
 * Note: This is NOT suitable for a production environment and is provided only
 * as a guide. A real implementation would persist things in a database
 */
public class SenseToUserService extends BaseUserService {
	public SenseToUserService(Application application) {
		super(application);
	}

	/**
	 * Saves the user. This method gets called when a user logs in. This is your
	 * chance to save the user information in your backing store.
	 * 
	 * @param user
	 */
	@Override
	public void doSave(Identity userIdentity) {
		OAuth1Info auth1info = null;
		OAuth2Info auth2info = null;
		String email = null;
		User user = null;

		if (userIdentity.oAuth1Info().isDefined()) {
			// there is a value
			auth1info = userIdentity.oAuth1Info().get();
		} else if (userIdentity.oAuth2Info().isDefined()) {
			// there is a value
			auth2info = userIdentity.oAuth2Info().get();
		}

//		String email = userIdentity.email().isDefined() ? userIdentity.email()
//				.get() : userIdentity.id().id() + "@" + userIdentity.id().providerId();
//		if (email != "Not available" && email != null) {
//			user = User.getByEmail(email);
//		}
		email = userIdentity.id().id() + "@" + userIdentity.id().providerId();
		user = User.getByEmail(email);
		
		
		if (user == null) {
			user = User.create(new User(email, email, userIdentity.firstName(),
					userIdentity.lastName(), userIdentity.avatarUrl()));
			destination = routes.CtrlUser.edit();
			Logger.info("created");
		}
		Logger.info("ok");
		//session("id", user.id.toString());
	}
	
	 /**
   * Saves a token
   */
	@Override
	public void doSave(Token token) {
		
	}
	
	/**
   * Finds an Identity in the backing store.       
   * @return an Identity instance or null if no user matches the specified id
   */
	@Override
	public Identity doFind(UserId userId) {
		return User.getByUserName(userId.id() + "@" + userId.providerId());
	}
	
	 /**
   * Finds a token by id
   *
   * Note: If you do not plan to use the UsernamePassword provider just provide en empty
   * implementation
   *
   * @return a Token instance or null if no token matches the id
   */
	@Override
	public Token doFindToken(String tokenId) {
		
	}

  /**
   * Finds an identity by email and provider id.
   *
   * Note: If you do not plan to use the UsernamePassword provider just provide en empty
   * implementation.
   *
   * @param email - the user email
   * @param providerId - the provider id
   * @return an Identity instance or null if no user matches the specified id
   */
	@Override
	public Identity doFindByEmailAndProvider(String email, String providerId) {
	}
	
	 /**
   * Deletes a token
   *
   * Note: If you do not plan to use the UsernamePassword provider just provide en empty
   * implementation
   *
   * @param uuid the token id
   */
	@Override
	public void doDeleteToken(String uuid) {
	}

  /**
   * Deletes a token
   *
   * Note: If you do not plan to use the UsernamePassword provider just provide en empty
   * implementation
   *
   * @param uuid the token id
   */
	@Override
	public void doDeleteExpiredTokens() {
	}

}
