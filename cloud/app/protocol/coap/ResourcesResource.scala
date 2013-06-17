package protocol.coap

import ch.ethz.inf.vs.californium.coap.DELETERequest
import ch.ethz.inf.vs.californium.coap.GETRequest
import ch.ethz.inf.vs.californium.coap.POSTRequest
import ch.ethz.inf.vs.californium.coap.PUTRequest
import ch.ethz.inf.vs.californium.coap.registries.CodeRegistry
import ch.ethz.inf.vs.californium.endpoint.resources.LocalResource
import logic.ResourceHub
import models.Resource
import play.core.PathPattern

class ResourcesResource(subpath: Option[String] = None) extends CoapResource("api/resources") {

  protected val pathPattern = PathPattern(Seq(singleComponentPathPart("key")))

  protected lazy val params = pathPattern(subpath.getOrElse(""))
  protected lazy val keyParam = params.get("key").fold(_ => None, Some(_))

  override def getResource(path: String, last: Boolean): LocalResource =
    if (subpath.isDefined) this
    else new StreamsResource(Option(path))

  override def performGET(request: GETRequest): Unit = secured(request) {
    if (keyParam.isEmpty) {
      return respond(request, CodeRegistry.RESP_BAD_REQUEST, "Key required")
    }

    val res = Resource.getByKey(keyParam.get)

    if (res == null) {
      return respond(request, CodeRegistry.RESP_NOT_FOUND, "Key does not exist")
    }

    ResourceHub.get(res).fold(
      (code, msg, ex) => code match {
        case _ => respond(request, CodeRegistry.RESP_INTERNAL_SERVER_ERROR, msg)
      },
      data => respond(request, data)
    )
  }

  override def performPOST(request: POSTRequest): Unit = secured(request) {
    if (keyParam.isEmpty) {
      return respond(request, CodeRegistry.RESP_BAD_REQUEST, "Key required")
    }

    val res = Resource.getByKey(keyParam.get)

    if (res == null) {
      return respond(request, CodeRegistry.RESP_NOT_FOUND, "Key does not exist")
    }

    val req = CoapProtocol.translateRequest(request)

    ResourceHub.post(res, req).fold(
      (code, msg, ex) => respond(request, CodeRegistry.RESP_INTERNAL_SERVER_ERROR, "Data could not be updated"),
      repr => respond(request, CodeRegistry.RESP_CONTENT, repr.content)
    )
  }

  // Don't allow PUT, it is not defined from the HTTP side
  override def performPUT(request: PUTRequest): Unit =
    request.respond(CodeRegistry.RESP_FORBIDDEN)

  // Don't allow DELETE, we have now possibility to check authorization
  override def performDELETE(request: DELETERequest): Unit =
    request.respond(CodeRegistry.RESP_FORBIDDEN)
}