package models;

import java.util.*;

import javax.persistence.*;

import org.codehaus.jackson.JsonNode;

import play.db.ebean.*;
import play.data.format.*;
import play.data.validation.*;

import ch.qos.logback.classic.db.SQLBuilder;

import com.avaje.ebean.*;

import controllers.Utils;
import play.libs.F.*;
import play.libs.WS;

@Entity 
public class DataPoint extends Model implements Comparable {
  
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
          .orderBy("timestamp asc")
          .findList();
    }
    
    public static List<DataPoint> getByStreamTail(Resource stream, long tail) {
      List<DataPoint> set = find.where()
          .eq("resource", stream)
          .orderBy("timestamp asc")
          .findList();
      return set.subList(set.size()-(int)tail, set.size());
    }
    
    public static List<DataPoint> getByStreamLast(Resource stream, long last) {
      return getByStreamSince(stream, Utils.currentTime() - last);
    }
    
    public static List<DataPoint> getByStreamSince(Resource stream, long since) {
      return find.where()
          .eq("resource", stream)
          .ge("timestamp", since)
          .orderBy("timestamp asc")
          .findList();
    }
    
    public static void deleteByStream(Resource stream) {
    //TODO this is an ugly workaround, we need to find out how to SQL delete directly
      List<DataPoint> list = find.where()
          .eq("resource", stream)
          .orderBy("timestamp asc")
          .findList();
//    Ebean.delete(list);
      List<Long> ids = new LinkedList<Long>();
      for(DataPoint element: list) {
        ids.add(element.id);
      }
      for(Long id: ids) {
        find.ref(id).delete(); 
      }
    }

    public int compareTo(Object arg0) {
      DataPoint d = (DataPoint)arg0;
      return Long.compare(this.timestamp, d.timestamp);
    }
    
}

