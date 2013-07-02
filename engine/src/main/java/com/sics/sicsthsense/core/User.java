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
	@NotNull
	@JsonProperty
	private String username;
	@JsonProperty
	public String email;
	@JsonProperty
	public String password; // only for username/password login
	@JsonProperty
	public String first_name;
	@JsonProperty
	public String last_name;
	@JsonProperty
	public String description = "";
	@JsonProperty
	public Double latitude = 0.0;
	@JsonProperty
	public Double longitude = 0.0;
	@JsonProperty
	public Date creation_date;
	@JsonProperty
	public Date last_login;
	@JsonProperty
	private String token; /** Secret token for authentication */
	@JsonProperty // only decoded if the poster is admin
	private boolean admin;

	public User(long id, String username) {
			this.id				= id;
			this.username = username;
	}
	public User(//long id, 
			String username,
			String first_name,
			String last_name,
			String description,
			Double latitude,
			Double longitude,
			Date creation_date,
			Date last_login,
			boolean admin
		) {
			//this.id							= id;
			this.username				= username;
			this.first_name			= first_name;
			this.last_name			= last_name;
			this.description		= description;
			this.latitude				= latitude;
			this.longitude			= longitude;
			this.creation_date	= creation_date;
			this.last_login			= last_login;
			this.admin					= admin;
	}
	public User(
			//long id, 
			@JsonProperty("username") String username, @JsonProperty("first_name") String first_name, @JsonProperty("last_name") String last_name, @JsonProperty("description") String description, @JsonProperty("latitude") String latitude_string, @JsonProperty("longitude") String longitude_string, @JsonProperty("creation_date") String creation_date_string, @JsonProperty("last_login") String last_login_string, @JsonProperty("admin") String admin_string) {
			this(//id, 
					username, first_name, last_name, description,
				Double.valueOf(latitude_string),
				Double.valueOf(longitude_string),
				new Date(),
				new Date(),
				admin_string.equals("true")
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

	public long getId()					{ return id; }
	public void setId(long id)	{ this.id = id; }
	public String getUsername()								{ return username; }
	public void setUsername(String username)	{ this.username = username; }

	public String  getEmail()             { return email; }
	public void    setEmail(String email) { this.email=email; }
	public String  getToken()             { return token; }
	public String  getFirstName()         { return first_name; }
	public String  getLastName()          { return last_name; }
	public String  description()          { return description; }
	public Double  getLatitude()          { return latitude; }
	public Double  getLongitude()         { return longitude; }
	//public Boolean exists()               { return exists; }
	//public void		 setLastLogin(Date last_login){ this.last_login = last_login; }
	//public void		 setCreationDate(Date last_login){ this.last_login = last_login; }
	public boolean isAdmin()		          { return admin; }
	public void		 setAdmin(boolean admin){ this.admin = admin; }

	public void setPassword(String newPassword) {
		this.password = DigestUtils.md5Hex(newPassword);
	}
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
		this.last_login = new Date();
		return this.last_login;
	}

	public String generateToken() {
		token = UUID.randomUUID().toString();
		return token;
	}

}
