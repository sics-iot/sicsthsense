package controllers

import play.api._
import play.api.data._
import play.api.data.Forms._
import play.api.mvc._
import play.api.mvc.Results._
import models._
import views._
import play.api.libs.openid.OpenID
import play.api.libs.concurrent._

object Application extends Controller with Secured {
  
  def home = IsAuthenticated { _ => implicit request => 
    Ok(html.home(null))
  }
  
  def search = IsAuthenticated { _ => implicit request => 
    Ok(html.search(Thing.all())) 
  }
  
  def yourThings = IsAuthenticated { _ => implicit request =>
    Ok(html.yourThings(
        Thing.all, Monitor.all
     ))
  }
  
  def account = IsAuthenticated { _ => implicit request =>
    Ok(html.account())
  }
    
  def help = Action { implicit request =>  
    Ok(html.help()) 
  }
  
  def login() = IsAuthenticated { username => implicit request =>
    Redirect(routes.Application.home)
  }
  
  def authenticate(openid_identifier: String) = Action { implicit request =>
    Async {
      OpenID.redirectURL(openid_identifier, routes.Application.openIDCallback.absoluteURL(),
          Seq("email" -> "http://axschema.org/contact/email",
              "firstName" -> "http://axschema.org/namePerson/first",
              "lastName" -> "http://axschema.org/namePerson/last"
          )
        ).extend( _.value match {
          case Redeemed(url) => Redirect(url)
          case Thrown(t)     => Redirect(routes.Application.home)
                .withSession("status" -> ("Failed to log in using URL: " + openid_identifier))
        })
    }
  }
  
  def logout = Action {
    Redirect(routes.Application.home).withNewSession
  }

  def openIDCallback = Action { implicit request =>
    Async {
      OpenID.verifiedId.
        extend( _.value match {
          case Redeemed(userInfo) => Redirect(routes.Application.home).withSession("id" -> userInfo.attributes("email"))
          case Thrown(t) => Redirect(routes.Application.home).withSession("status" -> ("Failed to log in " + t.toString()))
        })
    }
  }

}