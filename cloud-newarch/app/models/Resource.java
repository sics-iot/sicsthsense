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
public class Resource extends GenericSource implements Comparable<Resource> {
  


		@Id
    public Long id;
    
    @Constraints.Required
    public String label;
    public String url;
    public String uid;
    public String description;
    public String location;
        
    @ManyToOne//(cascade = CascadeType.ALL) 
    public User user;
    
  	@OneToMany(mappedBy="resource")
  	public List<Stream> streams; 
            
    public static Model.Finder<Long,Resource> find = new Model.Finder<Long, Resource>(Long.class, Resource.class);
    
    public Resource(User user, String url, String uid, String label, String description, String location) {
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
    
    public static List<Resource> getByUser(User user) {
      return find.where()
          .eq("user", user)
          .orderBy("label")
          .findList();
    }
    
    public static Resource getByLabel(User user, String label) {
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
        
    public static Resource get(Long id) {
      return find.byId(id);
    }
    
    public boolean hasUrl() {
      return this.url != null && this.url.length() > 0;
    }
    
    public static void delete(Long id) {
      //TODO should enable cascading instead
      Resource resource = find.byId(id);
      Stream.deleteByEndPoint(resource);
      find.byId(id).delete();
    }
    
    public static List<Resource> all() {
      return find.where()
          .orderBy("user.userName")
          .findList();
    }
    
    public static Resource register(User user, String label, String url) {
      Resource resource = new Resource(user, url, null, label, null, null);
      try { resource.save(); }
      catch (Exception e) {}
      return resource;
    }

    public Stream getOrCreateResource(String path) {
      Stream stream = Stream.getByPath(this, path);
      if(stream == null) {
        stream = Stream.add(path, this);
      }
      return stream;
    }

    public static Resource getOrCreateByLabel(User user, String label) {
      Resource resource = getByLabel(user, label);
      if(resource == null) {
        resource = Resource.register(user, label, null);
      }
      return resource;
    }
    
    public int compareTo(Resource resource) {
      return this.fullPath().compareTo(resource.fullPath());
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

