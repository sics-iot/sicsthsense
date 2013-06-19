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
