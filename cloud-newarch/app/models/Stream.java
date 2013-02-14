package models;

import java.util.*;

import javax.persistence.*;

import com.avaje.ebean.annotation.EnumValue;

import play.mvc.PathBindable;
import play.db.ebean.*;

public class Stream extends GenericSource implements PathBindable<Stream> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8823372604684774587L;
	
	/* Type of data points this stream stores */
	public enum StreamType {
		@EnumValue("D")
		DOUBLE,
		@EnumValue("L")
		LONG,
		@EnumValue("S")
		STRING
	}
	
	public StreamType type = StreamType.DOUBLE;

	@ManyToOne
	public GenericSource source;
	
	@OneToMany(cascade = CascadeType.ALL, mappedBy="stream")
	public List<DataPoint> dataPoints = new ArrayList<DataPoint>();
	
	/** The maximum duration to be kept.
	 * This should be used with the database to limit the size of the datapoints list */
	public long historySize=1L;
	
	public long getHistorySize() {
		return historySize;
	}

	public void setHistorySize(long historySize) {
		if(historySize <= 0)
			historySize=1;
		this.historySize = historySize;
	}

	/** Last time a point was inserted */
	public long lastUpdated=0L;
		
	public static Model.Finder<Long, Stream> find = new Model.Finder<Long, Stream>(
		Long.class, Stream.class);
	
	/** Secret token for authentication */
	private String token;
	
	/** Call to create, or update an access token */
	public String createToken() {
		token = UUID.randomUUID().toString();
		save();
		return token;
	}
	
	public String getToken() {
		return token;
	}
	
	public static Stream findByToken(String token) {
		if (token == null) {
			return null;
		}

		try {
			return find.where().eq("token", token).findUnique();
		} catch (Exception e) {
			return null;
		}
	}
	
	public Stream(User user) {
		super(user);
		// TODO Auto-generated constructor stub
	}
	
	public Stream() {
		super();
		// TODO Auto-generated constructor stub
	}
		
	public Boolean hasData() {
		return lastUpdated != 0;
	}

	public void post(double data, long time) {
		if(type == StreamType.DOUBLE) {
			DataPoint dp = new DataPointDouble(this, data, time).add();
			lastUpdated = time;
			update();
		}
	}

	public void post(long data, long time) {
		if(type == StreamType.LONG) {
			DataPoint dp = new DataPointLong(this, data, time).add();
			lastUpdated = time;
			update();
		}
	}
	
	public void post(String data, long time) {
		if(type == StreamType.STRING) {
			DataPoint dp = new DataPointString(this, data, time).add();
			lastUpdated = time;
			update();
		}
	}
	
	public static void delete(Long id) {
		// TODO should enable cascading instead
		// TODO To enable cascading the model should be reconstructed to
		// have a OneToMany relationship from a stream to dataPoints;
		// thus, dataPoints should be stored as a list in a stream instead
		// and this should be the same to all other ManyToOne relationships
		//clearStream(id);
		find.ref(id).delete();
	}

	public static void clearStream(Long id) {
		Stream stream = (Stream) get(id);
		stream.pollingProperties.lastPolled = 0;
		stream.lastUpdated = 0;
		stream.update();
		if (stream != null) {
			DataPoint.deleteByStream(stream);
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
		//verify();
		//this.lastUpdated = System.currentTimeMillis();
		super.update();
	}

	public void delete() {
		///XXX: Can't delete if a device is sending updates to this stream!
		//now I'm using ebean.trasaction on the action function...
		clearStream(this.id);
		super.delete();
	}

	@Override
	public Stream bind(String key, String id) {
	// TODO check key?
		Stream stream = (Stream) get(Long.parseLong(id));
		if (stream != null) {
			return stream;
		} else {
			throw new IllegalArgumentException("Stream with id " + id + " not found");
		}
	}

	@Override
	public String unbind(String key) {
		// TODO check key?
		return id.toString();
	}

	@Override
	public String javascriptUnbind() {
	// TODO check key?
		return "function(k,v) {\n" +
						"    return v.id;" +
		        "}";
	}

}


