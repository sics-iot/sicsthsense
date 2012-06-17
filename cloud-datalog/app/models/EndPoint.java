package models;

import java.util.*;

import javax.persistence.*;

import controllers.Utils;

import play.db.ebean.*;
import play.data.validation.*;

@Entity
@Table(name="end_point",
uniqueConstraints = {
    @UniqueConstraint(columnNames={"user_id", "label"})
    }
)
public class EndPoint extends Model implements Comparable<EndPoint> {
  
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
            
    public static Model.Finder<Long,EndPoint> find = new Model.Finder<Long, EndPoint>(Long.class, EndPoint.class);
    
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
    
    public String fullPath() {
      return Utils.concatPath(user.userName, label);
    }
    
    public static List<EndPoint> getByUser(User user) {
      return find.where()
          .eq("user", user)
          .orderBy("label")
          .findList();
    }
    
    public static EndPoint getByLabel(User user, String label) {
      return find.where()
          .eq("user", user)
          .eq("label", label)
          .findUnique();
    }
    
    public String getTextDescription() {
      String desc = (description == null || description.length()==0) ? "no description" : description;
      String loc = (location == null  || location.length()==0) ? "no location" : location;
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
      return find.where()
          .orderBy("user.userName")
          .findList();
    }
    
    public static EndPoint register(User user, String label, String url) {
      EndPoint endPoint = new EndPoint(user, url, null, label, null, null);
      try { endPoint.save(); }
      catch (Exception e) {}
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
    
    public int compareTo(EndPoint endPoint) {
      return this.fullPath().compareTo(endPoint.fullPath());
    }
    
    public void verify() {
      label = label.replaceAll( "[^\\w.-]", "" );
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

