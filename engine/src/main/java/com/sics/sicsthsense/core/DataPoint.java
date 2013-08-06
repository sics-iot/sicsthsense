package com.sics.sicsthsense.core;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DataPoint {
	@JsonProperty
	private long id;
	@JsonProperty
	private long stream_id;
	@JsonProperty
	private long timestamp;
	@JsonProperty
	private double value;

	public DataPoint() {
		id=-1;
		stream_id=-1;
		timestamp=-1;
		value=-1;
	}
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

	public long getId()										{ return id; }
	public long getStreamId()							{ return stream_id; }
	public long getTimestamp()						{ return timestamp; }
	public double getValue()							{ return value; }

	public void setId(long id)								{ this.id = id; }
	public void setStreamId(long stream_id)		{ this.stream_id = stream_id; }
	public void setTimestamp(long timestamp)	{ this.timestamp = timestamp; }
	public void	setValue(double value)				{ this.value = value; }

}
