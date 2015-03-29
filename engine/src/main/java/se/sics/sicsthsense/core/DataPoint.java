/*
 * Copyright (c) 2013, Swedish Institute of Computer Science
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of The Swedish Institute of Computer Science nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE SWEDISH INSTITUTE OF COMPUTER SCIENCE BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. */

/* Description:
 * TODO:
 * */
package se.sics.sicsthsense.core;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({ "id", "streamId", "timestamp", "value" })
public class DataPoint {
	@JsonProperty
	private long id;
	@JsonProperty("streamId")
	private long streamId;
	@JsonProperty
	private long timestamp;
	@JsonProperty
	private double value;

	private final Logger logger = LoggerFactory.getLogger(DataPoint.class);

	public DataPoint() {
		id=-1;
		streamId=-1;
		timestamp=-1;
		value=-1;
	}
	public DataPoint(double value) {
		super();
		this.timestamp	= System.currentTimeMillis();
		this.value			= value;
	}
	public DataPoint(long timestamp, double value) {
		super();
		this.timestamp	= timestamp;
		this.value			= value;
	}
	public DataPoint(long streamId, long timestamp, double value) {
		super();
		this.streamId	= streamId;
		this.timestamp	= timestamp;
		this.value			= value;
	}
	public DataPoint(long id, long streamId, long timestamp, double value) {
		this.id					= id;
		this.streamId	  = streamId;
		this.timestamp	= timestamp;
		this.value			= value;
	}
	public DataPoint(String json) {
		super();
		ObjectMapper mapper = new ObjectMapper();
		JsonNode root = null;
		try {
			root = mapper.readTree(json);
		} catch (JsonProcessingException e) {
			logger.error("JSON processing failed: "+json+"\t"+ e.getMessage());
		} catch (IOException e) {
			logger.error("JSON IO Error: "+json);
		}
		try {
			// we need a 'value' element
			this.value = root.findValue("value").asDouble();
			// optional 'timestamp'
			try { this.timestamp = root.findValue("timestamp").asLong(); } catch (Exception e) {}
		} catch (Exception e) { logger.error("Lacking 'value' JSON field! "+json); }
	}

	public String toString() {
		return getValue()+" @ "+getTimestamp() +" in Stream: "+getStreamId();
	}
	public String toJson() {
		return "{'value':"+getValue()+", 'timestamp':"+getTimestamp()+"}";
	}

	public long getId()										{ return id; }
	public long getStreamId()							{ return streamId; }
	public long getTimestamp()						{ return timestamp; }
	public double getValue()							{ return value; }

	public void setId(long id)								{ this.id = id; }
	public void setStreamId(long streamId)		{ this.streamId = streamId; }
	public void setTimestamp(long timestamp)	{ this.timestamp = timestamp; }
	public void	setValue(double value)				{ this.value = value; }

}
