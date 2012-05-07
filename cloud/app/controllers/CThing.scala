package controllers

import play.api._
import play.api.data._
import play.api.data.Forms._
import play.api.mvc._
import play.api.mvc.Results._

import models._
import views._

object CThing extends Controller {
  
  def discover(id: Long) = Action {
    Async {
      Thing.discover(id).orTimeout(false, 10000).map { status =>
        {
          val response = Redirect(routes.CThing.get(id))
          if(!status.merge)  response.flashing("status" -> "Discovery failed")
          else               response
        }
      } 
    }
  }
  
  def register(url: String) = Action {
    Redirect(routes.CThing.get(Thing.register(url)))
  }
  
  def setName(id: Long, url: String) = Action {
    Thing.setName(id, url)
    Redirect(routes.CThing.get(id))
  }
  
  def addResource(id: Long, path: String) = Action {
    Resource.register(id, List[String](path))
    Redirect(routes.CThing.get(id))
  }
  
  def deleteResource(id: Long, resourceId: Long) = Action {
    Resource.delete(resourceId)
    Redirect(routes.CThing.get(id))
  }
  
  def get(id: Long) = Action { implicit request =>
    val thing = Thing.getById(id)
    if (thing != null)
      Ok(html.thing(thing, flash.get("status").getOrElse(null)));
    else
      NotFound(html.notfound("Thing not found"));
  }
  
  def delete(id: Long) = Action {
    Thing.delete(id)
    Redirect("/yourThings")
  }

}
