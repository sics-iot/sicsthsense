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
import protocol.Response
import java.net.URI
import scala.io.Codec

object HttpProtocol extends Protocol {
  def createUrl(url: String): URL = ???
  def createConnection(url: String): URLConnection = ???

  def request(
    url: String,
    requestMethod: String,
    headers: java.util.Map[String, Array[String]],
    queryString: java.util.Map[String, Array[String]],
    body: String): Future[Response] = {

    val hs = headers.mapValues(_.mkString(",")).toSeq
    val qs = queryString.mapValues {
      values =>
        values.length match {
          case 0 => ""
          case 1 => values(0)
          case _ => "[" + values.mkString(",") + "]"
        }
    }.toSeq

    val req = WS.url(url).withQueryString(qs: _*).withHeaders(hs: _*)

    val promise = requestMethod match {
      case "GET"    => req.get()
      case "POST"   => req.post(body)
      case "PUT"    => req.put(body)
      case "DELETE" => req.delete()
      case _        => throw new Exception("Unrecognized request method")
    }

    promise.map {
      res =>
        new Response {
          override val uri: URI = res.getAHCResponse.getUri()

          override val status: Int = res.status
          override val statusText: String = res.statusText

          override def header(key: String): String =
            headers(key).head

          override lazy val headers: Map[String, Array[String]] =
            mapAsScalaMap(res.ahcResponse.getHeaders).mapValues(_.to[Array]).toMap

          override val contentType: String = res.getAHCResponse.getContentType()
          override lazy val contentLength: Long = body.length
          override lazy val contentEncoding: String = Codec.UTF8.name

          override lazy val body: String = res.body
        }
    }
  }
}
