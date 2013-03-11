package models;

import java.util.*;

import javax.persistence.*;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.annotation.EnumValue;

import controllers.Utils;

import play.mvc.PathBindable;
import play.db.ebean.*;

@Entity
@Table(name = "streams")
public class Stream extends Model {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8823372604684774587L;

	/* Type of data points this stream stores */
	public static enum StreamType {
		@EnumValue("D")
		DOUBLE, 
		//Long is not needed
		//TODO: Should provide location instead...s
		//@EnumValue("L")
		//LONG, 
		@EnumValue("S")
		STRING, 
		@EnumValue("U")
		UNDEFINED
	}

	@Id
	public Long id;

	public StreamType type = StreamType.UNDEFINED;

	@ManyToOne
	public User owner;

	@ManyToOne
	public Source source;

	@OneToOne(cascade = CascadeType.ALL, mappedBy="linkedStream")
	public Vfile file;

	public boolean publicAccess = false;

	/**
	 * The maximum duration to be kept. This should be used with the database to
	 * limit the size of the datapoints list
	 */
	public Long historySize = 1L;

	/** Last time a point was inserted */
	public Long lastUpdated = 0L;

	/** Secret key for authentication */
	private String key;
	
	@javax.persistence.Transient
	public List dataPoints;
	
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "stream")
	public List<DataPointString> dataPointsString;
	
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "stream")
	public List<DataPointDouble> dataPointsDouble;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "stream")
	public List<StreamParser> streamparsers = new ArrayList<StreamParser>();



	public Long getHistorySize() {
		return historySize;
	}

	public void setHistorySize(long historySize) {
		if (historySize <= 0)
			historySize = 1;
		this.historySize = historySize;
	}

	public static Model.Finder<Long, Stream> find = new Model.Finder<Long, Stream>(
			Long.class, Stream.class);
	/** Call to create, or update an access key */
	protected String createKey() {
		key = UUID.randomUUID().toString();
		save();
		return key;
	}

	protected String getKey() {
		return key;
	}

	public static Stream findByKey(String key) {
		if (key == null) {
			return null;
		}

		try {
			return find.where().eq("key", key).findUnique();
		} catch (Exception e) {
			return null;
		}
	}

	public Stream(User user, Source source, StreamType type) {
		this.owner = user;
		this.source = source;
		this.type = type;
		switch( this.type ) {
		case DOUBLE:
			this.dataPoints = this.dataPointsDouble;
			break;
		case STRING:
			this.dataPoints = this.dataPointsString;
			break;
		default:
			break;
		}
	}
	
	public Stream(User user, Source source) {
		this(user, source, StreamType.UNDEFINED);
	}

	public Stream(User user) {
		this(user, null, StreamType.UNDEFINED);
		// TODO Auto-generated constructor stub
	}

	public Stream() {
		super();
		// TODO Auto-generated constructor stub
	}

	/** Create a persisted stream */
	public static Stream create(User user) {
		if (user != null) {
			Stream stream = new Stream(user);
			stream.save();
			return stream;
		}
		return null;
	}

	/** Persist a stream */
	public static Stream create(Stream stream) {
		if (stream.owner != null) {
			stream.save();
			return stream;
		}
		return null;
	}

	public static Stream get(Long id) {
		return find.byId(id);
	}

	public boolean canRead(User user) {
		return (publicAccess || owner.equals(user)); // || isShare(user);
	}

	public boolean canRead(String key) {
		return (publicAccess || this.key == key);
	}

	public Boolean hasData() {
		return lastUpdated != 0L;
	}

	protected boolean post(double data, long time) {
		if (type == StreamType.UNDEFINED) {
			type = StreamType.DOUBLE;
			this.dataPoints = this.dataPointsDouble;
		}
		if (type == StreamType.DOUBLE) {
			DataPoint dp = new DataPointDouble(this, data, time).add();
			lastUpdated = time;
			update();
			return true;
		}
		return false;
	}

//	protected boolean post(long data, long time) {
//		if (type == StreamType.UNDEFINED) {
//			type = StreamType.LONG;
//		}
//		if (type == StreamType.LONG) {
//			DataPoint dp = new DataPointLong(this, data, time).add();
//			lastUpdated = time;
//			update();
//			return true;
//		}
//		return false;
//	}

	public boolean post(String data, long time) {
		if (type == StreamType.UNDEFINED) {
			type = StreamType.STRING;
			this.dataPoints = this.dataPointsString;
		}
		if (type == StreamType.STRING) {
			DataPoint dp = new DataPointString(this, data, time).add();
			lastUpdated = time;
			update();
			return true;
		}
		return false;
	}

	public static void delete(Long id) {
		// TODO should enable cascading instead
		// TODO To enable cascading the model should be reconstructed to
		// have a OneToMany relationship from a stream to dataPoints;
		// thus, dataPoints should be stored as a list in a stream instead
		// and this should be the same to all other ManyToOne relationships
		// clearStream(id);
		find.ref(id).delete();
	}
	public static void deleteBySource(Source source) {
		List<Stream> list = find.where()
		 .eq("source", source)
		 .findList();
		for (Stream stream : list) {
			stream.delete();
		}
	}

	public static void clearStream(Long id) {
		Stream stream = (Stream) get(id);
		if (stream != null) {
			stream.lastUpdated = 0L;
			stream.update();
			stream.deleteDataPoints();
		}
	}

	public List<? extends DataPoint> getDataPoints() {
		return (List<? extends DataPoint>)dataPoints;
	}

	public List<? extends DataPoint> getDataPointsTail(long tail) {
		if (tail == 0) {
			tail++;
			//return new ArrayList<? extends DataPoint>(); // TODO should this be return new
																					// ArrayList<? extends DataPoint>(0) ??
		}
		List<? extends DataPoint> set = null;
		if (type == StreamType.STRING) {
			set = DataPointString.find.where().eq("stream", this)
					.setMaxRows((int) tail).orderBy("timestamp desc").findList();
		} else if (type == StreamType.DOUBLE) {
			set = DataPointDouble.find.where().eq("stream", this)
					.setMaxRows((int) tail).orderBy("timestamp desc").findList();
		}
		return set;
	}

	public List<? extends DataPoint> getDataPointsLast(long last) {
		return this.getDataPointsSince(Utils.currentTime() - last);
	}

	public List<? extends DataPoint> getDataPointsSince(long since) {
		List<? extends DataPoint> set = null;
		if (type == StreamType.STRING) {
			set = DataPointString.find.where().eq("stream", this).ge("timestamp", since)
					.orderBy("timestamp desc").findList();
		} else if (type == StreamType.DOUBLE) {
			set = DataPointDouble.find.where().eq("stream", this).ge("timestamp", since)
					.orderBy("timestamp desc").findList();
		}
		return set;
	}

	public static Stream getByKey(String key) {
		return find.where().eq("key", key).findUnique();
	}

	private void deleteDataPoints() {
		if (dataPoints != null) {
			Ebean.delete(dataPoints);
		}
		// List<Long> ids = new LinkedList<Long>();
		// for(DataPoint element: list) {
		// ids.add(element.id);
		// }
		// for(Long id: ids) {
		// find.ref(id).delete();
		// }
	}

	public StreamType getType() {
		return type;
	}

	public void setType(StreamType type) {
		this.type = type;
	}

	public void save() {
		super.save();
	}

	public void update() {
		// verify();
		// this.lastUpdated = System.currentTimeMillis();
		super.update();
	}

	public void delete() {
		// /XXX: Can't delete if a device is sending updates to this stream!
		// now I'm using ebean.trasaction on the action function...
		clearStream(this.id);
		super.delete();
	}

}
