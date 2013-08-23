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

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import akka.actor.Props;
import scala.concurrent.duration.Duration;
import java.util.concurrent.TimeUnit;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;

import com.sics.sicsthsense.core.*;
import com.sics.sicsthsense.jdbi.StorageDAO;
 
public class PollSystem {
	private final Logger logger = LoggerFactory.getLogger(PollSystem.class);
	private StorageDAO storage;
 // LoggingAdapter log = Logging.getLogger(getContext().system(), this);
	private ActorSystem system;
	public Map<Long, ActorRef> actors;

	public PollSystem(StorageDAO storage) {
		this.storage = storage;
	}
 
	public void createPollers() {
		logger.info("Starting polling...");
		system = ActorSystem.create("SicsthAkkaSystem");

		actors = new HashMap<Long, ActorRef>(1000);
		List<Resource> toPoll = storage.findPolledResources();

		// for each polled resource
		for (Resource resource: toPoll) {
			createPoller(resource.getId(), resource.getLabel(),resource.getPolling_url(),resource.getPolling_period(),null);
		}
	}

	public void createPoller(long resourceId, String name, String url, long period, String auth) {
		logger.info("Making poller: "+name+" on: "+url);
		ActorRef actorRef = system.actorOf( Props.create(Poller.class,storage,resourceId,url), name);
		// schedule the actor to recieve a tick every period seconds
		system.scheduler().schedule(
				Duration.create(0, TimeUnit.MILLISECONDS),
				Duration.create(period, TimeUnit.MILLISECONDS),
		  actorRef, "probe", system.dispatcher(), null);
		// test if poller is already there?

		actors.put(resourceId, actorRef);
	}

	// tell specified poller to rebuild from the database
	public void rebuildResourcePoller(long resourceId) {
		//Resource resource = storage.findResourceById(resourceId);
		//if (resource==null) {logger.error("No resource with ID: "+resource.getId()); return;}
		ActorRef actorRef = actors.get(resourceId);
		if (actorRef==null) {logger.info("Could not find Actor for ResourceID: "+resourceId); return;}
		system.scheduler().scheduleOnce(
			Duration.create(0, TimeUnit.MILLISECONDS),
		  actorRef, "rebuild", system.dispatcher(), null);
	}

}
