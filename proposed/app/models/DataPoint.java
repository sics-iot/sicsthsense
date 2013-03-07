package models;

import java.util.*;

import javax.persistence.*;

//import org.codehaus.jackson.JsonNode;

import play.db.ebean.*;

import com.avaje.ebean.*;

import controllers.Utils;

/** T could be any comparable type; i.e. Long, Double, String, etc. */
@javax.persistence.MappedSuperclass
public abstract class DataPoint extends Model implements Comparable<DataPoint> {

	@Column(name = "stream_id", nullable = false)
	@ManyToOne
	public Stream stream;

	@Id
	public Long id;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2919758328697338009L;

	/** T could be any comparable type; i.e. Long, Double, String, etc. */
	//@Column(unique = true, nullable = false)
	//@Id
	public Long timestamp;
	
	public static Model.Finder<Long, ? extends DataPoint> find =  new Model.Finder<Long, DataPoint>(Long.class, DataPoint.class);

	public abstract DataPoint add();

	public DataPoint() {
		super();
		// TODO Auto-generated constructor stub
	}

	public static long getCount() {
		return find.findRowCount();
	}

	public abstract Object getData();

//	public abstract List<? extends DataPoint> getByStream(Stream stream);
//
//	public abstract List<? extends DataPoint> getByStreamTail(Stream stream, long tail);
//
//	public static List<? extends DataPoint> getByStreamLast(Stream stream, long last);
//
//	public static List<? extends DataPoint> getByStreamSince(Stream stream, long since);
//
//	public static void deleteByStream(Stream stream);
	
	public static List<? extends DataPoint> getByStream(Stream stream) {
		return find.where().eq("stream", stream).orderBy("timestamp desc")
				.findList();
	}

	public static List<? extends DataPoint> getByStreamTail(Stream stream, long tail) {
		if (tail == 0) {
			tail++;
		}
		List<? extends DataPoint> set = find.where().eq("stream", stream)
				.setMaxRows((int) tail).orderBy("timestamp desc").findList();
		// return set.subList(set.size()-(int)tail, set.size());
		return set;
	}

	public static List<? extends DataPoint> getByStreamLast(Stream stream, long last) {
		return getByStreamSince(stream, Utils.currentTime() - last);
	}

	public static List<? extends DataPoint> getByStreamSince(Stream stream, long since) {
		return find.where().eq("stream", stream).ge("timestamp", since)
				.orderBy("timestamp desc").findList();
	}

	public static void deleteByStream(Stream stream) {
		// TODO this is an ugly workaround, we need to find out how to SQL delete
		// directly
		List<? extends DataPoint> list = find.where().eq("stream", stream)
				.orderBy("timestamp desc").findList();
		Ebean.delete(list);
		// List<Long> ids = new LinkedList<Long>();
		// for(DataPoint element: list) {
		// ids.add(element.id);
		// }
		// for(Long id: ids) {
		// find.ref(id).delete();
		// }
	}	
	
	public int compareTo(DataPoint point) {
		return Long.valueOf(this.timestamp).compareTo(point.timestamp);
	}

}
