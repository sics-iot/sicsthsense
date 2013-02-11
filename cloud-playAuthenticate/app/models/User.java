package models;

import java.util.*;

import javax.persistence.*;

import play.db.ebean.*;
import play.data.format.*;
import play.data.validation.*;

import com.avaje.ebean.*;
import com.feth.play.module.pa.user.AuthUser;
import com.feth.play.module.pa.user.AuthUserIdentity;
import com.feth.play.module.pa.user.EmailIdentity;
import com.feth.play.module.pa.user.NameIdentity;

@Entity 
@Table(name="user",
uniqueConstraints = {
    @UniqueConstraint(columnNames={"user_name"}),
    @UniqueConstraint(columnNames={"email"})
    }
)
public class User extends Model {

    @Id
    public Long id;
    
    @Formats.NonEmpty
    public String email;
    
    @Formats.NonEmpty
    public String userName;
    public String firstName;
    public String lastName;
    public String location;
    
  	@OneToMany(mappedBy="user")
  	public List<EndPoint> endPoints; 
  	
    @ManyToMany
    public List<Resource> followedResources = new ArrayList<Resource>();
    
    @ManyToMany
    public List<EndPoint> followedEndPoints = new ArrayList<EndPoint>();
    
    /* For PlayAuthenticate library */
	/* Each user should be able to log in using different accounts */
    @OneToMany(cascade = CascadeType.ALL, mappedBy="user")
	public List<LinkedAccount> linkedAccounts;
	/* Is this the primary user account or the merged one? */
    public boolean active;
    public boolean emailValidated;
    
    // -- Queries
    
    public static Model.Finder<Long,User> find = new Model.Finder<Long,User>(Long.class, User.class);
        
    public User(String email, String userName, String firstName, String lastName, String location) {
      this.email = email;
      this.userName = userName;
      this.firstName = firstName;
      this.lastName = lastName;
      this.location = location;
    }
    
    public static User create(User user) {
        user.save();
        //is this necessary?
        user.saveManyToManyAssociations("followedResources");
        user.saveManyToManyAssociations("followedEndPoints");
        return user;
    }
    
    public static List<User> all() {
      return find.where()
          .orderBy("userName asc")
          .findList();
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
        
    public static User getByEmail(String email) {
      return find.where().eq("email", email).findUnique();
    }
    
    public static User getByUserName(String userName) {
      return find.where().eq("user_name", userName).findUnique();
    }
        
    public static void delete(Long id) {
      find.ref(id).delete();
    }

    public List<Resource> followedResources() {
      Collections.sort(followedResources);
      return followedResources;
    }
    
    public void followResource(Resource resource) {
      if(resource != null) {
        followedResources.add(resource);
      }
      this.saveManyToManyAssociations("followedResources");
    }
    
    public void unfollowResource(Resource resource) {
      if(resource != null) {
        followedResources.remove(resource);
      }
      this.saveManyToManyAssociations("followedResources");
    }
    
    public boolean followsResource(Resource resource) {
      return followedResources.contains(resource);
    }
    
    public List<EndPoint> followedEndPoints() {
      Collections.sort(followedEndPoints);
      return followedEndPoints;
    }
    
    public void followEndPoint(EndPoint endPoint) {
      if(endPoint != null) {
        followedEndPoints.add(endPoint);
      }
      this.saveManyToManyAssociations("followedEndPoints");
    }
    
    public void unfollowEndPoint(EndPoint endPoint) {
      if(endPoint != null) {
        followedEndPoints.remove(endPoint);
      }
      this.saveManyToManyAssociations("followedEndPoints");
    }
    
    public boolean followsEndPoint(EndPoint endPoint) {
      return followedEndPoints.contains(endPoint);
    }
    
	// Liam: currently requires naughty embedded HTML, can only be
	// rectified with an appropriate architecture discussion
	public static List<String> getLabelsByUser(User user) {
		List<String> labels = new ArrayList<String>();
		// pull devices with a label
		List<EndPoint> devices = EndPoint.getByUser(user);
		for (EndPoint device: devices) {
			String url ="/"+user.userName+"/"+device.label; 
			labels.add(url);
		}
		// pull resources with a label
		List<Resource> resources = Resource.getByUser(user);
		for (Resource resource: resources) {
			String url = "/"+user.userName+"/"+resource.label;
			labels.add(url);
		}
		return labels;
	}

    public void verify() {
      userName = userName.replaceAll( "[^\\w.-]", "" );
    }
    
    public void save() {
      verify();
      super.save();
    }
    
    public void update() {
      verify();
      super.update();
    }
  
    /* For PlayAuthenticate library */
    public static boolean existsByAuthUserIdentity(
			final AuthUserIdentity identity) {
		final ExpressionList<User> exp = getAuthUserFind(identity);
		return exp.findRowCount() > 0;
	}

	private static ExpressionList<User> getAuthUserFind(
			final AuthUserIdentity identity) {
		return find.where().eq("active", true)
				.eq("linkedAccounts.providerUserId", identity.getId())
				.eq("linkedAccounts.providerKey", identity.getProvider());
	}

	public static User findByAuthUserIdentity(final AuthUserIdentity identity) {
		if (identity == null) {
			return null;
		}
		return getAuthUserFind(identity).findUnique();
	}

	public void merge(final User otherUser) {
		for (final LinkedAccount acc : otherUser.linkedAccounts) {
			this.linkedAccounts.add(LinkedAccount.create(acc));
		}
		// do all other merging stuff here - like resources, etc.

		// deactivate the merged user that got added to this one
		otherUser.active = false;
		Ebean.save(Arrays.asList(new User[] { otherUser, this }));
	}

	public static User create(final AuthUser authUser) {
		
		boolean active = true, emailValidated = false;
		String email = "", firstName="", lastName="", userName="", location=""; 

		if (authUser instanceof NameIdentity) {
			final NameIdentity identity = (NameIdentity) authUser;
			final String name = identity.getName();
			if (name != null) {
				firstName = name.split(" ")[0];
				lastName = name.split(" ")[1];
			}
		}
		if (authUser instanceof EmailIdentity) {
			final EmailIdentity identity = (EmailIdentity) authUser;
			// Remember, even when getting them from FB & Co., emails should be
			// verified within the application as a security breach there might
			// break your security as well!
			email = identity.getEmail();
		} else {
			email = authUser.toString();
		}
		userName = email. replace('@', '_');
		 
		User user = new User(email, userName, firstName, lastName, location);
		user.linkedAccounts = Collections.singletonList(LinkedAccount
				.create(authUser));		
		return User.create(user);
	}

	public static void merge(final AuthUser oldUser, final AuthUser newUser) {
		User.findByAuthUserIdentity(oldUser).merge(
				User.findByAuthUserIdentity(newUser));
	}

	public Set<String> getProviders() {
		final Set<String> providerKeys = new HashSet<String>(
				linkedAccounts.size());
		for (final LinkedAccount acc : linkedAccounts) {
			providerKeys.add(acc.providerKey);
		}
		return providerKeys;
	}

	public static void addLinkedAccount(final AuthUser oldUser,
			final AuthUser newUser) {
		final User u = User.findByAuthUserIdentity(oldUser);
		u.linkedAccounts.add(LinkedAccount.create(newUser));
		u.save();
	}
	
	public static User findByEmail(final String email) {
		return getEmailUserFind(email).findUnique();
	}

	private static ExpressionList<User> getEmailUserFind(final String email) {
		return find.where().eq("active", true).eq("email", email);
	}

	public LinkedAccount getAccountByProvider(final String providerKey) {
		return LinkedAccount.findByProviderKey(this, providerKey);
	}


}
