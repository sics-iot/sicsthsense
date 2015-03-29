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
import akka.actor.Cancellable;

import com.fasterxml.jackson.databind.ObjectMapper;

import se.sics.sicsthsense.core.*;
import se.sics.sicsthsense.jdbi.StorageDAO;

public class PollSystem {
	private static PollSystem singleton;
	private final Logger logger = LoggerFactory.getLogger(PollSystem.class);
	private StorageDAO storage;
	public ObjectMapper mapper;
	private ActorSystem system;
	public Map<Long, ActorRef> actors;
	public Map<Long, Cancellable> killSwitches;

	// Static methods to ensure single instance
	public static PollSystem getInstance() {
		return singleton;
	}
	public static PollSystem build(StorageDAO storage) {
		singleton = new PollSystem(storage);
		return singleton;
	}

	public PollSystem(StorageDAO storage) {
		this.storage = storage;
		this.mapper = new ObjectMapper();
	}

	public void createPollers() {
		logger.info("Starting polling...");
		system = ActorSystem.create("SicsthAkkaSystem");

		actors = new HashMap<Long, ActorRef>(1000);
		killSwitches = new HashMap<Long, Cancellable>(1000);
		List<Resource> toPoll = storage.findPolledResources();

		// for each polled resource
		for (Resource resource: toPoll) {
			createPoller(resource.getId(), resource.getLabel(), resource.getPolling_url(), resource.getPolling_period(), null);
		}
	}
	public void createPoller(long resourceId) {
		Resource resource = storage.findResourceById(resourceId);
		createPoller(resource);
	}
	public void createPoller(Resource resource) {
		createPoller(resource.getId(), resource.getLabel(), resource.getPolling_url(), resource.getPolling_period(), null);
	}

	public void createPoller(long resourceId, String name, String url, long period, String auth) {
		logger.info("Making poller: "+name+" on: "+url);

		ActorRef actorRef = actors.get(resourceId);
		if (actorRef==null) {
			actorRef = system.actorOf( Props.create(Poller.class,storage,mapper,resourceId,url), String.valueOf(resourceId));
			actors.put(resourceId, actorRef);
		}
		if (period > 0 && url!=null && url!="") {
			// schedule the actor to recieve a tick every period seconds
			Cancellable killSwitch = system.scheduler().schedule(Duration.create(0, TimeUnit.SECONDS),
					Duration.create(period, TimeUnit.SECONDS), actorRef, "probe", system.dispatcher(), null);
			// test if poller is already there?
			killSwitches.put(resourceId,killSwitch);
		}
	}

	// tell specified poller to rebuild from the database
	public void rebuildResourcePoller(long resourceId) {
		Resource resource = storage.findResourceById(resourceId);
		if (resource==null) { // it may have been deleted
			logger.error("No resource with ID: "+resourceId);
			killSwitches.remove(resourceId);
			return;
		}
		Cancellable killSwitch = killSwitches.get(resourceId);
		if (killSwitch!=null) {killSwitch.cancel();} // Cancel polling // race condition?

		//ActorRef actorRef = actors.get(resourceId);
		//if (actorRef==null) {logger.info("Could not find Actor for ResourceID: "+resourceId); return;}
		logger.info("Rebuilding poller: "+resourceId);

		createPoller(resource);
	}

}
