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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

import se.sics.sicsthsense.jdbi.*;

public class Subscription {
	@JsonProperty
	protected long id;
	@JsonProperty
	protected String topic;
	@JsonProperty
	protected long user_id;
	@JsonProperty
	protected long resource_id;
	@JsonProperty
	protected long stream_id;

	private final Logger logger = LoggerFactory.getLogger(Subscription.class);
	private StorageDAO storage = null;

	public Subscription () {
		this.storage = DAOFactory.getInstance();
	}
	public Subscription(Long id, String topic, Long user_id, Long resource_id, Long stream_id) {
		super();
		this.id          = id;
		this.topic       = topic;
		this.user_id     = user_id;
		this.resource_id = resource_id;
		this.stream_id   = stream_id;
	}

	public String toString() {
		return "Subscription: "+topic+" id: "+id;
	}

	public long getId()       { return id; }
	public long getStreamId() { return stream_id; }

	public void setId(long id)              { this.id = id; }
	public void setStreamId(long stream_id) { this.stream_id = stream_id; }

}
