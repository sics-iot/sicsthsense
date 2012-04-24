package controllers

import models.Thing
import play.api._
import play.api.mvc._
import play.api.mvc.Results._
import play.libs.F.Promise

object RestThing extends Controller {
    
  def register(url: String) = Action {
    Async {
      Thing.register(url).map { success => 
        if (success) Ok("Registered: " + url)
        else NotFound
      } 
    }    
  }
    
  def remove(id: Long) = Action {
    if (Thing.remove(id)) Ok("Removed" + id)
    else NotFound
  }
	
}
