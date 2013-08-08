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
 
public class PollSystem {
 // LoggingAdapter log = Logging.getLogger(getContext().system(), this);
	ActorSystem system;
	public Map<String, ActorRef> actors;
	public void createPoller(String name, String url, long period, String auth) {
		ActorRef actorRef = system.actorOf(Props.create(Poller.class,url),name);
		system.scheduler().schedule(
				Duration.create(2500, TimeUnit.MILLISECONDS),
				Duration.create(period, TimeUnit.MILLISECONDS),
		  actorRef, "tick", system.dispatcher(), null);
		// test if already there?

		actors.put(name, actorRef);
	}
 
	public void createPollers() {
		system = ActorSystem.create("SicsthAkkaSystem");

	}
}
