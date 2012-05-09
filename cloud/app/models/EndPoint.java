package models;

import java.util.*;

import javax.persistence.*;

import org.codehaus.jackson.JsonNode;

import play.db.ebean.*;
import play.data.format.*;
import play.data.validation.*;

import com.avaje.ebean.*;
import play.libs.F.*;
import play.libs.WS;

@Entity 
public class EndPoint extends Model {
  
    @Id
    public Long id;
    
    public String label;
    @Constraints.Required
    public String url;
    public String uid;
        
    @ManyToOne
    public User user;
            
    public static Model.Finder<Long,EndPoint> find = new Model.Finder(Long.class, EndPoint.class);
    
    public EndPoint(User user, String url, String uid, String label) {
      this.user = user;
      this.url = url;
      this.uid = uid;
      this.label = label;
    }
    
    public User getUser() {
      return User.get(user.id);
    }
    
    public static List<EndPoint> getByUser(User user) {
      return find.where()
          .eq("user", user)
          .findList();
    }
        
    public static EndPoint get(Long id) {
      return find.byId(id);
    }
    
    public static void delete(Long id) {
      find.byId(id).delete();
    }
    
    public static List<EndPoint> all() {
      return find.all();
    }
    
    public static EndPoint register(User user, String url) {
      EndPoint endPoint = new EndPoint(user, url, null, url);
      endPoint.save();
      return endPoint;
    }

    public Boolean discover() {
      try {
        JsonNode json = WS.url(url + "/discover").get().get().asJson();
        uid = json.findPath("uid").getTextValue();
        for(JsonNode node: json.findPath("resources")) {
          Resource.add(node.getTextValue(), this);
        }
        update();
        return true;
      } catch (Exception e) {
        return false;
      }
    }
    
}

