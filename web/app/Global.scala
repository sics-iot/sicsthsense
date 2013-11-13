import play.api._
import play.libs.Akka
import scala.concurrent.duration._
import akka.actor._
import models._
import scala.compat.Platform
import play.api.libs.concurrent.Execution.Implicits._
import protocol.coap.CoapServer
import java.net.URL
import java.net.URLStreamHandlerFactory
import java.net.URLStreamHandler
import controllers.Poller

class PeriodicMonitor extends Actor {

  def receive = {
    case _ => {
      /* There should actually be a fully developed logic engine 
      / determining when sources should be polled. For the moment,
      / we just poll everyone each second and let them choose to 
      / not act, according to their polling freq.
      */
      //Logger.info("Periodic Monitor")
      //Poller.pollAll()
    }
  }

}

object Global extends GlobalSettings {

  override def onStart(app: Application) {
    Logger.info("Application has started")
    
    val akkaSystem = Akka.system
    
    val periodicMonitor = akkaSystem.actorOf(Props[PeriodicMonitor])
    akkaSystem.scheduler.schedule(0.seconds, 5.seconds, periodicMonitor, "tick")
    
    val coapServer = new CoapServer(app.configuration.getInt("coap.port").get)
    coapServer.start()
  }

  override def onStop(app: Application) {
    Logger.info("Application shutdown...")
    Akka.system.shutdown()
  }

}
