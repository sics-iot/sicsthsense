package models;

import java.util.*;

import javax.persistence.*;

import play.db.ebean.*;
import play.data.format.*;
import play.data.validation.*;

import com.avaje.ebean.*;

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

}
