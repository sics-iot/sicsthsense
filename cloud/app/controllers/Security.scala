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

trait Secured {
  
  /* Retrieve the connected username */
  private def username(request: RequestHeader) = request.session.get("id")

  /* Redirect to login if the user in not authorized */
  private def onUnauthorized(request: RequestHeader) = { Ok(html.login()(Session())) }
  
  /* Action for authenticated users */
  def IsAuthenticated(f: => String => Request[AnyContent] => Result) = 
    Security.Authenticated(username, onUnauthorized) { user =>
      Action(request => f(user)(request))
    }

}
