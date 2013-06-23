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
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import scala.collection.JavaConversions.asScalaIterator
import scala.concurrent.duration.DurationLong

import akka.actor.Props
import logic.Poll
import logic.Poller
import models.Resource
import play.api.Application
import play.api.GlobalSettings
import play.api.Logger
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.libs.Akka
import protocol.coap.CoapServer

object Global extends GlobalSettings {

  override def onStart(app: Application) {
    Logger.info("Application has started")

    val akkaSystem = Akka.system

    val poller = akkaSystem.actorOf(Props[Poller])
    val pollResources = Resource.find.where()
      .gt("pollingPeriod", 0)
      .select("id, pollingPeriod")
      .findIterate()
    var count = 0

    for (res <- pollResources) {
      akkaSystem.scheduler.scheduleOnce(res.pollingPeriod.seconds, poller, Poll(res.id))
      count += 1
    }

    Logger.info(s"Started polling on $count resources")

    val coapServer = new CoapServer(app.configuration.getInt("coap.port").get)
    coapServer.start()
  }

  override def onStop(app: Application) {
    Logger.info("Application shutdown...")
    Akka.system.shutdown()
  }

}
