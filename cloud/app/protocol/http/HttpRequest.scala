package protocol.http

import protocol.Request
import play.mvc.Http
import java.net.URI
import org.apache.http.entity.ContentType

class HttpRequest(request: Http.Request) extends Request {
  override def method: String = request.method()

  override def uri: URI = URI.create(request.uri())

  override def headers: java.util.Map[String, Array[String]] = request.headers()

  override def params: java.util.Map[String, Array[String]] = request.queryString()

  private lazy val ct =
    Option(request.getHeader("Content-Type"))
      .map(ContentType.create)
      .getOrElse(ContentType.APPLICATION_OCTET_STREAM)

  override def contentType: String = ct.getMimeType()
  override def contentLength: Long = body.length()
  override def contentEncoding: String = ct.getCharset().displayName()

  override def body: String = request.body().asText()
}