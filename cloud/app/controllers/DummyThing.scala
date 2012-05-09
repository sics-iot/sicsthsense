package controllers

import play.api._
import play.api.mvc._
import play.api.mvc.Results._
import play.api.libs.json._
import java.util.Random

object DummyThing extends Controller {
  
  val rand = new Random(System.currentTimeMillis())
  
  def discover(id: Long) = Action {
    val jsonObject = Json.toJson(
            Map(
              "uid" -> Json.toJson("SicsthSense_Dummy_" + id),
              "resources" -> Json.toJson(Seq(
                    Json.toJson("/sensors/temperature"),
                    Json.toJson("/sensors/energy")
                  )
               )
            )
      )
     Ok(jsonObject)
  }
  
  def temperature(id: Long) = Action {
    val currTemp = 42 + rand.nextInt() % 20
    Ok(currTemp.toString)
  }
  
  def energy(id: Long) = Action {
    val currEnergy = 23  + rand.nextInt() % 20
    Ok(currEnergy.toString)
  }
  
}