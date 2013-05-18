package protocol.coap

import java.net.URI
import ch.ethz.inf.vs.californium.coap
import protocol.Response
import protocol.Request
import scala.collection.JavaConversions.mapAsJavaMap

class CoapResponse(response: coap.Response) extends Response {
  // Response Uri
  override def uri: URI =
    new URI("coap", response.getUriHost(), response.getUriPath(), response.getUriQuery(), null)
  
  // Request
  override def request: Request = new CoapRequest(response.getRequest())

  // Status code and text
  override def status: Int = CoapTranslator.getHttpStatusCode(response.getCode()).get
  override def statusText: String = CoapTranslator.getHttpStatusText(response.getCode()).get

  // Headers
  override def headers: java.util.Map[String, Array[String]] =
    CoapTranslator.getHttpHeaders(response.getOptions)

  // Content Type
  private lazy val ct = CoapTranslator.getContentType(response)

  override def contentType: String =
    if (ct == null) ""
    else ct.getMimeType()
  override def contentEncoding: String =
    if (ct == null) ""
    else ct.getCharset().name()
  override def contentLength: Long = body.length

  // Body
  override def body: String = CoapTranslator.getContent(response)

}