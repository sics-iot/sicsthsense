package com.sics.sicsthsense.core;

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
	public Map<String, ActorRef> actors;

	public PollSystem(StorageDAO storage) {
		this.storage = storage;
	}

	public void createPoller(String name, String url, long period, String auth) {
		logger.info("Making poller"+name);
		ActorRef actorRef = system.actorOf(Props.create(Poller.class,url),name);
		system.scheduler().schedule(
				Duration.create(0, TimeUnit.MILLISECONDS),
				Duration.create(period, TimeUnit.MILLISECONDS),
		  actorRef, "tick", system.dispatcher(), null);
		// test if already there?

		actors.put(name, actorRef);
	}
 
	public void createPollers() {
		logger.info("Starting polling...");
		system = ActorSystem.create("SicsthAkkaSystem");

		actors = new HashMap(1000);
		// for each polled resource

		createPoller("test","http://test.com",5000,null);
		createPoller("example","http://example.com",20000,null);

	}
}
