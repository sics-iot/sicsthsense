package models;

import java.util.*;

import javax.persistence.*;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Expr;
import com.avaje.ebean.SqlUpdate;
import com.avaje.ebean.annotation.EnumValue;

import controllers.Utils;

import play.Logger;
import play.mvc.PathBindable;
import play.db.ebean.*;

@Entity
@Table(name = "streams")
public class Stream extends Model implements Comparable<Stream> {
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
	public double latitude;
	public double longtitude;
	public String description;

	public boolean publicAccess = false;
	public boolean publicSearch = false;
	
	/** Freeze the Stream so any new incoming data is discarded */
	public boolean frozen = false;

	/**
	 * The maximum duration to be kept. This should be used with the database to
	 * limit the size of the datapoints list
	 */
	public Long historySize = 1L;

	/** Last time a point was inserted */
	public Long lastUpdated = 0L;

	@Version //for concurrency protection
	private int version;
	
	/** Secret key for authentication */
	@Column(name="secret_key") //key is a reserved keyword in mysql
	private String key;
	
	@ManyToOne
	public User owner;

	@ManyToOne
	public Resource resource;

	//should this be a field in the table? (i.e. not mappedBy)?
	@OneToOne(cascade = CascadeType.ALL, mappedBy="linkedStream")
	public Vfile file;

	@javax.persistence.Transient
	public List dataPoints;
	
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "stream")
	public List<DataPointString> dataPointsString;
	
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "stream")
	public List<DataPointDouble> dataPointsDouble;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "stream")
	public List<StreamParser> streamparsers = new ArrayList<StreamParser>();

  @ManyToMany(cascade = CascadeType.ALL, mappedBy = "followedStreams")
  public List<User> followingUsers = new ArrayList<User>();

	public Stream(User user, Resource resource, StreamType type) {
		this.owner =  user;
		this.resource = resource;
		this.type =   type;
		this.latitude   = 0.0;
		this.longtitude = 0.0;

		createKey();

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
	
	public Stream(User user, Resource resource) {
		this(user, resource, StreamType.UNDEFINED);
	}

	public Stream(User user) {
		this(user, null, StreamType.UNDEFINED);
	}

	public Stream() {
		this(null, null, StreamType.UNDEFINED);
	}

	public void updateStream(Stream modified) {
		this.description = modified.description;
		this.longtitude  = modified.longtitude;
		this.latitude    = modified.latitude;
		update();
	}

	public Long getHistorySize() {
		return historySize;
	}

	public void setHistorySize(long historySize) {
		if (historySize <= 0)
			historySize = 1;
		this.historySize = historySize;
	}
	/*
	public void setLocation(Location location) {
		this.location=location;
	}

	public void setLocation(double lon, double lat) {
		setLocation(new Location(lon,lat));
	}*/
	
	/** Call to create, or update an access key */
	protected String createKey() {
		key = UUID.randomUUID().toString();
		return key;
	}

	public String updateKey() {
		key = createKey();
		update();
		return key;
	}
	
	public String getKey() {
		return key;
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
	
	public boolean canWrite(User user) {
		return (owner.equals(user));
	}

	public boolean canRead(String key) {
		return (publicAccess || this.key == key);
	}

	public Boolean hasData() {
		return lastUpdated != 0L;
	}

	protected boolean post(double data, long time) {
		if(!this.frozen) {
			if (type == StreamType.UNDEFINED) {
				type = StreamType.DOUBLE;
				this.dataPoints = this.dataPointsDouble;
			}
			if (type == StreamType.DOUBLE) {
				DataPoint dp = new DataPointDouble(this, data, time).add();
				Logger.info("Adding new point: " + dp);
				lastUpdated = time;
				update();
				return true;
			}
		}
		return false;
	}

	public boolean post(String data, long time) {
		if(!this.frozen) {
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
		}
		return false;
	}

	public static Model.Finder<Long, Stream> find = new Model.Finder<Long, Stream>(
			Long.class, Stream.class);

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

	public static void delete(Long id) {
		find.ref(id).delete();
	}
	
	public static void deleteByResource(Resource resource) {
		List<Stream> list = find.where()
		 .eq("resource", resource)
		 .findList();
		for (Stream stream : list) {
			stream.delete();
		}
	}
	
	public static void dattachResource(Resource resource) {
		List<Stream> list = find.where()
		 .eq("resource", resource)
		 .findList();
		for (Stream stream : list) {
			stream.resource = null;
			stream.update();
		}
	}

	public static void clearStream(Long id) {
		Stream stream = (Stream) get(id);
		if (stream != null) {
			stream.clearStream();
		}
	}
	
	@play.db.ebean.Transactional
	public void clearStream() {		
		this.lastUpdated = 0L;
		this.deleteDataPoints();
		this.update();
	}

	public List<? extends DataPoint> getDataPoints() {
		return (List<? extends DataPoint>)dataPoints;
	}

	public List<? extends DataPoint> getDataPointsTail(long tail) {
		if (tail <= 0) {
			tail=1L;
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
		//Logger.info(this.id + " : Points since: " + since + set.toString());
		return set;
	}

	public static Stream getByKey(String key) {
		return find.where().eq("key", key).findUnique();
	}

	private void deleteDataPoints() {
			if (type == StreamType.STRING && dataPointsString.size() > 0) {
				Ebean.delete(this.dataPointsString);
			} else if (type == StreamType.DOUBLE && dataPointsDouble.size() > 0) {
				Ebean.delete(this.dataPointsDouble);
			}
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

	// @play.db.ebean.Transactional
	public void delete() {
		Ebean.beginTransaction();
		try {
			
			//(StreamParsers will be deleted with cascading deletes; instead.)
			
			// Detach parsers from streams. 
//			String s = "UPDATE parsers set stream_id = :target_stream_id where stream_id = :stream_id";
//			SqlUpdate update = Ebean.createSqlUpdate(s);
//			update.setParameter("stream_id", this.id);
//			update.setParameter("target_stream_id", null);
//			int modifiedCount = Ebean.execute(update);
//			String msg = "There were " + modifiedCount + "rows updated";
//			Logger.info("Deleting stream: Detaching some stream parsers: "
//					+ msg);
			
			//Detach file: No need; cascading delete instead.
			//this.file.linkedStream = null;
			//this.file.update();
			
			deleteDataPoints();
			//detach following users //Cascading delete instead (Will only delete the relation)
			//this.followingUsers.clear();
			//this.saveManyToManyAssociations("followingUsers");
			//delete stream
			super.delete();
			
			//commit transaction
			Ebean.commitTransaction();
		} 
//		catch(Exception e) {
//			Logger.warn("Deleting stream failed!! " + e.getMessage() + e.getStackTrace()[0].toString());
//		} 
		finally {
			Ebean.endTransaction();
		}

		/// probably a bit easier way
//		// setFrozen(true);
//		Logger.info("Deleting stream: Detaching some stream parsers: "
//				+ this.streamparsers.size());
//		//List<StreamParser> relatedParsers = StreamParser.find.where().eq("stream", this).findList();
//		for (StreamParser sp : this.streamparsers) {
//			sp.stream = null;
//			sp.update();
//		}
//		this.file.linkedStream = null;
//		this.file.update();
//		// clearStream(this.id);
//		deleteDataPoints();
//		this.followingUsers.clear();
//		this.saveManyToManyAssociations("followingUsers");
//		super.delete();
	}

	public String showKey(User user){
		if(this.owner.equals(user)){
			return this.key;
		}
		return null;
	}
	
	public boolean setPublicAccess( Boolean pub ) {
		this.publicAccess = pub;
		this.update();
		return pub;
	}
	
	public boolean setPublicSearch( Boolean pub ) {
		this.publicSearch = pub;
		this.update();
		return pub;
	}
	
	public boolean isPublicSearch() {
		return publicSearch;
	}
	
	public boolean setFrozen( Boolean frozen ) {
		this.frozen = frozen;
		this.update();
		return frozen;
	}

	public List<StreamParser> getStreamParsers() {
		return streamparsers;
//		if(resource != null) {
//			return StreamParser.find.where().eq("resource", resource).eq("stream", this).orderBy("streamVfilePath asce").findList();
//		}
//		return null;
	}
	
	public int compareTo(Stream other) {
		//Logger.info("paths: "+file.getPath()+" "+other.file.getPath());
		return file.getPath().compareTo(other.file.getPath());
	}

	public static List<Stream> availableStreams(User currentUser) {
		List<Stream> available = find.where()
				.or(
						Expr.eq("publicSearch", true), 
						Expr.eq("owner", currentUser) 
						)
				.orderBy("owner").findList();
		return available;
	}

	// get the recently updated public streams that are not followed by currentUser
	public static List<Stream> getLastUpdatedStreams(User currentUser, int count) {
		List<Stream> available = find.where()
				.and(
						//Expr.and(	Expr.eq("publicSearch", true), Expr.eq("publicAccess", true) ),
						Expr.eq("publicSearch", true),
						Expr.ne("owner", currentUser) 
						)
					 //do not include already followed streams	
					.not(Expr.in("id", Stream.find.where().join("followingUsers").where().eq("followingUsers.id", currentUser.id).findIds())) // User.find.where().join("streams").where().eq("id", currentUser.id)))
					.orderBy("lastUpdated")
					.setMaxRows(count)
					.findList();
		return available;
	}
	
}
