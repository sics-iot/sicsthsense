package controllers

import models.Thing
import play.api._
import play.api.mvc._
import play.api.mvc.Results._
import play.api.libs.json._
import java.util.Random

object DummyThing extends Controller {
  
  val rand = new Random(System.currentTimeMillis())
  
  def discover() = Action {
    val jsonObject = Json.toJson(
            Map(
              "description" -> Json.toJson("SicsthSense_Dummy_1"),
              "resources" -> Json.toJson(Seq(
                    Json.toJson("/discover"),
                    Json.toJson("/sensors/temperature"),
                    Json.toJson("/sensors/energy")
                  )
               )
            )
      )
     Ok(jsonObject)
  }
  
  def temperature() = Action {
    val currTemp = 42 + rand.nextInt() % 20
    Ok(currTemp.toString)
  }
  
  def energy() = Action {
    val currEnergy = 23  + rand.nextInt() % 20
    Ok(currEnergy.toString)
  }
  
}