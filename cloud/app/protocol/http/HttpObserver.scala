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
