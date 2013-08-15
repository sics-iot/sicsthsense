package com.sics.sicsthsense.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Resource {
	@JsonProperty
  private long id;
	@JsonProperty
  private String label;
	@JsonProperty
  private String version;
	@JsonProperty
	private long owner_id;
	@JsonProperty
	private long parent_id;
	@JsonProperty
	private String polling_url;
	@JsonProperty
	private String polling_authentication_key;
	@JsonProperty
	private long polling_period;
	@JsonProperty
	private String secret_key;
	@JsonProperty
	private String description;
	@JsonProperty
	private long last_polled;
	@JsonProperty
	private long last_posted;

	private final Logger logger = LoggerFactory.getLogger(Resource.class);

	public Resource() {
		this.id			= -1;
		this.label	= "nolabel";
		this.version= "1.0";
		this.owner_id=-1;
	}
	public Resource(long id, String label) {
		this();
		this.id			= id;
		this.label	= label;
	}
	public Resource(long id, 
									String label,
									String version,
									long owner_id,
									long parent_id,
									String polling_url,
									String polling_authentication_key,
									long polling_period,
									String secret_key,
									String description,
									long last_polled,
									long last_posted
								) {
		this(); // default values
		this.id			= id;
		this.label	= label;
		this.version= version;
		this.owner_id = owner_id;
		this.parent_id		= parent_id;
		this.polling_url	= polling_url;
		this.polling_authentication_key = polling_authentication_key;
		this.polling_period	= polling_period;
		this.secret_key		= secret_key;
		this.description	= description;
		this.last_polled	= last_polled;
		this.last_posted	= last_posted;
	}

	public boolean isReadable(User user) {
		if (user.getId() == owner_id) {return true;} // owners can read
		else {logger.warn(user.getId()+"!="+owner_id);}
		return false;
	}
	public boolean isWritableable(User user) {
		if (user.getId() == owner_id) {return true;} // owners can read
		return false;
	}


	public long   getId()						{ return id; }
public String getVersion()			{ return "1.0"; }
//public int getVersion()					{ return version; }
	public String getLabel()				{ return label; }
	public long   getOwner_id()			{ return owner_id; } 
	public long   getParent_id()		{ return parent_id; }
	public String getPolling_url()	{ return polling_url; }
	public String getPolling_authentication_key()	{ return polling_authentication_key; }
	public long		getPolling_period(){return polling_period; }
	public String getSecret_key()		{ return secret_key; }
	public String getDescription()	{ return description; }
	public long		getLast_polled()	{ return last_polled; }
	public long		getLast_posted()	{ return last_posted; }

	public void setId(long id)											{ this.id = id; }
	public void setLabel(String label)							{ this.label = label; }
	public void setVersion(String version)					{ this.version = version; }
	public void setOwner_id(long owner_id)					{ this.owner_id = owner_id; } 
	public void setParent_id(long parent_id)				{ this.parent_id = parent_id; }
	public void setPolling_url(String polling_url)	{ this.polling_url = polling_url; }
	public void setPolling_authentication_key(String polling_authentication_key) { this.polling_authentication_key = polling_authentication_key; }
	public void setPolling_period(long polling_period) { this.polling_period = polling_period; }
	public void setSecret_key(String secret_key)		{ this.secret_key = secret_key; }
	public void setDescription(String description)	{ this.description = description;}
	public void setLast_polled(long last_polled)	{ this.last_polled = last_polled; }
	public void setLast_posted(long last_posted)		{ this.last_posted = last_posted; }


	public String toString() {
		return "Resource: "+id+","+label+","+polling_url;
	}
}
