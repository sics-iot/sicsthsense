package logic

import scala.collection.JavaConversions.iterableAsScalaIterable
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration.DurationLong

import akka.actor.Actor
import controllers.Utils
import models.Representation
import models.Resource
import models.ResourceLog
import models.StreamParser
import models.UpdateMode
import play.api.Logger

sealed trait PollingMessage
case class Poll(id: Long) extends PollingMessage

class Poller extends Actor {
  def receive = {
    case p @ Poll(id) =>
      val resource = Resource.getById(id)

      Logger.debug(s"Polling $id, ${resource.getUrl()}, ${resource.updateMode}")

      val response = for {
        // Only to enter Future monad
        _ <- Future()

        if resource.updateMode == UpdateMode.Poll
        if resource.hasUrl()

        requestTime = Utils.currentTime()

        _ = Logger.info(s"Requesting representation from resource $id")

        res <- resource.request().getWrappedPromise()
      } yield {
        Logger.info(s"Received response from resource $id")

        val repr = Representation.fromResponse(res, resource)
        repr.save()

        val log = ResourceLog.fromResponse(resource, res, requestTime, Utils.currentTime())
        log.save()

        for (sp <- StreamParser.forResource(resource)) {
          val data = sp.parse(res.body, res.contentType)
          sp.stream.post(data, Utils.currentTime())
        }

        resource.lastPolled = Utils.currentTime()
        resource.save()
      }

      response.onFailure { case t => Logger.error("Error while polling", t) }
      response.onComplete { _ => context.system.scheduler.scheduleOnce(resource.pollingPeriod.seconds, self, p) }
  }
}