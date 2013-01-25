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
public class DataPoint extends Model implements Comparable<DataPoint> {
  
    @Id
    public Long id;
  
    //XXX: Cascade from this side might be wrong!
    @ManyToOne//(cascade = CascadeType.ALL) 
    public Resource resource;
    
    public float data;
    public long timestamp;
          
    public static Model.Finder<Long,DataPoint> find = new Model.Finder<Long, DataPoint>(Long.class, DataPoint.class);
    
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
    
    public static long getCount() {
      return find.findRowCount();
    }
    
    public static List<DataPoint> getByStream(Resource stream) {
      return find.where()
          .eq("resource", stream)
          .orderBy("timestamp desc")
          .findList();
    }
    
    public static List<DataPoint> getByStreamTail(Resource stream, long tail) {    	
    	if(tail==0)
    		return new ArrayList<DataPoint>();
    	List<DataPoint> set = find.where()
          .eq("resource", stream)
          .setMaxRows((int)tail)
          .orderBy("timestamp desc")
          .findList();
//      return set.subList(set.size()-(int)tail, set.size());
      return set;
    }
    
    public static List<DataPoint> getByStreamLast(Resource stream, long last) {
      return getByStreamSince(stream, Utils.currentTime() - last);
    }
    
    public static List<DataPoint> getByStreamSince(Resource stream, long since) {
      return find.where()
          .eq("resource", stream)
          .ge("timestamp", since)
          .orderBy("timestamp desc")
          .findList();
    }
    
    public static void deleteByStream(Resource stream) {
    //TODO this is an ugly workaround, we need to find out how to SQL delete directly
      List<DataPoint> list = find.where()
          .eq("resource", stream)
          .orderBy("timestamp desc")
          .findList();
      Ebean.delete(list);
//      List<Long> ids = new LinkedList<Long>();
//      for(DataPoint element: list) {
//        ids.add(element.id);
//      }
//      for(Long id: ids) {
//        find.ref(id).delete(); 
//      }
    }

    public int compareTo(DataPoint point) {
      return Long.valueOf(this.timestamp).compareTo(point.timestamp);
    }
    
}

