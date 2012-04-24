package controllers

import play.api._
import play.api.data._
import play.api.data.Forms._
import play.api.mvc._
import play.api.mvc.Results._

import models._
import views._

object SicsthSense extends Controller {
  
  def home = Action { Ok(html.home.render()) }
  def search = Action { Ok(html.search.render(Thing.all())) }
  def account = Action { Ok(html.account.render()) }
  def help = Action { Ok(html.help.render()) }

  def yourThings = Action { implicit request =>
    Ok(html.yourThings.render(
        Thing.all, flash.get("statusT").getOrElse(null),
        Monitor.all, flash.get("statusM").getOrElse(null))
     )
  }
  
  def register(url: String) = Action {
    Async {
      Thing.register(url).orTimeout(false, 10000).map { success =>
        Redirect("/yourThings").flashing(
            if (success.merge)  "statusT" -> ("Registered " + url) 
            else                "statusT" -> ("Failed to register " + url) 
        )
      } 
    }
  }
  
  def get(id: Long) = Action { request =>
    val thing = Thing.getById(id)
    if (thing != null)
      Ok(html.thing.render(thing));
    else
      NotFound(html.notfound.render("Thing not found"));
  }
  
  def remove(id: Long) = Action {
    Thing.remove(id)
    Redirect("/yourThings")
  }

}
