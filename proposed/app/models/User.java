package models;

import java.util.*;

import javax.persistence.*;

import play.Logger;
import play.db.ebean.*;
import play.data.format.*;
import play.data.validation.*;
import play.mvc.PathBindable;

import com.avaje.ebean.*;

//the table name "user" might be invalid for some db systems
@Entity
@Table(name = "users")
public class User extends Model implements Comparable<User> { //PathBindable<User>,
	/** User class, contains all personal information
	 */
	@Id
	public Long id;

	@Column(length = 255, unique = true, nullable = false)
	@Constraints.MaxLength(255)
	@Formats.NonEmpty
	@Constraints.Email
	public String email;

	@Column(length = 255, unique = true, nullable = false)
	@Constraints.MaxLength(255)
	@Constraints.Required
	@Formats.NonEmpty
	public String userName;
	public String firstName;
	public String lastName;
	public String location;
	private static final long serialVersionUID = 5178587449713353935L;

	@Column(nullable = false)
	public Date creationDate;
	public Date lastLogin;
	
	@OneToMany(mappedBy = "owner")
	public List<Source> sourceList = new ArrayList<Source>();
	@OneToMany(mappedBy = "owner")
	public List<Stream> streamList = new ArrayList<Stream>();
	@OneToMany(mappedBy = "owner")
	public List<Actuator> actuatorList = new ArrayList<Actuator>();
	@OneToMany(mappedBy = "owner")
	public List<Vfile> fileList = new ArrayList<Vfile>();
	
  @ManyToMany
  public List<Stream> followedStreams = new ArrayList<Stream>();
	
	/** Secret token for session authentication */
	@Transient
	public String currentSessionToken;
	/** Secret token for authentication */
	private String token;

	
	public static Model.Finder<Long, User> find = new Model.Finder<Long, User>(Long.class, User.class);


	public User(String email, String userName, String firstName, String lastName,
			String location) {
		this.creationDate = new Date();
		this.email = email.toLowerCase();
		this.userName = userName.toLowerCase();
		this.firstName = firstName;
		this.lastName = lastName;
		this.location = location;
	}
	public User() {
		this.creationDate = new Date();
	}

	public String getEmail() { return email; }
	public String getToken() { return token; }
	public String getUserName() { return userName; }
	public String getFirstName() { return firstName; }
	public String getLastName() { return lastName; }
	public String getLocation() { return location; }
	public Long getId() { return new Long(id); }
	public boolean exists() { return exists(id); }

	public void updateUser(User user) {
		this.userName = user.userName.toLowerCase();
		this.firstName = user.firstName;
		this.lastName = user.lastName;
		this.location = user.location;
		update();
	}

	public Date updateLastLogin() {
		this.lastLogin = new Date();
		update();
		return this.lastLogin;
	}
	public String generateToken() {
		token = UUID.randomUUID().toString();
		//save();
		return token;
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
	
	public int compareTo(User user) {
		return token.compareTo(user.token);
	}
	
	public boolean equals(User user) {
		return user!=null && token.equals(user.token) && this.id == user.id;
	}

  public void followStream(Stream stream) {
    if(stream != null && stream.id > 0L && !followedStreams.contains(stream)) {
      followedStreams.add(stream);
    }
    this.saveManyToManyAssociations("followedStreams");
  }
  
  public void unfollowStream(Stream stream) {
    if(stream != null && stream.id > 0L) {
      followedStreams.remove(stream);
    }
    this.saveManyToManyAssociations("followedStreams");
  }
  
  public boolean isfollowingStream(Stream stream) {
   return (stream != null) && followedStreams.contains(stream);
  }
  
  public List<Stream> followedStreams() {
  	return followedStreams;
  }

	public void sortStreamList() {
		Logger.info("Sorting StreamList");
		Collections.sort(streamList);
	}
  
	public static User create(User user) {		
		user.generateToken();
		user.save();
		// is this necessary? -YES!
		user.saveManyToManyAssociations("followedStreams");
		return user;
	}

	public static User getByToken(String token) {
		if (token == null) { return null; }
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

	public static User get(Long id) {
		return (id==null) ? null : find.byId(id);
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
}
