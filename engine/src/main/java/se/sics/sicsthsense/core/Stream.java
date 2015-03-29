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
import java.util.List;
import java.util.ArrayList;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import se.sics.sicsthsense.*;
import se.sics.sicsthsense.jdbi.*;
import se.sics.sicsthsense.core.functions.*;

@JsonInclude(Include.NON_NULL)
public class Stream {
	@JsonProperty
	protected long id;
	@JsonProperty
	protected String type;
	@JsonProperty
	protected String label;
	@JsonProperty
	protected double latitude;
	@JsonProperty
	protected double longitude;
	@JsonProperty
	protected String description;
	@JsonProperty
	protected boolean public_access;
	@JsonProperty
	protected boolean public_search;
	@JsonProperty
	protected boolean frozen;
	@JsonProperty
	protected int history_size;
	@JsonProperty
	protected long last_updated;
	@JsonProperty
	protected String secret_key;
	@JsonProperty
	protected long owner_id;
	@JsonProperty
	protected long resource_id;
	@JsonProperty
	protected int version;
	// name of potential aggregation function
	@JsonProperty
	protected String function;
	//@JsonDeserialize(as=ArrayList.class, contentAs=Long.class)
	@JsonProperty
	public List<Long> antecedents;

	// actions to take on triggers
	public List<Trigger> triggers;

	private final Logger logger = LoggerFactory.getLogger(Stream.class);
	private StorageDAO storage = null;

    public Stream() {
		this.storage = DAOFactory.getInstance();
		this.type = "D";
		this.secret_key = UUID.randomUUID().toString();
		}
    public Stream(StorageDAO storage, long id) {
		this();
		this.id = id;
    }
    public Stream(
			long id,
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
			String function,
			int version
		) {
			this(null,id);
			this.type	= type;
			this.latitude	= latitude;
			this.longitude	= longitude;
			this.description	= description;
			this.public_access = public_access;
			this.public_search = public_search;
			this.frozen				= frozen;
			this.history_size = history_size;
			this.last_updated = last_updated;
			this.secret_key		= secret_key;
			this.owner_id			= owner_id;
			this.resource_id	= resource_id;
			this.function			= function;
			this.version			= version;
		}

	public boolean isReadable(StorageDAO storage, String token) {
		if (public_access) {return true;}
		if (token.equals(secret_key)) {return true;} // owners can read
		User owner = storage.findUserById(owner_id);
		if (owner!=null && token.equals(owner.getToken())) {return true;} // owners can read
		return false;
	}
	public boolean isReadable(User user) {
		if (public_access) {return true;}
		if (user.getId() == owner_id) {return true;} // owners can read
		return false;
	}
	public boolean isWritableable(User user) {
		if (user.getId() == owner_id) {return true;} // owners can read
		return false;
	}

	// when an antecedent input stream has changed, update this stream's datapoints
	public void update(StorageDAO storage) throws Exception {
		long sid =getId();
		//logger.info("Updating stream: "+sid);

		List<Long> antecedents = storage.findAntecedents(getId());
		if (antecedents==null) { logger.error("Antecedents are null! ID:"+getId()); return; }
        try {
            List<DataPoint> newPoints = performFunction(antecedents);
            // add to stream
            for (DataPoint p: newPoints) {
			p.setStreamId(sid);
			Utils.insertDataPoint(storage,p);
            }
            //notifyDependents();
        } catch (IOException e) {
          logger.error("Error: function failed! Stream ID: "+getId()+" "+e.toString());
        }
	}

	public void notifyDependents(StorageDAO storage) throws Exception {
		//logger.info("Notify dependents of stream "+getId());
		List<Long> dependents = storage.findDependents(getId());
		// update dependents!
		for (Long dependent: dependents) {
			Stream ds = storage.findStreamById(dependent);
			if (ds!=null) { ds.update(storage);
			} else { logger.warn("Dependent stream not found, id:"+dependent); }
		}
	}
	public static void notifyDependents(StorageDAO storage, long stream_id) throws Exception {
		List<Long> dependents = storage.findDependents(stream_id);
		// update dependents!
		for (Long dependent: dependents) {
			Stream ds = storage.findStreamById(dependent);
			if (ds!=null) { ds.update(storage);
			} else { System.out.println("Dependent stream not found, id:"+dependent); }
		}
	}

	public List<DataPoint> performFunction(List<Long> antecedents) throws Exception {
		Function function = null;
		//logger.info("Performing function of stream "+getId());
		if (antecedents==null) { logger.error("Antecedents are null!!"); return null;	}

		// do nothing if the function is not set
		if (this.getFunction()==null || this.getFunction()=="") {return new ArrayList<DataPoint>();}

		if ("mean".equals(this.getFunction())) {
			function = new Mean(storage);
		} else if ("min".equals(this.getFunction())) {
			function = new Min(storage);
		} else if ("max".equals(this.getFunction())) {
			function = new Max(storage);
		} else if ("median".equals(this.getFunction())) {
			function = new Median(storage);
		} else if ("intensity".equals(this.getFunction())) {
			function = new Intensity(storage,getId());
		} else if ("smooth".equals(this.getFunction())) {
			function = new Smooth(storage);
		} else if ("meansmooth".equals(this.getFunction())) {
			function = new MeanSmooth(storage);
		} else {
			logger.error("Unknown function "+this.getFunction()+"! Stream ID: "+getId());
			return new ArrayList<DataPoint>();
		}
		return function.apply(antecedents);
	}

	public void testTriggers(DataPoint dp) {
		//logger.info("testing triggers on point :"+dp.toString());
		if (this==null) {return;}
		storage = DAOFactory.getInstance();
		this.triggers =  storage.findTriggersByStreamId(this.getId());
		if (triggers!=null) {
			for (Trigger t: triggers) {
				//logger.warn("Testing trigger: "+t.toString());
				t.test(dp);
			}
		}
	}
	public boolean isAuthorised(String key) {
		if (this.secret_key.equals(key)) {
			return true;
		}
		return false;
	}



	public long getId()								{ return id; }
	public String getType()						{ return type; }
	public String getLabel()					{ return label; }
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
	public String getFunction()				{ return function; }
	public int getVersion()						{ return version; }

	public void setId(long id)										{ this.id = id; }
	public void setType(String type)							{ this.type = type; }
	public void setLabel(String label)						{ this.label = label; }
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
	public void setFunction(String function)			{ this.function = function; }
	public void setVersion(int version)						{ this.version = version; }
}
