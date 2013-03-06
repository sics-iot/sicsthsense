package models;

import javax.persistence.*;

//@Entity
public class DataPointLong extends DataPoint {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6502881310122879601L;
	
	public Long data;

	@Column(nullable = false)
	@ManyToOne
	public Stream stream;
	public DataPointLong() {
		this(null, null, null);
		// TODO Auto-generated constructor stub
	}
	
	public DataPointLong(Stream stream, Long data, Long timestamp) {
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
	
	public Long getData() {
		return data;
  }
}
