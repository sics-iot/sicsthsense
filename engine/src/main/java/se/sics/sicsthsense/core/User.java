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
package se.sics.sicsthsense.core;

import java.util.Set;
import java.util.UUID;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.text.SimpleDateFormat;
import java.text.ParseException;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import com.google.common.base.Objects;
import com.google.common.collect.Sets;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;


import se.sics.sicsthsense.model.openid.DiscoveryInformationMemento;
import se.sics.sicsthsense.model.security.*;


/**
 * <p>Simple representation of a User to provide the following to Resources:<br>
 * <ul>
 * <li>Storage of user state</li>
 * </ul>
 * </p>
 */
/*@JsonPropertyOrder({
  "id",
  "username",
  "passwordDigest",
  "firstName",
  "lastName",
  "email",
  "openIDIdentifier",
  "authorities"
})*/
@JsonInclude(Include.NON_NULL)
public class User {

  /** * <p>Unique identifier for this entity</p> */
	@JsonProperty
  private long id;
	@JsonProperty
  private String username;

  /** * <p>A user password (not plaintext and optional for anonymity reasons)</p> */
  @JsonProperty
  protected String passwordDigest = null;

  /** * A first name */
  @JsonProperty
  private String firstName;

  /** * A last name */
  @JsonProperty
  private String lastName;

  /**
   * <p>The OpenID discovery information used in phase 1 of authenticating against an OpenID server</p>
   * <p>Once the OpenID identifier is in place, this can be safely deleted</p>
   */
  @JsonIgnore
  private DiscoveryInformationMemento openIDDiscoveryInformationMemento;

  @JsonProperty
  private String openIDIdentifier;

  @JsonIgnore
  private UUID sessionToken;

  /**The authorities for this User (an unauthenticated user has no authorities) */
  @JsonIgnore
  private Set<Authority> authorities = Sets.newHashSet();

	@JsonProperty
	public String email;
	@JsonProperty
	public String password; // only for username/password login
	@JsonProperty
	public String description = "";
	@JsonProperty
	public Double latitude = 0.0;
	@JsonProperty
	public Double longitude = 0.0;
	@JsonProperty
	public Date creationDate;
	@JsonProperty
	public Date lastLogin;
	@JsonProperty
	private String token; /** Secret token for authentication */
	@JsonProperty // only decoded if the poster is admin
	private boolean admin;

  public User() {
		this.creationDate = new Date();
		this.token = UUID.randomUUID().toString();
		getAuthorities().add(Authority.ROLE_PUBLIC);
	}
	public User(long id,
			String username,
			String email,
			String passwordDigest,
			String firstName,
			String lastName,
			String description,
			Double latitude,
			Double longitude,
			Date creationDate,
			Date lastLogin,
			String token
		) {
			this();
			this.id							= id;
			this.username				= username;
			this.email          = email;

			// when making a user, ensure the raw password is never stored
			//if (password!=null) { this.passwordDigest	= new String(Hex.encodeHex(DigestUtils.md5(password))); }
			this.passwordDigest	= passwordDigest;
			this.firstName			= firstName;
			this.lastName				= lastName;
			this.description		= description;
			this.latitude				= latitude;
			this.longitude			= longitude;
			this.creationDate		= creationDate;
			this.lastLogin			= lastLogin;
			this.token					= token;
	}

	/*
	public User( long id,
			String username, String first_name, String last_name, String description, Double latitude, Double longitude, String creation_date_string, String last_login_string, String token) {
			this(id, username, first_name, last_name, description,
				latitude,
				longitude,
				new Date(),
				new Date()
				//admin_string.equals("true")
			);
			this.token					= token;
				// set complex parameters that throw exception
			//this.last_login = parseStringToDate(last_login_string);
	//			this.creation_date= new SimpleDateFormat("YYYY-MM-DD kk:mm:ss", Locale.ENGLISH).parse(creation_date_string);
	}
*/
	public Date parseStringToDate(String date_string) {
		try {
			return new SimpleDateFormat("YYYY-MM-DD kk:mm:ss", Locale.ENGLISH).parse(date_string);
		} catch (ParseException e) {
			System.out.println("Error: Date form database not parsed by java!");
			return new Date();
		}
	}

	// pull all user details from the parameter user into this object
	public void update(User user) {
		this.firstName = user.firstName;
		this.username  = user.username;
		this.lastName  = user.lastName;
		this.email     = user.email;
		this.latitude  = user.latitude;
		this.longitude = user.longitude;
		this.token     = user.token;
		this.password  = user.password;
		this.passwordDigest  = user.passwordDigest;
	}

	// XXX Need to hash and check password!
	public boolean hasPassword(String password) {
		return true;
	}
	public boolean isAuthorised(String key) {
		if (this.token.equals(key)) {
			return true;
		}
		return false;
	}

	/*public List<Resource> getResources() {
		List<Resource> resources = new ArrayList<Resource>();
		return storage.findResourcesByOwnerId(getId());
		return resources;
	}*/

	//
	// Get set methods
	//

  public long		getId()										{ return id; }
  public String getFirstName()						{ return firstName; }
  public String getLastName()							{ return lastName; }
  public String getEmail()								{ return email; }
  public Set<Authority> getAuthorities()	{ return authorities; }
  /** * @return The user name to authenticate with the client */
  public String getUsername()							{ return username; }
  /** * @return The digested password to provide authentication between the user and the client */
  public String getPasswordDigest()				{ return passwordDigest; }
  public String getPassword()							{ return password; }
  /** * @return The OpenID identifier */
  public String getOpenIDIdentifier()			{ return openIDIdentifier; }
  /** * @return The session key */
  public UUID		getSessionToken()					{ return sessionToken; }
  public String	getToken()								{ return token; }
  /** * @return The OpenID discovery information (phase 1 of authentication) */
  public DiscoveryInformationMemento getOpenIDDiscoveryInformationMemento() { return openIDDiscoveryInformationMemento; }


  public void setId(long id)																{ this.id = id; }
  public void setUsername(String username)									{ this.username = username; }
  public void setFirstName(String firstName)								{ this.firstName = firstName; }
  public void setLastName(String lastName)									{ this.lastName = lastName; }
  public void setEmail(String email)												{ this.email = email; }
  public void setAuthorities(Set<Authority> authorities)		{ this.authorities = authorities; }
  public void setOpenIDIdentifier(String openIDIdentifier)	{ this.openIDIdentifier = openIDIdentifier; }
  public void setSessionToken(UUID sessionToken)						{ this.sessionToken = sessionToken; }
  public void setToken(String token)													{ this.token = token; }
  /** * <h3>Note that it is expected that Jasypt or similar is used prior to storage</h3>
   * @param passwordDigest The password digest */
  public void setPasswordDigest(String passwordDigest)			{ this.passwordDigest = passwordDigest; }
  public void setPassword(String password)			            { this.password = password; }
  public void setOpenIDDiscoveryInformationMemento(DiscoveryInformationMemento openIDDiscoveryInformationMemento) {
    this.openIDDiscoveryInformationMemento = openIDDiscoveryInformationMemento;
  }

	public boolean hasSessionToken() { return sessionToken!=null; }
  public boolean hasAllAuthorities(Set<Authority> requiredAuthorities) { return authorities.containsAll(requiredAuthorities); }
  public boolean hasAuthority(Authority authority) { return hasAllAuthorities(Sets.newHashSet(authority)); }


  @Override
  public String toString() {
    return Objects.toStringHelper(this)
      .add("username", username)
      .add("password", "**********")
      .add("passwordDigest", getPasswordDigest())
      .add("email", email)
      .add("openIDIdentifier", openIDIdentifier)
  //    .add("sessionToken", sessionToken)
      .add("firstName", firstName)
      .add("lastName", lastName)
      .toString();
  }

}
