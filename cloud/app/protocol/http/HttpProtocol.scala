package protocol.http

import java.net.URL
import java.net.URLConnection
import scala.Array.canBuildFrom
import scala.collection.JavaConversions.asScalaBuffer
import scala.collection.JavaConversions.mapAsScalaMap
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import play.api.libs.ws.WS
import protocol.Protocol
import protocol.Request
import protocol.Response
import java.net.URI
import scala.io.Codec

object HttpProtocol extends Protocol[play.mvc.Http.Request, play.api.libs.ws.Response] {
  def createUrl(url: String): URL = ???
  def createConnection(url: String): URLConnection = ???

  def request(request: Request): Future[Response] = {

    val hs = request.headers.mapValues(_.mkString(",")).toSeq
    val qs = request.params.mapValues {
      values =>
        values.length match {
          case 0 => ""
          case 1 => values(0)
          case _ => "[" + values.mkString(",") + "]"
        }
    }.toSeq

    val req = WS.url(request.uri.toString()).withQueryString(qs: _*).withHeaders(hs: _*)

    val promise = request.method match {
      case "GET"    => req.get()
      case "POST"   => req.post(request.body)
      case "PUT"    => req.put(request.body)
      case "DELETE" => req.delete()
      case _        => throw new Exception("Unrecognized request method")
    }

    promise.map(translateResponse)
  }

  def translateRequest(request: play.mvc.Http.Request): Request = new HttpRequest(request)

  def translateResponse(response: play.api.libs.ws.Response): Response = new HttpResponse(response)
}
