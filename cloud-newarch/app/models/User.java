package models;

import java.util.*;

import javax.persistence.*;

import play.db.ebean.*;
import play.data.format.*;
import play.data.validation.*;
import play.mvc.PathBindable;

import com.avaje.ebean.*;

//the table name "user" might be invalid for some db systems
@Entity
@Table(name = "user_account")
public class User extends Model implements  Comparable<User> { //PathBindable<User>,

	/**
	 * 
	 */
	private static final long serialVersionUID = 5178587449713353935L;

	@Id
	public Long id;

	@Column(length = 256, unique = true, nullable = false)
  @Constraints.MaxLength(256)
  @Constraints.Required
  @Constraints.Email
	public String email;

	//private List<String> linkedEmails = new ArrayList<String>(10);
	
	/** Secret token for authentication */
	private String token;

	public String getToken() {
		return token;
	}

	/** Secret token for session authentication */
	@Transient
	public String currentSessionToken;
	
	public String getEmail() {
		return email;
	}

	@Column(length = 256, unique = true, nullable = false)
  @Constraints.MaxLength(256)
  @Constraints.Required
	@Formats.NonEmpty
	protected String userName;

	public String getUserName() {
		return userName;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public String getLocation() {
		return location;
	}

	protected String firstName;
	protected String lastName;
	protected String location;

	@Column(nullable = false)
	public Date creationDate;
	
	public Date lastLogin;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
	public List<UserOwnedResource> ownedResources = new ArrayList<UserOwnedResource>();

	@ManyToMany(mappedBy = "sharedWithUsers")
	public List<UserOwnedResource> followedResources = new ArrayList<UserOwnedResource>();

	// -- Queries

	public static Model.Finder<Long, User> find = new Model.Finder<Long, User>(
			Long.class, User.class);

	public User(String email, String userName, String firstName, String lastName,
			String location) {
		this.creationDate = new Date();
		this.email = email.toLowerCase();
		this.userName = userName.toLowerCase();
		this.firstName = firstName;
		this.lastName = lastName;
		this.location = location;
	}

	public void updateUser(User user) {
		this.userName = user.userName.toLowerCase();
		this.firstName = user.firstName;
		this.lastName = user.lastName;
		this.location = user.location;
		update();
	}
	
	public User() {
		this.creationDate = new Date();
	}

	public static User create(User user) {		
		user.generateToken();
		user.save();
		// is this necessary? -YES!
		user.saveManyToManyAssociations("followedResources");
		return user;
	}

	public Date updateLastLogin() {
		this.lastLogin = new Date();
		save();
		return this.lastLogin;
	}
	public String generateToken() {
		token = UUID.randomUUID().toString();
		//save();
		return token;
	}

	public static User getByToken(String token) {
		if (token == null) {
			return null;
		}

		try {
			return find.where().eq("token", token).findUnique();
		} catch (Exception e) {
			return null;
		}
	}

	public static List<User> all() {
		return find.where().orderBy("userName asc").findList();
	}

	public static boolean exists(Long id) {
		return find.byId(id) != null;
	}

	public boolean exists() {
		return exists(id);
	}

	public static User get(Long id) {
		return find.byId(id);
	}

	public Long getId() {
		return new Long(id);
	}
	
	public static User getByEmail(String email) {
		return find.where().eq("email", email).findUnique();
	}

	public static User getByUserName(String userName) {
		return find.where().eq("user_name", userName).findUnique();
	}

	public static void delete(Long id) {
		find.ref(id).delete();
	}

	public void followResource(UserOwnedResource resource) {
		if (resource != null) {
			followedResources.add(resource);
		}
		this.saveManyToManyAssociations("followedResources");
	}

	public void unfollowResource(UserOwnedResource resource) {
		if (resource != null) {
			followedResources.remove(resource);
		}
		this.saveManyToManyAssociations("followedResources");
	}

	public boolean followsResource(UserOwnedResource resource) {
		return followedResources.contains(resource);
	}

	public void verify() {
		userName = userName.replaceAll("[^\\w.-]", "");
	}

	public void save() {
		verify();
		super.save();
	}

	public void update() {
		verify();
		super.update();
	}
	
//	@Override
//	public User bind(String key, String userName) {
//	// TODO check key?
//		User user = getByUserName(userName);
////		return user;
//		if (user != null) {
//			return user;
//		} else {
//			throw new IllegalArgumentException("User with id " + id + " not found");
//		}
//	}
//
//	@Override
//	public String unbind(String key) {
//		// TODO check key?
//		return userName;
//	}
//
//	@Override
//	public String javascriptUnbind() {
//	// TODO check key?
//		return "function(k,v) {\n" +
//						"    return v.userName;" +
//		        "}";
//	}

	public int compareTo(User user) {
    return token.compareTo(user.token);
  }
}
