package models;

import java.util.*;

import javax.persistence.*;

import org.codehaus.jackson.JsonNode;

import play.Logger;
import play.db.ebean.*;
import play.data.format.*;
import play.data.validation.*;

import com.avaje.ebean.*;
import play.libs.F.*;
import play.libs.WS;

@Entity 
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
    public long lastUpdate;
            
    public static Model.Finder<Long,Resource> find = new Model.Finder(Long.class, Resource.class);
    
    public Resource(String path, EndPoint endPoint) {
      this.endPoint = endPoint;
      this.user = endPoint.getUser();
      this.path = path;
      this.pollingPeriod = 0;
      this.lastUpdate = 0;
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

    public static List<Resource> getWithPolling() {
      return find.where()
          .gt("pollingPeriod", 0)
          .findList();
    }
    
    public static List<Resource> getByUserWithPolling(User user) {
      return find.where()
          .gt("pollingPeriod", 0)
          .eq("user", user)
          .findList();
    }
        
    public static long currentTime() {
      return new java.util.Date().getTime() / (1000*60);
    }
        
    public void periodic() {
      final EndPoint endPoint = getEndPoint();
      final long current = currentTime();
      final Resource resource = this;
      Logger.info("Periodic " + endPoint.label + "/" + path + " " + pollingPeriod + " " + (current-lastUpdate));
      if (current >= lastUpdate + pollingPeriod) {
        Logger.info("Now sampling " + endPoint.label + path);
        WS.url(endPoint.url + path).get().map(
          new Function<WS.Response, Long>() {
            public Long apply(WS.Response response) {
              long data = Long.parseLong(response.getBody());
              Logger.info("New data " + endPoint.label + path + ": " + data);
              DataPoint.add(resource, data, current);
              return data;
            }
          }
        );
        lastUpdate = current;
        update();
      }
    }
    
    public static void periodicAll() {
      List<Resource> withPolling = find.where()
          .gt("pollingPeriod", 0)
          .findList();
      for(Resource resource: withPolling) {
        resource.periodic();
      }      
    }
    
    public static Resource get(Long id) {
      return find.byId(id);
    }
    
    public static void delete(Long id) {
      find.byId(id).delete();
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
        
}

