package controllers

import models.Thing
import play.api._
import play.api.mvc._
import play.api.mvc.Results._

object RestThing extends Controller {
    
  def register(url: String) = Action {
    if (Thing.register(url)) Ok("Registered: " + url)
    else NotFound
  }
    
  def remove(id: String) = Action {
    if (Thing.remove(id)) Ok("Removed" + id)
    else NotFound
  }
	
}
