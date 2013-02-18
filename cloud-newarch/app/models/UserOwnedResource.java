package models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import play.data.validation.Constraints;
import play.db.ebean.Model;

@Entity
@Inheritance
@DiscriminatorColumn(length=16)
public class UserOwnedResource extends Model {

	 /**
	 * The serialization runtime associates with each serializable class 
	 * a version number, called a serialVersionUID
	 */
	private static final long serialVersionUID = -5054334579997918762L;

	@Id
	public Long id;
	
	@Column(nullable = false)
	@Constraints.Required
  @ManyToOne
  public User user;
  
	public Date creationDate;
	
	public String description="";
	
  /* List of users that can read this element */
	@ManyToMany(cascade = CascadeType.REMOVE)
	public List<User> sharedWithUsers = new ArrayList<User>();
	
	/* Is it publicly available to others? */
	public boolean publicAccess = false;
	
  public static Model.Finder<Long,UserOwnedResource> find = new Model.Finder<Long, UserOwnedResource>(Long.class, UserOwnedResource.class);

  public UserOwnedResource(User user) {
    this.user = user;
  }
  
  public UserOwnedResource() {
  	super();
  }
  
  /** Create a new instance and save it in the db*/
  public static UserOwnedResource create(UserOwnedResource res) {
  	if(res.user != null){
			res.save();
	  	res.saveManyToManyAssociations("sharedWithUsers");
	  	return res;
  	}
  	return null;
  }
  
  public static UserOwnedResource create(User user) {
			UserOwnedResource persisted = new UserOwnedResource(user);
	  	return create(persisted);
  }
  
	public static boolean isOwnedBy(UserOwnedResource res, User user) {
		if(res != null && res.user != null && user != null) {
			return user.id == res.user.id;
		}
		return false;
	}

	public static List<UserOwnedResource> all() {
		return find.where().orderBy("user").findList();
	}
	
	public static UserOwnedResource get(Long id) {
		return find.byId(id);
	}
	
  /**
   * Sharing helper methods
   * */

	public void setPublicAccess( Boolean pub ) {
		this.publicAccess = pub;
		if(this.id != 0) {
			this.update();
		}
	}
	
	public Boolean isPublicAccess() {
		return this.publicAccess;
	}
	
	public Boolean addShare(User user) {		
		try{
			if( user != null && user.exists() && sharedWithUsers != null && sharedWithUsers.add(user) ) {
				this.saveManyToManyAssociations( "sharedWithUsers" );
				return true;
			}
		} catch(Exception e) {
		}
		return false;
	}
	
	public Boolean isShare(User user) {
		return user != null && sharedWithUsers.contains(user);
	}
	
	public List<User> getShare() {
		return sharedWithUsers;
	}
	
	public Boolean removeShare(User user) {
		try{
			if( user != null && sharedWithUsers != null && sharedWithUsers.remove(user) ) {
				this.saveManyToManyAssociations( "sharedWithUsers" );
				return true;
			}
		} catch(Exception e) {
		}
		return false;
	}
	
  /** Persistence functions */
	public void save() {
		this.creationDate = new Date();
		super.save();
	}

	public void update() {
		//verify();
		super.update();
	}
}
