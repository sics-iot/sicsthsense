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
  def yourThings = Action { implicit request =>
    Ok(html.yourThings.render(
        Thing.all, Monitor.all
     ))
  }
  def account = Action { Ok(html.account.render()) }
  def help = Action { Ok(html.help.render()) }
  
}
