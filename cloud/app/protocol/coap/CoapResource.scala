package protocol.coap

import ch.ethz.inf.vs.californium
import ch.ethz.inf.vs.californium.coap.registries.CodeRegistry
import ch.ethz.inf.vs.californium.coap.registries.MediaTypeRegistry
import ch.ethz.inf.vs.californium.endpoint.resources.LocalResource
import play.core.DynamicPart

class CoapResource(val path: String) extends LocalResource(path) {

  protected def singleComponentPathPart(name: String): DynamicPart =
    DynamicPart(name, """[^/]+""", encodeable = true)

  protected def multipleComponentsPathPart(name: String): DynamicPart =
    DynamicPart(name, """.+""", encodeable = false)

  protected def regexComponentPathPart(name: String, regex: String): DynamicPart =
    DynamicPart(name, regex, encodeable = false)

  protected def respond(request: californium.coap.Request, body: String): Unit =
    request.respond(CodeRegistry.RESP_CONTENT, body, MediaTypeRegistry.TEXT_PLAIN)

  protected def respond(
    request: californium.coap.Request,
    statusCode: Int,
    body: String,
    contentType: Int = MediaTypeRegistry.TEXT_PLAIN): Unit =
    request.respond(CodeRegistry.RESP_CONTENT, body, MediaTypeRegistry.TEXT_PLAIN)
}
