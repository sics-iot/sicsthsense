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
	
	// this is probably bad
	//public Model.Finder<Long, ? extends DataPoint> find;

	public abstract DataPoint add();

	public DataPoint() {
		super();
		// TODO Auto-generated constructor stub
	}

	public int compareTo(DataPoint point) {
		return Long.valueOf(this.timestamp).compareTo(point.timestamp);
	}
	
		public abstract Object getData();

//	public abstract long getCount();
//
//
//	public abstract List<? extends DataPoint> getByStream(Stream stream);
//
//	public abstract List<? extends DataPoint> getByStreamTail(Stream stream, long tail);
//
//	public abstract List<? extends DataPoint> getByStreamLast(Stream stream, long last);
//
//	public abstract List<? extends DataPoint> getByStreamSince(Stream stream, long since);
//
//	public abstract void deleteByStream(Stream stream);

}
