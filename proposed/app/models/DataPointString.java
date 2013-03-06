package models;

import java.util.List;

import javax.persistence.*;

import play.db.ebean.Model;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.validation.Length;

import controllers.Utils;

@Entity
@Table(name = "data_point_string", uniqueConstraints = { 
		@UniqueConstraint(columnNames = {"stream_id", "timestamp" }) 
		})
public class DataPointString extends DataPoint {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6502881310122879601L;
	
	@Transient
	public final static int maxLength = 160;
	@Length(min=1, max=maxLength)
	
	public String data;

	public DataPointString() {
		this(null, null, null);
		// TODO Auto-generated constructor stub
	}
	
	public DataPointString(Stream stream, String data, Long timestamp) {
		this.stream = stream;
		this.data = data;
		this.timestamp = timestamp;
		//find = new Model.Finder<Long, DataPointString>(Long.class, DataPointString.class);
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
