package models;

import java.util.*;
import play.mvc.*;

import javax.persistence.*;

import org.codehaus.jackson.JsonNode;

import play.db.ebean.*;
import play.data.format.*;
import play.data.validation.*;

import com.avaje.ebean.*;
import play.libs.F.*;
import play.libs.WS;

@Entity
@Table(name="end_point",
uniqueConstraints = {
    @UniqueConstraint(columnNames={"user_id", "label"})
    }
)
public class EndPoint extends Model {
  
    @Id
    public Long id;
    
    @Constraints.Required
    public String label;
    public String url;
    public String uid;
    public String description;
    public String location;
        
    @ManyToOne 
    public User user;
            
    public static Model.Finder<Long,EndPoint> find = new Model.Finder(Long.class, EndPoint.class);
    
    public EndPoint(User user, String url, String uid, String label, String description, String location) {
      this.user = user;
      this.url = url;
      this.uid = uid;
      this.label = label;
      this.description = description;
      this.location = location;
    }
    
    public User getUser() {
      return User.get(user.id);
    }
    
    public static List<EndPoint> getByUser(User user) {
      return find.where()
          .eq("user", user)
          .findList();
    }
    
    public static EndPoint getByLabel(User user, String label) {
      return find.where()
          .eq("user", user)
          .eq("label", label)
          .findUnique();
    }
    
    public String getTextDescription() {
      String desc = description == null ? "no description" : description;
      String loc = location == null ? "no location" : location;
      return desc + ", " + loc;
    }
        
    public static EndPoint get(Long id) {
      return find.byId(id);
    }
    
    public static void delete(Long id) {
      //TODO should enable cascading instead
      EndPoint endPoint = find.byId(id);
      Resource.deleteByEndPoint(endPoint);
      find.byId(id).delete();
    }
    
    public static List<EndPoint> all() {
      return find.all();
    }
    
    public static EndPoint register(User user, String label, String url) {
      EndPoint endPoint = new EndPoint(user, url, null, label, null, null);
      endPoint.save();
      return endPoint;
    }

    public Resource getOrCreateResource(String path) {
      Resource resource = Resource.getByPath(this, path);
      if(resource == null) {
        resource = Resource.add(path, this);
      }
      return resource;
    }

    public static EndPoint getOrCreateByLabel(User user, String label) {
      EndPoint endPoint = getByLabel(user, label);
      if(endPoint == null) {
        endPoint = EndPoint.register(user, label, null);
      }
      return endPoint;
    }
    
}

