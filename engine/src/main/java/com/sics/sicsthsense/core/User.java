package com.sics.sicsthsense.core;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;
import java.lang.Double;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import javax.validation.constraints.*;

import org.apache.commons.codec.digest.DigestUtils;

import com.fasterxml.jackson.annotation.JsonProperty;

public class User {
	private long id;
	//@NotNull
	@JsonProperty
	private String username;
	@JsonProperty
	public String email;
	@JsonProperty
	public String password; // only for username/password login
	@JsonProperty
	public String firstName;
	@JsonProperty
	public String lastName;
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
	}
	public User(long id, String username) {
			this.id				= id;
			this.username = username;
	}
	public User(//long id, 
			String username,
			String firstName,
			String lastName,
			String description,
			Double latitude,
			Double longitude,
			Date creationDate,
			Date lastLogin //,
			//boolean admin
		) {
			//this.id							= id;
			this.username				= username;
			this.firstName			= firstName;
			this.lastName			= lastName;
			this.description		= description;
			this.latitude				= latitude;
			this.longitude			= longitude;
			this.creationDate	= creationDate;
			this.lastLogin			= lastLogin;
			//this.admin					= admin;
	}
	public User(
			//long id, 
			@JsonProperty("username") String username, @JsonProperty("firstName") String first_name, @JsonProperty("lastName") String last_name, @JsonProperty("description") String description, @JsonProperty("latitude") String latitude_string, @JsonProperty("longitude") String longitude_string, @JsonProperty("creationDate") String creation_date_string, @JsonProperty("lastLogin") String last_login_string) {
			this(//id, 
					username, first_name, last_name, description,
				Double.valueOf(latitude_string),
				Double.valueOf(longitude_string),
				new Date(),
				new Date() //,
				//admin_string.equals("true")
			);
				// set complex parameters that throw exception
			//this.last_login = parseStringToDate(last_login_string);
	//			this.creation_date= new SimpleDateFormat("YYYY-MM-DD kk:mm:ss", Locale.ENGLISH).parse(creation_date_string);
	}
	public Date parseStringToDate(String date_string) {
		try {
			return new SimpleDateFormat("YYYY-MM-DD kk:mm:ss", Locale.ENGLISH).parse(date_string);
		} catch (ParseException e) {
			System.out.println("Error: Date form database not parsed by java!");
			return new Date();
		}
	}

	public long		getId()					{ return id; }
	public String getUsername()		{ return username; }
	public String getEmail()      { return email; }
	public String getDescription(){ return description; }
	public String getToken()      { return token; }
	public String getFirstName()  { return firstName; }
	public String getLastName()   { return lastName; }
	public Double getLatitude()   { return latitude; }
	public Double getLongitude()  { return longitude; }
	public Date		getLastLogin()  { return lastLogin; }
	public boolean getAdmin()			{ return admin; }
	public Date	getCreationDate() { return creationDate; }

	public void		setId(long id)										{ this.id = id; }
	public void		setUsername(String username)			{ this.username = username; }
	public void		setEmail(String email)						{ this.email = email; }
	public void		setDescription(String description){ this.description = description; }
	public void		setToken()												{ this.token = token; }
	public void		setAdmin(boolean admin)						{ this.admin = admin; }
	public void		setPassword(String newPassword)		{ this.password = DigestUtils.md5Hex(newPassword); }
	//public void		setLastLogin(Date lastLogin)			{ this.lastLogin = lastLogin; }
	public void		setLastLogin(String lastLogin)				{ }
	public void		setCreationDate(String creationDate)	{ }
	//public void		setCreationDate(Date creationDate){ this.creationDate = creationDate; }
	public void		setAdmin()												{ }
	//public Boolean exists()               { return exists; }
	//public void		 setLastLogin(Date last_login){ this.last_login = last_login; }
	//public void		 setCreationDate(Date last_login){ this.last_login = last_login; }
	public boolean isAdmin()		          { return admin; }

	public String resetPassword() {
		String newPassword = new BigInteger(130,new SecureRandom()).toString(32);
		this.password = hash(newPassword);
		return this.password;
	}
	// perform the Password -> Hash transform
	public static String hash(String toHash) {
		return DigestUtils.md5Hex(toHash);
	}

	public Date updateLastLogin() {
		this.lastLogin = new Date();
		return this.lastLogin;
	}

	public String generateToken() {
		token = UUID.randomUUID().toString();
		return token;
	}

}
