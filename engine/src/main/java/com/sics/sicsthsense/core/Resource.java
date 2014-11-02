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

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
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
	private Long parent_id;
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
		this.parent_id = null;
		this.description="";
		this.secret_key = UUID.randomUUID().toString();
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

	public void update(Resource newresource) {
		if (newresource.getLabel()!="nolabel") {setLabel(newresource.getLabel());}
		if (newresource.getDescription()!="") {setDescription(newresource.getDescription());}
		setPolling_period(newresource.getPolling_period());
		if (newresource.getPolling_url()!="") {setPolling_url(newresource.getPolling_url());}
		if (newresource.getPolling_authentication_key()!="") {setPolling_authentication_key(newresource.getPolling_authentication_key());}
	}
	public boolean isAuthorised(String key) {
		if (this.secret_key.equals(key)) {
			return true;
		}
		return false;
	}



	public long   getId()            { return id; }
	public String getVersion()       { return "1.0"; }
	public String getLabel()         { return label; }
	public long   getOwner_id()      { return owner_id; }
	public Long   getParent_id()	 { return parent_id; }
	public String getPolling_url()	 { return polling_url; }
	public long   getPolling_period(){ return polling_period; }
	public String getSecret_key()    { return secret_key; }
	public String getDescription()	 { return description; }
	public long   getLast_polled()	 { return last_polled; }
	public long   getLast_posted()	 { return last_posted; }
	public String getPolling_authentication_key()	{ return polling_authentication_key; }
	//public int getVersion(){ return version; }

	public void setId(long id)                         { this.id = id; }
	public void setLabel(String label)                 { this.label = label; }
	public void setVersion(String version)             { this.version = version; }
	public void setOwner_id(long owner_id)             { this.owner_id = owner_id; }
	public void setParent_id(Long parent_id)           { this.parent_id = parent_id; }
	public void setPolling_url(String polling_url)     { this.polling_url = polling_url; }
	public void setPolling_period(long polling_period) { this.polling_period = polling_period; }
	public void setSecret_key(String secret_key)       { this.secret_key = secret_key; }
	public void setDescription(String description)	   { this.description = description;}
	public void setLast_polled(long last_polled)	   { this.last_polled = last_polled; }
	public void setLast_posted(long last_posted)       { this.last_posted = last_posted; }
	public void setPolling_authentication_key(String polling_authentication_key) { this.polling_authentication_key = polling_authentication_key; }

	public String toString() { return "Resource: "+id+","+label+","+polling_url; }
}
