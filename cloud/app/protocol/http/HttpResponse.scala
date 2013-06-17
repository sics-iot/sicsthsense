package protocol.http

import protocol.Response
import play.api.libs.ws
import java.net.URI
import scala.collection.JavaConversions.{ mapAsJavaMap, mapAsScalaMap }
import org.apache.http.entity.ContentType
import protocol.Request
import controllers.Utils

class HttpResponse(response: ws.Response) extends Response {
  override def request: Request = ???
  
  override def uri: URI = response.getAHCResponse.getUri()

  override def status: Int = response.status
  override def statusText: String = response.statusText

  override def header(key: String): String =
    headers(key).head

  override def headers: java.util.Map[String, Array[String]] =
    mapAsJavaMap(
      mapAsScalaMap(response.ahcResponse.getHeaders)
        .mapValues(_.toArray(Array[String]()))
    )
    
  private lazy val ct =
    Option(response.getAHCResponse.getContentType())
      .map(ContentType.create)
      .getOrElse(ContentType.APPLICATION_OCTET_STREAM)

  override def contentType: String = ct.getMimeType()
  override def contentLength: Long = body.length()
  override def contentEncoding: String = ct.getCharset().displayName()
  
  override val receivedAt: Long = Utils.currentTime

  override def body: String = response.body
}