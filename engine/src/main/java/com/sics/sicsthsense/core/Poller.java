package com.sics.sicsthsense.core;

import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
 
public class Poller extends UntypedActor {
  LoggingAdapter log = Logging.getLogger(getContext().system(), this);
	public String url;

	public Poller(String url) {
		this.url=url;
	}
 
	@Override
  public void onReceive(Object message) throws Exception {
    if (message instanceof String) {
      log.info("Received String message: to probe: {}\n\n", url);
      //getSender().tell(message, getSelf());
    } else
      unhandled(message);
  }
}
