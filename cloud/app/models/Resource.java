package models;

import java.util.*;

import javax.persistence.*;

import org.codehaus.jackson.JsonNode;

import play.Logger;
import play.db.ebean.*;
import play.data.format.*;
import play.data.validation.*;

import com.avaje.ebean.*;

import controllers.Utils;
import play.libs.F.*;
import play.libs.WS;

@Entity 
@Table(name="resource",
uniqueConstraints = {
    @UniqueConstraint(columnNames={"end_point_id", "path"})
    }
)
public class Resource extends Model {
  
    @Id
    public Long id;
    
    @Constraints.Required
    public String path;
    @ManyToOne
    public EndPoint endPoint;
    @ManyToOne 
    public User user;
    
    public long pollingPeriod;
    public long lastPolled;
    public long lastUpdated;
            
    public static Model.Finder<Long,Resource> find = new Model.Finder(Long.class, Resource.class);
    
    public Resource(String path, EndPoint endPoint) {
      this.endPoint = endPoint;
      this.user = endPoint.getUser();
      this.path = Utils.concatPath(path);
      this.pollingPeriod = 0;
      this.lastPolled = 0;
      this.lastUpdated = 0;
    }
    
    public Boolean hasData() {
      return lastUpdated != 0;
    }
    
    public EndPoint getEndPoint() {
      return EndPoint.get(endPoint.id);
    }
    
    public User getUser() {
      return User.get(user.id);
    }
    
    public static List<Resource> all() {
      return find.all();
    }

    public static Resource getByPath(EndPoint endPoint, String path) {
      return find.where()
          .eq("endPoint", endPoint)
          .eq("path", Utils.concatPath(path))
          .findUnique();
    }
    
    public static List<Resource> getWithData() {
      return find.where()
          .gt("lastUpdated", 0)
          .findList();
    }
    
    public static List<Resource> getByUserWithData(User user) {
    return find.where()
        .gt("lastUpdated", 0)
        .eq("user", user)
        .findList();
   }
    
    public void post(float data, long time) {
      Logger.info("New data " + endPoint.label + path + ": " + data);
      DataPoint.add(this, data, time);
      lastUpdated = time;
      update();
    }
        
    public static Resource get(Long id) {
      return find.byId(id);
    }
    
    public static void delete(Long id) {
      //TODO should enable cascading instead
      clearStream(id);
      find.ref(id).delete();
    }
    
    public static void setPeriod(Long id, Long period) {
      Resource resource = get(id);
      if(resource != null) {
        resource.pollingPeriod = period;
        resource.update();
      }
    }
    
    public static void clearStream(Long id) {
      Resource resource = get(id);
      resource.lastPolled = 0;
      resource.lastUpdated = 0;
      resource.update();
      if(resource != null) {
        DataPoint.deleteByStream(resource);
      }
    }
    
    public static List<Resource> getByEndPoint(EndPoint endPoint) {
      return find.where()
          .eq("endPoint", endPoint)
          .findList();
    }
    
    public static Resource add(String path, EndPoint endPoint) {
      Resource resource = new Resource(path, endPoint);
      resource.save();
      return resource;
    }
    
    public static void deleteByEndPoint(EndPoint endPoint) {
      //TODO this is an ugly workaround, we need to find out how to SQL delete directly
      List<Resource> list = find.where()
          .eq("endPoint", endPoint)
          .findList();
      List<Long> ids = new LinkedList<Long>();
      for(Resource element: list) {
        ids.add(element.id);
      }
      for(Long id: ids) {
        delete(id); 
      }
    }
        
}

