package protocol.coap

import java.net.URI

import scala.collection.JavaConversions.mapAsJavaMap

import ch.ethz.inf.vs.californium.coap.DELETERequest
import ch.ethz.inf.vs.californium.coap.GETRequest
import ch.ethz.inf.vs.californium.coap.{ Message => CoapMessage }
import ch.ethz.inf.vs.californium.coap.POSTRequest
import ch.ethz.inf.vs.californium.coap.PUTRequest
import controllers.ScalaUtils
import protocol.Request

class CoapRequest(message: CoapMessage) extends Request {
  // Request uri
  override def method: String = message match {
    case _: GETRequest    => "GET"
    case _: POSTRequest   => "POST"
    case _: PUTRequest    => "PUT"
    case _: DELETERequest => "DELETE"
  }

  override def uri: URI = message.getCompleteUri()

  // Header part
  override def headers: java.util.Map[String, Array[String]] =
    CoapTranslator.getHttpHeaders(message.getOptions())

  // Query string parameters
  override def params: java.util.Map[String, Array[String]] =
    ScalaUtils.parseQueryString(uri.getQuery())

  // ContentType
  private lazy val ct = CoapTranslator.getContentType(message)

  override def contentType: String = ct.getMimeType()
  override def contentLength: Long = body.length()
  override def contentEncoding: String = ct.getCharset().displayName()

  // Body
  override def body: String = CoapTranslator.getContent(message)
}