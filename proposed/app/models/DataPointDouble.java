package models;

import java.util.List;

import javax.persistence.*;

import play.db.ebean.Model;

import com.avaje.ebean.Ebean;

import controllers.Utils;

@Entity
@Table(name = "data_point_double", uniqueConstraints = { 
		@UniqueConstraint(columnNames = {"stream_id", "timestamp" }) 
		})
public class DataPointDouble extends DataPoint {
	
	public DataPointDouble() {
		this(null, null, null);
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -6502881310122879601L;
	public Double data;

	public DataPointDouble(Stream stream, Double data, Long timestamp) {
		super();
		this.stream = stream;
		this.data = data;
		this.timestamp = timestamp;
		//find = new Model.Finder<Long, DataPointDouble>(Long.class, DataPointDouble.class);
	}

	public DataPointDouble add() {
		// DataPointDouble dataPoint = new DataPoint(stream, data, timestamp);
		if (stream != null) {
			this.save();
			return this;
		}
		return null;
	}

  public Double getData() {
  	return data;
  }
  
}
