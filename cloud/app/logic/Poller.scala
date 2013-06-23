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

package logic

import akka.actor.{Props, Cancellable, Actor}
import controllers.Utils
import models.Representation
import models.Resource
import models.Resource.UpdateMode
import models.ResourceLog
import models.StreamParser
import play.api.Logger
import play.api.libs.concurrent.Akka
import scala.collection.JavaConversions.iterableAsScalaIterable
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration.DurationLong
import scala.collection.JavaConversions.asScalaIterator


sealed trait PollingMessage

case class Poll(id: Long) extends PollingMessage

class Poller extends Actor {
  private val logger = Logger(this.getClass)

  def receive = {
    case p@Poll(id) =>
      val resource = Resource.getById(id)

      logger.debug(s"Polling $id, ${resource.getUrl()}, ${resource.updateMode}")

      val response = for {
      // Only to enter Future monad
        _ <- Future()

        if resource.updateMode == UpdateMode.Poll
        if resource.hasUrl()

        requestTime = Utils.currentTime()

        _ = logger.info(s"Requesting representation from resource $id")

        res <- resource.request().getWrappedPromise()
      } yield {
        logger.info(s"Received response from resource $id")

        val repr = Representation.fromResponse(res, resource)
        repr.save()
        logger.debug(s"Stored Representation for resource $id")

        val log = ResourceLog.fromResponse(resource, res, requestTime, Utils.currentTime())
        log.save()
        logger.debug(s"Stored ResourceLog for resource $id")

        for (sp <- StreamParser.forResource(resource)) {
          val data = sp.parse(res.body, res.contentType)
          sp.stream.post(data, Utils.currentTime())
        }
        logger.debug(s"Updated Streams for resource $id")

        resource.lastPolled = Utils.currentTime()
        resource.save()
        logger.debug(s"Updated resource $id")
      }

      response.onFailure { case t => logger.error("Error while polling", t)}
      response.onComplete { _ => context.system.scheduler.scheduleOnce(resource.pollingPeriod.seconds, self, p)}
  }
}

object Poller {
  private lazy val system = Akka.system(play.api.Play.current)
  private lazy val scheduler = system.scheduler
  private lazy val poller = system.actorOf(Props[Poller])

  def initialize: Unit = synchronized {
    val pollResources = Resource.find.where()
      .gt("pollingPeriod", 0)
      .select("id, pollingPeriod")
      .findIterate()
    var count = 0

    for (res <- pollResources) {
      schedulePoll(res.id, res.pollingPeriod)
      count += 1
    }

    Logger.info(s"Started polling on $count resources")
  }

  def schedulePoll(id: Long, seconds: Long): Cancellable = {
    scheduler.scheduleOnce(seconds.seconds, poller, Poll(id))
  }
}
