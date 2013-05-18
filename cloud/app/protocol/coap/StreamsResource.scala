package protocol.coap

import scala.util.Try

import ch.ethz.inf.vs.californium.coap.DELETERequest
import ch.ethz.inf.vs.californium.coap.GETRequest
import ch.ethz.inf.vs.californium.coap.POSTRequest
import ch.ethz.inf.vs.californium.coap.PUTRequest
import ch.ethz.inf.vs.californium.coap.registries.CodeRegistry
import ch.ethz.inf.vs.californium.endpoint.resources.LocalResource
import models.Resource
import models.Stream
import play.core.PathPattern

class StreamsResource(subpath: Option[String] = None) extends CoapResource("api/streams") {

  protected val pathPattern = PathPattern(Seq(singleComponentPathPart("key")))

  protected lazy val params = pathPattern(subpath.getOrElse(""))
  protected lazy val keyParam = params.get("key").fold(_ => None, Some(_))

  override def getResource(path: String, last: Boolean): LocalResource =
    if (subpath.isDefined) StreamsResource.this
    else new StreamsResource(Option(path))

  override def performGET(request: GETRequest): Unit =
    for (key <- keyParam) Try {
      val stream = Stream.getByKey(key)
      val resource = Resource.getByKey(key)
      respond(request, s"$params")
    }

  override def performPOST(request: POSTRequest): Unit =
    for (key <- keyParam) Try {
      val resource = Resource.getByKey(key)

      resource
    }

  // Don't allow PUT, it is not defined from the HTTP side
  override def performPUT(request: PUTRequest): Unit =
    request.respond(CodeRegistry.RESP_FORBIDDEN)

  // Don't allow DELETE, we have now possibility to check authorization
  override def performDELETE(request: DELETERequest): Unit =
    request.respond(CodeRegistry.RESP_FORBIDDEN)
}
