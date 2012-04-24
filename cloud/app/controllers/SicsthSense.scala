package controllers

import play.api._
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
    Ok(html.yourThings.render(Thing.all(), flash.get("status").getOrElse(null)))
  }
  
  def register(url: String) = Action {
    val success = Thing.register(url)
    Redirect("/yourThings").flashing(
        if(success) "status" -> ("Failed to register: " + url)
        else null
     )
  }
  
  def thing(id: String) = Action { request =>
    val thing = Thing.get(id)
    if (thing != null)
      Ok(html.thing.render(thing, thing.resources.split("\n")));
    else
      NotFound(html.notfound.render(request.path + ": Thing not found"));
  }
  
}
