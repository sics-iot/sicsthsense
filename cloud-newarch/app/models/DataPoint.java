package models;

import java.util.*;

import javax.persistence.*;

//import org.codehaus.jackson.JsonNode;

import play.db.ebean.*;

import com.avaje.ebean.*;

import controllers.Utils;

/** T could be any comparable type; i.e. Long, Double, String, etc. */
@Entity
@Inheritance
@DiscriminatorColumn(length=16)
public abstract class DataPoint extends Model implements Comparable<DataPoint> {

		/**
	 * 
	 */
	private static final long serialVersionUID = 2919758328697338009L;

		@Id
    public Long id;
  
		@Column(nullable = false)
    @ManyToOne 
    public Stream stream;
    
    /** T could be any comparable type; i.e. Long, Double, String, etc. */   
    public Long timestamp;
    //public static final String type= T.getClass().ToString();
          
    public static Model.Finder<Long,DataPoint> find = new Model.Finder<Long, DataPoint>(Long.class, DataPoint.class);
    
    public abstract DataPoint add();

		public DataPoint() {
		super();
		// TODO Auto-generated constructor stub
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

