/*
 * Copyright (c) 2013, Institute for Pervasive Computing, ETH Zurich.
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

 * Authors:
 *  26/08/2013 Adrian Kündig (adkuendi@ethz.ch)
 */

 package protocol.http

import akka.actor.{Props, Cancellable, Actor}
import protocol.{Response, Request}
import rx.{Observable, Observer}
import akka.pattern.pipe
import scala.util.{Failure, Success}
import controllers.Utils
import scala.concurrent.duration._
import play.api.Logger
import play.api.libs.concurrent.Akka
import rx.subscriptions.Subscriptions
import scala.concurrent.ExecutionContext.Implicits.global
import rx.util.functions.Action0

private sealed trait UpdateMessage

private case class Poll(req: Request, obs: Observer[Response]) extends UpdateMessage

private case class StopPoll(req: Request) extends UpdateMessage

private case class PollResponse(req: Request, obs: Observer[Response], res: Response) extends UpdateMessage

private case class PollError(req: Request, obs: Observer[Response], t: Throwable) extends UpdateMessage


private class HttpObserver extends Actor {
  private val logger = Logger(this.getClass)

  private val MIN_POLL_PERIOD = 30.seconds
  private val DEFAULT_POLL_PERIOD = 1.minute
  private var scheduledPolls = Map.empty[Request, (Observer[Response], Cancellable)]

  override def postRestart(reason: Throwable) {
    logger.error("Error while polling, actor crashed", reason)
  }

  override def postStop() {
    for ((req, (obs, cancel)) <- scheduledPolls) {
      try {
        cancel.cancel()
        obs.onCompleted()
      } catch {
        case t: Throwable => logger.error(s"Error while canceling scheduled poll", t)
      }
    }
  }

  def receive = {
    case Poll(req, obs) =>
      scheduledPolls -= req

      val res = HttpProtocol.request(req).map {
        response => PollResponse(req, obs, response)
      } recover {
        case t: Throwable => PollError(req, obs, t)
      }

      res.pipeTo(self)
    case PollResponse(req, obs, res) =>
      obs.onNext(res)

      val delay =
        if (res.expires > 0)
          MIN_POLL_PERIOD.max(res.expiresAsDuration - Utils.currentTimeAsDuration())
        else
          DEFAULT_POLL_PERIOD

      val cancel =
        context.system.scheduler.scheduleOnce(delay, self, Poll(req, obs))

      scheduledPolls = scheduledPolls.updated(req, (obs, cancel))
    case PollError(req, obs, t) =>
      t match {
        case e: Exception => obs.onError(e)
        case _ => obs.onError(new RuntimeException(t))
      }
  }
}

object HttpObserver {
  private val logger = Logger(this.getClass)
  private def system = Akka.system(play.api.Play.current)

  private def observer = system.actorFor("/user/httpObserver")

  // Instant
  def initialize() {
    system.actorOf(Props[HttpObserver], "httpObserver")
    logger.debug(s"Created $observer actor")
  }

  def observe(req: Request): Observable[Response] = {
    Observable.create[Response] { (obs: Observer[Response]) =>
      observer ! Poll(req, obs)

      Subscriptions.create(new Action0 {
        def call() {
          observer ! StopPoll(req)
        }
      })
    }
  }
}
