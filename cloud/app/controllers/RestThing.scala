package controllers

import models.Thing
import play.api._
import play.api.mvc._
import play.api.mvc.Results._
import play.libs.F.Promise

object RestThing extends Controller {
    
  def register(url: String) = Action {
    Ok(Thing.register(url).toString) 
  }
    
  def remove(id: Long) = Action {
   Thing.delete(id)
   Ok("Removed")
  }
	
}
