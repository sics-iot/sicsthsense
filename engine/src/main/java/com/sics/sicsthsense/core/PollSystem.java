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
