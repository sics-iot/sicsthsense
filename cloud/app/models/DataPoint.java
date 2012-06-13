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
public class DataPoint extends Model {
  
    @Id
    public Long id;
    
    @ManyToOne
    public Resource resource;
    
    public float data;
    public long timestamp;
          
    public static Model.Finder<Long,DataPoint> find = new Model.Finder(Long.class, DataPoint.class);
    
    public DataPoint(Resource resource, float data, long timestamp) {
      this.resource = resource;
      this.data = data;
      this.timestamp = timestamp;
    }
    
    public static DataPoint add(Resource resource, float data, long timestamp) {
      DataPoint dataPoint = new DataPoint(resource, data, timestamp);
      dataPoint.save();
      return dataPoint;
    }
    
    public static List<DataPoint> getByStream(Resource stream) {
      return find.where()
          .eq("resource", stream)
          .findList();
    }
    
    public static void deleteByStream(Resource stream) {
      for(DataPoint dataPoint: getByStream(stream)) {
        dataPoint.delete();
      }
    }
        
}

