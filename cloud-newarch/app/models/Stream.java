package models;

import java.util.*;

import javax.persistence.*;

import play.db.ebean.*;

public class Stream extends GenericSource {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8823372604684774587L;

	@ManyToOne
	public GenericSource source;
	
	@OneToMany(cascade = CascadeType.ALL, mappedBy="stream")
	public List<DataPoint> dataPoints = new ArrayList<DataPoint>();
	
	/** The maximum duration to be kept */
	public long historySize=1L;
	
	/** Last time a point was inserted */
	public long lastUpdated=0L;
	
	public static Model.Finder<Long, Stream> find = new Model.Finder<Long, Stream>(
		Long.class, Stream.class);
	
	public Stream(User user) {
		super(user);
		// TODO Auto-generated constructor stub
	}
		
	public Boolean hasData() {
		return lastUpdated != 0;
	}

	public void post(float data, long time) {
		DataPoint.add(this, data, time);
		lastUpdated = time;
		update();
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

}
