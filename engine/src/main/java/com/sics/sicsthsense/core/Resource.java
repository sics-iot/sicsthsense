package com.sics.sicsthsense.core;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Resource {
	@JsonProperty
  private long id;
	@JsonProperty
  private String label;
  private int version;

	public Resource() {
		this.id			= -1;
		this.label	= "";
	}
	public Resource(long id, String label) {
		this.id			= id;
		this.label	= label;
	}

	public long getId()							{ return id; }
	public String getLabel()				{ return label; }
	public String getOwner_id()			{ return "null"; } 
	public String getPolling_period(){ return "null"; }
	public String getLast_polled()	{ return "null"; }
	public String getPolling_url()	{ return "null"; }
	public String getPolling_authentication_key()		{ return "null"; }
	public String getDescription()	{ return "null"; }
	public String getParent_id()		{ return "null"; }
	public String getSecret_key()		{ return "null"; }
	public String getVersion()			{ return "null"; }
	public String getLast_posted()	{ return "null"; }

	public void setId(long id)	{ this.id = id; }
	public void setOwner_id(String p) {} 
	public void setLabel(String label)	{ this.label = label; }
	public void setPolling_period(String p) {}
	public void setLast_polled(String p) {}
	public void setPolling_url(String p) {}
	public void setPolling_authentication_key(String p) {}
	public void setDescription(String p) {}
	public void setParent_id(String p) {}
	public void setSecret_key(String p) {}
	public void setVersion(String p) {}
	public void setLast_posted(String p) {}

}
