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
package com.sics.sicsthsense.core;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Stream {
	@JsonProperty
	private long id;
	@JsonProperty
	private String type;
	@JsonProperty
	private double latitude;
	@JsonProperty
	private double longitude;
	@JsonProperty
	private String description;
	@JsonProperty
	private boolean public_access;
	@JsonProperty
	private boolean public_search;
	@JsonProperty
	private boolean frozen;
	@JsonProperty
	private int history_size;
	@JsonProperty
	private long last_updated;
	@JsonProperty
	private String secret_key;
	@JsonProperty
	private long owner_id;
	@JsonProperty
	private long resource_id;
	@JsonProperty
	private int version;
	//public String classtype;

    public Stream() {
			//classtype="stream";
		}
    public Stream(long id) {
			this();
      this.id			= id;
    }
    public Stream(long id,
			String type,
			double latitude,
			double longitude,
			String description,
			boolean public_access,
			boolean public_search,
			boolean frozen,
			int history_size,
			long last_updated,
			String secret_key,
			long owner_id,
			long resource_id,
			int version
		) {
			this(id);
			this.type					= type;
			this.latitude			= latitude;
			this.longitude		= longitude;
			this.description	= description;
			this.public_access = public_access;
			this.public_search = public_search;
			this.frozen				= frozen;
			this.history_size = history_size;
			this.last_updated = last_updated;
			this.secret_key		= secret_key;
			this.owner_id			= owner_id;
			this.resource_id	= resource_id;
			this.version			= version;
		}

	public boolean isReadable(User user) {
		if (user.getId() == owner_id) {return true;} // owners can read
		if (public_access) {return true;}
		return false;
	}
	public boolean isWritableable(User user) {
		if (user.getId() == owner_id) {return true;} // owners can read
		return false;
	}

	public long getId()								{ return id; }
	public String getType()						{ return type; }
	public double getLatitude()				{ return latitude; }
	public double getLongitude()			{ return longitude; }
	public String getDescription()		{ return description; }
	public boolean getPublic_access() { return public_access; }
	public boolean getPublic_search() { return public_search; }
	public boolean getFrozen()				{ return frozen; }
	public int getHistory_size()			{ return history_size; }
	public long getLast_updated()			{ return last_updated; }
	public String getSecret_key()			{ return secret_key; }
	public long getOwner_id()					{ return owner_id; }
	public long getResource_id()			{ return resource_id; }
	public int getVersion()						{ return version; }

	public void setId(long id)										{ this.id = id; }
	public void setType(String type)							{ this.type = type; }
	public void setLatitude(double latitude)			{ this.latitude = latitude; }
	public void setLongitude(double longitude)		{ this.longitude = longitude; }
	public void setDescription(String description)			{ this.description = description; }
	public void setPublic_access(boolean public_access) { this.public_access = public_access; }
	public void setPublic_search(boolean public_search) { this.public_search = public_search; }
	public void setFrozen(boolean frozen)					{ this.frozen = frozen; }
	public void setHistory_size(int history_size) { this.history_size = history_size; }
	public void setLast_updated(long last_updated){ this.last_updated = last_updated; }
	public void setSecret_key(String secret_key)	{ this.secret_key = secret_key; }
	public void setOwner_id(long owner_id)				{ this.owner_id = owner_id; }
	public void setResource_id(long resource_id)	{ this.resource_id = resource_id; }
	public void setVersion(int version)						{ this.version = version; }
}
