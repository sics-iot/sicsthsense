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
              "streams" -> Json.toJson(Seq(
                    Json.toJson("/sensors"),
                    Json.toJson("/sensors/temperature"),
                    Json.toJson("/sensors/energy"),
                    Json.toJson("/actuators/print")
                  )
               )
            )
      )
     Ok(jsonObject)
  }
  
  def currTemperature = 42 + rand.nextInt() % 20
  def currEnergy = 42 + rand.nextInt() % 20
  
  def temperature(id: Long) = Action {
    Ok(currTemperature.toString)
  }
  
  def energy(id: Long) = Action {
    Ok(currEnergy.toString)
  }
  
  def sensors(id: Long) = Action {
    Ok(currEnergy.toString)
    val jsonObject = Json.toJson(
            Map("temperature" ->  Json.toJson(currTemperature),
                "energy" ->  Json.toJson(currEnergy))
      )
     Ok(jsonObject)
  }
  
  def print(id: Long) = Action { request =>
    val str = "DummyThing" + id + " prints: " + request.body.asText.getOrElse("[no body]");
    Ok(str)
  }
  
}
