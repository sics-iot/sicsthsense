package models;

import java.util.*;

import javax.persistence.*;

//import org.codehaus.jackson.JsonNode;

import play.db.ebean.*;

import com.avaje.ebean.*;

import controllers.Utils;

@Entity 
public class DataPoint extends Model implements Comparable<DataPoint> {
  
    /**
	 * 
	 */
	private static final long serialVersionUID = 159529385564134763L;

		@Id
    public Long id;
  
    @ManyToOne 
    public Stream stream;
    
    //should be a template to a type
    ///we should support: Long, Double and String
    public double data;
    public long timestamp;
          
    public static Model.Finder<Long,DataPoint> find = new Model.Finder<Long, DataPoint>(Long.class, DataPoint.class);
    
    public DataPoint(Stream stream, double data, long timestamp) {
      this.stream = stream;
      this.data = data;
      this.timestamp = timestamp;
    }
    
    public static DataPoint add(Stream stream, double data, long timestamp) {
      DataPoint dataPoint = new DataPoint(stream, data, timestamp);
      dataPoint.save();
      return dataPoint;
    }
    
    public static long getCount() {
      return find.findRowCount();
    }
    
    public static List<DataPoint> getByStream(Stream stream) {
      return find.where()
          .eq("stream", stream)
          .orderBy("timestamp desc")
          .findList();
    }
    
    public static List<DataPoint> getByStreamTail(Stream stream, long tail) {    	
    	if(tail==0)
    		return new ArrayList<DataPoint>();
    	List<DataPoint> set = find.where()
          .eq("stream", stream)
          .setMaxRows((int)tail)
          .orderBy("timestamp desc")
          .findList();
//      return set.subList(set.size()-(int)tail, set.size());
      return set;
    }
    
    public static List<DataPoint> getByStreamSince(Resource stream, long since) {
      return find.where()
          .eq("resource", stream)
          .ge("timestamp", since)
          .orderBy("timestamp desc")
          .findList();
    }
    
    public static List<DataPoint> getByStreamLast(Stream stream, long last) {
      return getByStreamSince(stream, Utils.currentTime() - last);
    }
    
    public static List<DataPoint> getByStreamSince(Stream stream, long since) {
      return find.where()
          .eq("stream", stream)
          .ge("timestamp", since)
          .orderBy("timestamp desc")
          .findList();
    }
    
    public static void deleteByStream(Stream stream) {
    //TODO this is an ugly workaround, we need to find out how to SQL delete directly
      List<DataPoint> list = find.where()
          .eq("stream", stream)
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

