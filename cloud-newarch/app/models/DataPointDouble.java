package models;

public class DataPointDouble extends DataPoint {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6502881310122879601L;
	public double data;

	public DataPointDouble(Stream stream, double data, long timestamp) {
		this.stream = stream;
		this.data = data;
		this.timestamp = timestamp;
	}

	public DataPointDouble add() {
		// DataPointDouble dataPoint = new DataPoint(stream, data, timestamp);
		if (stream != null) {
			this.save();
			return this;
		}
		return null;
	}

}
