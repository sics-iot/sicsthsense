package com.sics.sicsthsense.core;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Resource {
	@JsonProperty
  private long id;
	@JsonProperty
  private String label;
	@JsonProperty
  private int version;
	@JsonProperty
	private String owner_id;
	@JsonProperty
	private String polling_period;
	@JsonProperty
	private String last_polled;
	@JsonProperty
	private String polling_url;
	@JsonProperty
	private String polling_authentication_key;
	@JsonProperty
	private String description;
	@JsonProperty
	private String parent_id;
	@JsonProperty
	private String secret_key;
	@JsonProperty
	private String last_posted;

	public Resource() {
		this.id			= -1;
		this.label	= "nolabel";
		this.version= 1;
		this.owner_id=null;
	}
	public Resource(long id, String label) {
		this();
		this.id			= id;
		this.label	= label;
	}
	public Resource(long id, 
									String label,
									int version,
									String owner_id,
									String polling_period,
									String last_polled,
									String polling_url,
									String polling_authentication_key,
									String description,
									String parent_id,
									String secret_key,
									String last_posted
								) {
		this(); // defaulty values
		this.id			= id;
		this.label	= label;
		this.version= version;
		this.owner_id = owner_id;
		this.polling_period	= polling_period;
		this.last_polled	= last_polled;
		this.polling_url	= polling_url;
		this.polling_authentication_key = polling_authentication_key;
		this.description	= description;
		this.parent_id		= parent_id;
		this.secret_key		= secret_key;
		this.last_posted	= last_posted;
	}

	public long getId()							{ return id; }
	public String getLabel()				{ return label; }
	public String getOwner_id()			{ return owner_id; } 
	public String getPolling_period(){return polling_period; }
	public String getLast_polled()	{ return last_polled; }
	public String getPolling_url()	{ return polling_url; }
	public String getPolling_authentication_key()	{ return polling_authentication_key; }
	public String getDescription()	{ return description; }
	public String getParent_id()		{ return parent_id; }
	public String getSecret_key()		{ return secret_key; }
	//public int getVersion()					{ return version; }
public String getVersion()			{ return "1"; }
	public String getLast_posted()	{ return last_posted; }

	public void setId(long id)											{ this.id = id; }
	public void setOwner_id(String p)								{ this.owner_id = owner_id; } 
	public void setLabel(String label)							{ this.label = label; }
	public void setLast_polled(String last_polled)	{ this.last_polled = last_polled; }
	public void setPolling_url(String polling_url)	{ this.polling_url = polling_url; }
	public void setDescription(String description)	{ this.description = description;}
	public void setParent_id(String parent_id)			{ this.parent_id = parent_id; }
	public void setSecret_key(String secret_key)		{ this.secret_key = secret_key; }
	public void setVersion(int version)							{ this.version = version; }
	public void setLast_posted(String last_posted)	{ this.last_posted = last_posted; }
	public void setPolling_period(String polling_period) { this.polling_period = polling_period; }
	public void setPolling_authentication_key(String polling_authentication_key) { this.polling_authentication_key = polling_authentication_key; }
}
