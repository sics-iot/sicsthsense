package models;

public class DataPointLong extends DataPoint {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6502881310122879601L;
	public long data;

	public DataPointLong(Stream stream, long data, long timestamp) {
		this.stream = stream;
		this.data = data;
		this.timestamp = timestamp;
	}

	public DataPointLong add() {
		// DataPointDouble dataPoint = new DataPoint(stream, data, timestamp);
		if (stream != null) {
			this.save();
			return this;
		}
		return null;
	}

}
