package models;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.Transient;

import com.avaje.ebean.validation.Length;

@Entity
public class DataPointString extends DataPoint {

	public DataPointString() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -6502881310122879601L;
	
	@Transient
	public final static int maxLength = 160;
	@Length(min=1, max=maxLength)
	
	public String data;

	public DataPointString(Stream stream, String data, long timestamp) {
		this.stream = stream;
		this.data = data;
		this.timestamp = timestamp;
	}

	public DataPointString add() {
		// DataPointDouble dataPoint = new DataPoint(stream, data, timestamp);
		if (stream != null && data != null) {
			if(data.length() > maxLength) {
				data.substring(0, maxLength-1);
			}
			this.save();
			return this;
		}
		return null;
	}
	
	public String getData() {
  	return data;
  }
}
