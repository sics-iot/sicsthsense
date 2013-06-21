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
import play.api.Logger
import models.Resource.UpdateMode

sealed trait PollingMessage
case class Poll(id: Long) extends PollingMessage

class Poller extends Actor {
  private val logger = Logger(this.getClass)

  def receive = {
    case p @ Poll(id) =>
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

      response.onFailure { case t => logger.error("Error while polling", t) }
      response.onComplete { _ => context.system.scheduler.scheduleOnce(resource.pollingPeriod.seconds, self, p) }
  }
}
