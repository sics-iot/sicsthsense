/**
 *
 */
package protocol.coap

import java.io.ByteArrayInputStream
import java.io.InputStream
import java.net.URI
import java.net.URL
import java.net.URLConnection
import java.net.URLStreamHandler

import scala.Array.canBuildFrom
import scala.collection.JavaConversions.mapAsScalaMap
import scala.concurrent.Await
import scala.concurrent.Future
import scala.concurrent.Promise
import scala.concurrent.duration.Duration

import ch.ethz.inf.vs.californium.coap.DELETERequest
import ch.ethz.inf.vs.californium.coap.GETRequest
import ch.ethz.inf.vs.californium.coap.POSTRequest
import ch.ethz.inf.vs.californium.coap.PUTRequest
import ch.ethz.inf.vs.californium.coap.{Response => CoapResponse}
import ch.ethz.inf.vs.californium.coap.ResponseHandler
import ch.ethz.inf.vs.californium.coap.registries.MediaTypeRegistry
import ch.ethz.inf.vs.californium.util.HttpTranslator
import protocol.Protocol
import protocol.Response
import protocol.Translator

object CoapProtocol extends Protocol {

  private class CoapUrlStreamHandler extends URLStreamHandler {
    override def openConnection(url: URL): URLConnection = new CoapUrlConnection(url)
  }

  private val handler = new CoapUrlStreamHandler()

  def createUrl(url: String): URL =
    new URL(null, url, handler)

  def createConnection(url: String): CoapUrlConnection =
    createUrl(url).openConnection().asInstanceOf[CoapUrlConnection]

  def request(
    url: String,
    requestMethod: String,
    headers: java.util.Map[String, Array[String]],
    queryString: java.util.Map[String, Array[String]],
    body: String): Future[Response] = {

    val req = requestMethod match {
      case "GET"    => new GETRequest()
      case "POST"   => new POSTRequest()
      case "PUT"    => new PUTRequest()
      case "DELETE" => new DELETERequest()
      case _        => throw new IllegalArgumentException(s"Unknown request type: $requestMethod")
    }

    val qs = queryString.map {
      case ((key, values)) =>
        key + "=" + (values.length match {
          case 0 => ""
          case 1 => values(0)
          case _ => "[" + values.mkString(",") + "]"
        })
    }.mkString("&")

    val ops = HttpTranslator.getCoapOptions(headers.toArray.map {
      case ((key, values)) => new org.apache.http.message.BasicHeader(key, values.mkString(","))
    })

    val responsePromise = Promise[Response]

    req.setURI(url)
    req.setUriQuery(qs)
    req.setOptions(ops)
    req.registerResponseHandler(new ResponseHandler() {
      override def handleResponse(resp: CoapResponse): Unit =
        responsePromise.success(new Response() {
          override val uri: URI = new URI("coap", resp.getUriHost(), resp.getUriPath(), resp.getUriQuery(), null)

          override val status: Int = Translator.getHttpStatusCode(resp.getCode()).get
          override val statusText: String = Translator.getHttpStatusText(resp.getCode()).get

          override def header(key: String): String = headers(key).head

          override lazy val headers: Map[String, Array[String]] =
            Translator.getHttpHeaders(resp.getOptions)

          private lazy val ct = Translator.getContentType(resp)

          override lazy val contentType: String =
            if (ct == null) ""
            else ct.getMimeType()
          override lazy val contentEncoding: String =
            if (ct == null) ""
            else ct.getCharset().name()
          override lazy val contentLength: Long = body.length

          override lazy val body: String = Translator.getContent(resp)
        })
    })

    req.execute

    responsePromise.future
  }
}

class CoapUrlConnection(
  url: URL,
  requestMethod: String = "GET") extends URLConnection(url) {

  private val responsePromise = Promise[CoapResponse]

  private def response: CoapResponse =
    Await.result(responsePromise.future, Duration.Inf)

  override def connect: Unit = {
    val req = requestMethod match {
      case "GET"    => new GETRequest()
      case "POST"   => new POSTRequest()
      case "PUT"    => new PUTRequest()
      case "DELETE" => new DELETERequest()
      case _        => throw new IllegalArgumentException(s"Unknown request type: $requestMethod")
    }

    req.setURI(url.toURI())
    req.registerResponseHandler(new ResponseHandler() {
      override def handleResponse(resp: CoapResponse): Unit =
        responsePromise.success(resp)
    })

    req.execute
  }

  override def getContentType: String = MediaTypeRegistry.toString(response.getContentType)

  override def getInputStream: InputStream = new ByteArrayInputStream(response.getPayload())
}
