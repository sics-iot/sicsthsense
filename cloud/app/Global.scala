import play.api._
import play.libs.Akka
import akka.util.duration._
import akka.actor._
import models._
import scala.compat.Platform

class PeriodicMonitor extends Actor {

  def receive = {
    case _ => {
      Resource.periodicAll()
    }
  }
  
}

object Global extends GlobalSettings {
     
  override def onStart(app: Application) {
    Logger.info("Application has started")
    var periodicMonitor = Akka.system.actorOf(Props[PeriodicMonitor])
    Akka.system.scheduler.schedule(0 seconds, 5 seconds, periodicMonitor, "tick")
  }  
  
  override def onStop(app: Application) {
    Logger.info("Application shutdown...")
    Akka.system.shutdown()
  }  
  
}
