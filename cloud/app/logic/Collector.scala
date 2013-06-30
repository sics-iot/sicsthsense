package logic

import akka.actor.{Props, Actor}
import play.api.Logger
import play.api.libs.concurrent.Akka
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

private sealed trait CollectorMessage
private case object CollectorTick extends CollectorMessage

class Collector extends Actor {
  private val COLLECTOR_PERIOD = 10.minutes

  def receive = {
    case CollectorTick =>
      ResourceHub.deleteOldRepresentations
      context.system.scheduler.scheduleOnce(COLLECTOR_PERIOD, self, CollectorTick)
  }
}

object Collector {
  private val logger = Logger(this.getClass)

  private def system = Akka.system(play.api.Play.current)

  private def collector = system.actorFor("/user/collector")

  // Instant
  def initialize() {
    system.actorOf(Props[Collector], "collector")
    logger.debug(s"Created $collector actor")
    collector ! CollectorTick
  }
}
