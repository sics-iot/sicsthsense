package com.sics.sicsthsense.core;

public class DataPoint {
    private long id;
    private long stream_id;
    private long timestamp;
    private double value;

    public DataPoint(double value) {
			this.timestamp	= System.currentTimeMillis();
			this.value			= value;
    }
    public DataPoint(long timestamp, double value) {
			this.timestamp	= timestamp;
			this.value			= value;
    }
    public DataPoint(long id, long stream_id, long timestamp, double value) {
			this.id					= id;
			this.stream_id	= stream_id;
			this.timestamp	= timestamp;
			this.value			= value;
    }

    public long getTimestamp()						{ return timestamp; }
    public void setId(long timestamp)			{ this.timestamp = timestamp; }

    public double getValue()							{ return value; }
    public void		setValue(double  value) { this.value = value; }

}
