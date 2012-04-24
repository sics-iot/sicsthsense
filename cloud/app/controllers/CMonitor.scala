package controllers

import play.api._
import play.api.data._
import play.api.data.Forms._
import play.api.mvc._
import play.api.mvc.Results._

import models._
import views._

object CMonitor extends Controller {
    
  def create(resourceId: Long, period: Long) = Action {
    Redirect("/yourThings").flashing(
        if (Monitor.create(resourceId, period))  "status" -> ("Created") 
        else                                       "status" -> ("Failed to create") 
    ) 
  }
  
  def get(id: Long) = Action { request =>
    val monitor = Monitor.getById(id)
    if (monitor != null)
      Ok(html.monitor.render(monitor));
    else
      NotFound(html.notfound.render("Monitor not found"));
  }
  
  def remove(id: Long) = Action {
    Monitor.remove(id)
    Redirect("/yourThings")
  }

}
