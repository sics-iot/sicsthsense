package protocol.coap

import scala.collection.JavaConversions.seqAsJavaList
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Failure
import scala.util.Success

import ch.ethz.inf.vs.californium
import ch.ethz.inf.vs.californium.coap.DELETERequest
import ch.ethz.inf.vs.californium.coap.GETRequest
import ch.ethz.inf.vs.californium.coap.POSTRequest
import ch.ethz.inf.vs.californium.coap.PUTRequest
import ch.ethz.inf.vs.californium.coap.registries.CodeRegistry
import ch.ethz.inf.vs.californium.endpoint.resources.LocalResource
import models.Resource
import play.api.Logger
import play.core.PathPattern

class ProxyResource(subpath: Option[String] = None) extends CoapResource("api/proxy") {

  protected val pathPattern = PathPattern(Seq(singleComponentPathPart("key")))

  protected lazy val params = pathPattern(subpath.getOrElse(""))
  protected lazy val keyParam = params.get("key").fold(_ => None, Some(_))

  override def getResource(path: String, last: Boolean): LocalResource =
    if (subpath.isDefined) ProxyResource.this
    else new ProxyResource(Option(path))

  override def performGET(request: GETRequest): Unit =
    proxy(request)

  override def performPOST(request: POSTRequest): Unit =
    proxy(request)

  override def performPUT(request: PUTRequest): Unit =
    proxy(request)

  override def performDELETE(request: DELETERequest): Unit =
    proxy(request)

  private def proxy(request: californium.coap.Request): Unit = secured(request) {
    if (keyParam.isEmpty) {
      return respond(request, CodeRegistry.RESP_BAD_REQUEST, "Key required")
    }

    val resource = Resource.getByKey(keyParam.get)

    if (resource == null) {
      return respond(request, CodeRegistry.RESP_NOT_FOUND, "Key does not exist")
    }

    // Translate the coap request into uniform format
    val req = CoapProtocol.translateRequest(request)

    if (!resource.hasUrl()) {
      return respond(request, CodeRegistry.RESP_FORBIDDEN, "Resource des not have an origin-server.")
    }

    // Execute the request according to the resurces url
    resource.request(req).getWrappedPromise().andThen {
      case Success(response) =>
        val res = for {
          code <- CoapTranslator.getCoapStatusCode(response.status)
          options = CoapTranslator.getCoapOptions(response.headers)
        } yield {
          val res = request.getResponse()
          res.setCode(code)
          res.setOptions(options)
          res.setPayload(response.body)
          res
        }

        res match {
          case Some(res) =>
            request.respond(res)
          case None =>
            Logger.error(s"Mapping to result protocol failed. Response was $response")
            respond(request, CodeRegistry.RESP_INTERNAL_SERVER_ERROR, "Mapping to result protocol failed.")
        }
      case Failure(t) =>
        Logger.error("Request to origin server failed.", t)
        respond(request, CodeRegistry.RESP_INTERNAL_SERVER_ERROR, "Request to origin server failed.")
    }
  }
}
