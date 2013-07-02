package com.sics.sicsthsense.core;

public class DataPoint {
    private long timestamp;
    private long value;

    public DataPoint(long timestamp, long value) {
			this.timestamp	= timestamp;
			this.value			= value;
    }

    public long getTimestamp()				{ return timestamp; }
    public void setId(long timestamp)	{ this.timestamp = timestamp; }

    public long getValue()						{ return value; }
    public void setValue(long value){ this.value = value; }

}
