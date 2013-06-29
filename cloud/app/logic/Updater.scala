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
import models.ResourceLog
import models.StreamParser
import play.api.Logger
import play.api.libs.concurrent.Akka
import scala.collection.JavaConversions.asScalaIterator
import scala.collection.JavaConversions.iterableAsScalaIterable
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.DurationLong
import scala.util.{Failure, Success}
import rx.Subscription
import protocol.{Request, Response}
import play.api.http.ContentTypes


private sealed trait UpdateMessage

private case class Push(id: Long, request: Request) extends UpdateMessage

private case class StartPoll(id: Long, period: Long) extends UpdateMessage

private case class StopPoll(id: Long, period: Long) extends UpdateMessage

private case class Poll(id: Long) extends UpdateMessage

private case class PollResponse(id: Long, requestTime: Long, responseTime: Long, response: Response) extends UpdateMessage

private case class StartObserve(id: Long, failures: Int) extends UpdateMessage

private case class StopObserve(id: Long) extends UpdateMessage

private case class Notification(id: Long, response: Response) extends UpdateMessage

private case class NotificationError(id: Long, t: Throwable, failures: Int) extends UpdateMessage


private class Updater extends Actor {
  private val MAX_FAILURES = 5

  private val logger = Logger(this.getClass)
  private var observing = Map.empty[Long, Subscription]
  private var polling = Map.empty[Long, (Long, Cancellable)]

  override def preStart() {
    val pollResources = Resource.find.where()
      .gt("pollingPeriod", 0)
      .select("id, pollingPeriod")
      .findIterate()

    val pollCount = pollResources.map {
      res => Updater.poll(res.id, res.pollingPeriod)
    }.size

    Logger.info(s"Started polling on $pollCount resources")

    val observeResources = Resource.find.where()
      .eq("pollingPeriod", -1)
      .findIds()

    val observeCount = observeResources.map {
      id => Updater.observe(id.asInstanceOf[Long])
    }.size

    Logger.info(s"Started observing on $observeCount resources")
  }

  override def preRestart(reason : Throwable, message: Option[Any]) {
    logger.error(s"Updater is restarting because an exception happened while processing message $message", reason)

    for ((id, sub) <- observing) {
      try {
        logger.info(s"Updater is restarting down and thus stops observing resource '$id'")
        sub.unsubscribe()
      } catch {
        case t: Throwable => logger.error(s"Error while stopping observe of resource '$id'", t)
      }
    }

    for ((id, (_, cancelable)) <- polling) {
      try {
        logger.info(s"Updater is restarting down and thus stops polling resource '$id'")
        cancelable.cancel()
      } catch {
        case t: Throwable => logger.error(s"Error while stopping polling of resource '$id'", t)
      }
    }
  }

  def receive = {
    case m@StartPoll(id, period) =>
      def startPolling() {
        logger.info(s"Starting to poll resource '$id'")

        val cancel = context.system.scheduler.schedule(
          0.seconds, period.seconds, self, Poll(id)
        )

        polling = polling.updated(id, (period, cancel))
      }

      if (polling.contains(id)) {
        val (pPeriod, cancelable) = polling(id)

        if (pPeriod != period) {
          logger.trace(s"Before polling resource '$id' the old polling schedule is canceled because the periods differ")
          cancelable.cancel()

          startPolling()
        } else {
          logger.trace(s"Received $m but there is already polling scheduled for resource '$id' with the same period")
          // Do nothing, same period
        }
      } else {
        startPolling()
      }
    case m@Poll(id) =>
      val resource = Resource.getById(id)
      val shouldUpdate = resource.isPoll && resource.hasUrl()

      if (shouldUpdate) {
        logger.trace(s"Polling resource '$id' URL: ${resource.getUrl()}")

        val requestTime = Utils.currentTime()
        val responseP = resource.request().getWrappedPromise()

        responseP.onComplete {
          case Success(res) =>
            self ! PollResponse(id, requestTime, Utils.currentTime(), res)
          case Failure(t) =>
            logger.error(s"Error while polling resource '$id'", t)
        }
      } else {
        logger.trace(s"Received $m but resource '$id' is not in poll mode or has no url")
      }
    case PollResponse(id, reqT, resT, res) =>
      val resource = Resource.getById(id)

      logger.info(s"Received response from resource $id")

      val repr = Representation.fromResponse(res, resource)
      repr.save()
      logger.trace(s"Stored Representation for resource $id")

      val log = ResourceLog.fromResponse(resource, res, reqT, resT)
      log.save()
      logger.trace(s"Stored ResourceLog for resource $id")

      for (sp <- StreamParser.forResource(resource)) {
        val data = sp.parse(res.body, res.contentType)
        sp.stream.post(data, Utils.currentTime())
      }
      logger.trace(s"Updated Streams for resource $id")

      resource.lastPolled = Utils.currentTime()
      resource.save()
    case m@StartObserve(id, failures) =>
      val resource = Resource.getById(id)

      if (resource.isObserve && !observing.contains(id)) {
        logger.info(s"Starting to observe resource '$id'")

        val sub =
          resource
            .observe()
            .subscribe(
          {
            (res: Response) => self ! Notification(id, res)
          }, {
            (err: Throwable) => self ! NotificationError(id, err, failures)
          }
          )

        observing = observing.updated(id, sub)
      } else if (observing.contains(id)) {
        logger.trace(s"Received $m but already observing '$id'")
      } else {
        logger.trace(s"Received $m but resource '$id' is not in observe mode")
      }
    case m@StopObserve(id) =>
      val resource = Resource.getById(id)

      if (!resource.isObserve && observing.contains(id)) {
        logger.info(s"Stopping to observe '$id'")

        observing(id).unsubscribe()
        observing -= id
      } else if (!observing.contains(id)) {
        logger.trace(s"Received $m but already stopped observing '$id'")
      } else {
        logger.trace(s"Received $m but resource '$id' is still in observe mode")
      }
    case Push(id, request) =>
      val resource = Resource.getById(id)

      logger.info(s"Post received from URI: ${request.uri}, Content-Type: ${request.contentType}, Content: ${request.body}");

      val resourceLog = ResourceLog.fromRequest(resource, request, Utils.currentTime())
      ResourceLog.create(resourceLog)
      logger.trace(s"Stored ResourceLog for resource $id")

      val repr = Representation.fromRequest(request, resource)
      Representation.create(repr)
      logger.trace(s"Stored Representation for resource $id")

      // if first POST (and no poll's), auto make parsers
      if (resource.streamParsers.isEmpty() && resource.isUnused() && request.contentType.equalsIgnoreCase(ContentTypes.JSON)) {
        logger.info(s"Resource '$id' has no StreamParsers defined but got pushed. Automatically creating StreamParsers");
        ResourceHub.createParsersFromJson(resource, request)
      }

      for (sp <- StreamParser.forResource(resource)) {
        val points = sp.parse(request.body, request.contentType)
        sp.stream.post(points, Utils.currentTime())
      }
      logger.trace(s"Updated Streams for resource $id")
    case Notification(id, res) =>
      val resource = Resource.getById(id)

      logger.info(s"Received notification from resource $id")

      val log = ResourceLog.fromResponse(resource, res)
      ResourceLog.create(log)
      logger.trace(s"Stored ResourceLog for resource $id")

      val repr = Representation.fromResponse(res, resource)
      Representation.create(repr)
      logger.trace(s"Stored Representation for resource $id")

      for (sp <- StreamParser.forResource(resource)) {
        val data = sp.parse(res.body, res.contentType)
        sp.stream.post(data, Utils.currentTime())
      }
      logger.trace(s"Updated Streams for resource $id")
    case NotificationError(id, err, failures) =>
      observing -= id
      logger.warn(s"Resource '$id' has now $failures failures", err)

      if (failures > MAX_FAILURES) {
        logger.warn(s"Resource '$id' had more than $MAX_FAILURES failures")
      } else {
        self ! StartObserve(id, failures + 1)
      }
  }
}

object Updater {
  private val logger = Logger(this.getClass)

  private def system = Akka.system(play.api.Play.current)

  private def updater = system.actorFor("/user/updater")

  // Instant
  def initialize() {
    system.actorOf(Props[Updater], "updater")
    logger.trace(s"Created $updater actor")
  }

  def push(id: Long, request: Request) {
    updater ! Push(id, request)
  }

  def poll(id: Long, seconds: Long) {
    updater ! StartPoll(id, seconds)
  }

  def stopPoll(id: Long, seconds: Long) {
    updater ! StopPoll(id, seconds)
  }

  def observe(id: Long) {
    updater ! StartObserve(id, 0)
  }

  def stopObserve(id: Long) {
    updater ! StopObserve(id)
  }
}
