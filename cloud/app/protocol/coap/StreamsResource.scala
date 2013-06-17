package protocol.coap

import scala.collection.JavaConversions.mapAsScalaMap
import scala.util.Try

import ch.ethz.inf.vs.californium.coap.DELETERequest
import ch.ethz.inf.vs.californium.coap.GETRequest
import ch.ethz.inf.vs.californium.coap.POSTRequest
import ch.ethz.inf.vs.californium.coap.PUTRequest
import ch.ethz.inf.vs.californium.coap.registries.CodeRegistry
import ch.ethz.inf.vs.californium.endpoint.resources.LocalResource
import controllers.ScalaUtils
import models.DataPoint
import models.Stream
import play.core.PathPattern

class StreamsResource(subpath: Option[String] = None) extends CoapResource("api/streams") {

  protected val pathPattern = PathPattern(Seq(singleComponentPathPart("key")))

  protected lazy val params = pathPattern(subpath.getOrElse(""))
  protected lazy val keyParam = params.get("key").fold(_ => None, Some(_))

  override def getResource(path: String, last: Boolean): LocalResource =
    if (subpath.isDefined) StreamsResource.this
    else new StreamsResource(Option(path))

  override def performGET(request: GETRequest): Unit = secured(request) {
    if (keyParam.isEmpty) {
      return respond(request, CodeRegistry.RESP_BAD_REQUEST, "Key required")
    }

    val stream = Stream.getByKey(keyParam.get)

    if (stream == null) {
      return respond(request, CodeRegistry.RESP_NOT_FOUND, "Key does not exist")
    }

    val qs = mapAsScalaMap(ScalaUtils.parseQueryString(request.getUriQuery()))

    if (qs.contains("tail")) {
      val tail = qs.get("tail").flatMap(v => Try(Integer.parseInt(v.mkString)).toOption)

      if (tail.isDefined) {
        return respond(request, DataPoint.getTail(stream, tail.get))
      } else {
        return respond(request, CodeRegistry.RESP_BAD_REQUEST, "Query parameter 'tail' is maleformed.'")
      }
    } else if (qs.contains("last")) {
      val last = qs.get("last").flatMap(v => Try(Integer.parseInt(v.mkString)).toOption)

      if (last.isDefined) {
        return respond(request, DataPoint.getLast(stream, last.get))
      } else {
        return respond(request, CodeRegistry.RESP_BAD_REQUEST, "Query parameter 'last' is maleformed.'")
      }
    } else if (qs.contains("since")) {
      val since = qs.get("since").flatMap(v => Try(Integer.parseInt(v.mkString)).toOption)

      if (since.isDefined) {
        return respond(request, DataPoint.getSince(stream, since.get))
      } else {
        return respond(request, CodeRegistry.RESP_BAD_REQUEST, "Query parameter 'since' is maleformed.'")
      }
    }

    return respond(request, DataPoint.getTail(stream, 50))
  }

  override def performPOST(request: POSTRequest): Unit =
    request.respond(CodeRegistry.RESP_FORBIDDEN)

  // Don't allow PUT, it is not defined from the HTTP side
  override def performPUT(request: PUTRequest): Unit =
    request.respond(CodeRegistry.RESP_FORBIDDEN)

  // Don't allow DELETE, we have now possibility to check authorization
  override def performDELETE(request: DELETERequest): Unit =
    request.respond(CodeRegistry.RESP_FORBIDDEN)
}
