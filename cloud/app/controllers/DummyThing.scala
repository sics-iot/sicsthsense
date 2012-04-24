package controllers

import models.Thing
import play.api._
import play.api.mvc._
import play.api.mvc.Results._
import play.api.libs.json._

object DummyThing extends Controller {
  
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
  
}