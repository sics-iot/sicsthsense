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
    Monitor.create(resourceId, period)
    val thingId = Resource.getById(resourceId).thingId
    Redirect(routes.CThing.get(thingId))
  }
  
  def clear(id: Long) = Action { implicit request =>
    val monitor = Monitor.getById(id)
    if (monitor == null) NotFound(html.notfound("Monitor not found"));
    else {
      Monitor.deleteByResourceId(monitor.resourceId)
      Redirect(routes.CThing.get(monitor.resource.thingId))
    }
  }
  
  def delete(id: Long) = Action {
    val thingId = Monitor.getById(id).resource.thingId
    Monitor.delete(id)
    Redirect(routes.CThing.get(thingId))
  }

}
