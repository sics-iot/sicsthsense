package models;

import java.util.*;

import javax.persistence.*;

import play.db.ebean.*;
import play.data.format.*;
import play.data.validation.*;

import com.avaje.ebean.*;

@Entity 
@Table(name="user",
uniqueConstraints = {@UniqueConstraint(columnNames={"email"})})
public class User extends Model {

    @Id
    public Long id;
    
    @Formats.NonEmpty
    public String email;
    @Constraints.Required
    public String firstName;
    @Constraints.Required
    public String lastName;
    @Constraints.Required
    public String location;
    
    // -- Queries
    
    public static Model.Finder<Long,User> find = new Model.Finder(Long.class, User.class);
        
    public User(String email, String firstName, String lastName, String location) {
      this.email = email;
      this.firstName = firstName;
      this.lastName = lastName;
      this.location = location;
    }
    
    public static User create(User user) {
        user.save();
        return user;
    }
    
    public static boolean exists(Long id) {
      return find.byId(id) != null;
    }
    
    public static User get(Long id) {
      return find.byId(id);
    }
        
    public static User getByEmail(String email) {
      return find.where().eq("email", email).findUnique();
    }
        
    public static void delete(Long id) {
      find.ref(id).delete();
    }

}
