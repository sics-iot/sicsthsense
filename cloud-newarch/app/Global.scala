import play.api._
import play.libs.Akka
import scala.concurrent.duration._
import akka.actor._
import models._
//import controllers.Streams
import scala.compat.Platform
import play.api.libs.concurrent.Execution.Implicits._

//class PeriodicMonitor extends Actor {

//  def receive = {
//    case _ => {
//      Streams.pollAll()
//    }
//  }
  
//}

object Global extends GlobalSettings {
     
  override def onStart(app: Application) {
    Logger.info("Application has started")
    //var periodicMonitor = Akka.system.actorOf(Props[PeriodicMonitor])
    //Akka.system.scheduler.schedule(0.seconds, 5.seconds, periodicMonitor, "tick")
  }  
  
  override def onStop(app: Application) {
    Logger.info("Application shutdown...")
    Akka.system.shutdown()
  }  
  
}
