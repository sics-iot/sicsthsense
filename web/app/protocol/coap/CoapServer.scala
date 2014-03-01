package protocol.coap

import ch.ethz.inf.vs.californium.endpoint.ServerEndpoint
import ch.ethz.inf.vs.californium.endpoint.resources.LocalResource
import ch.ethz.inf.vs.californium.coap.GETRequest
import ch.ethz.inf.vs.californium.coap.registries.CodeRegistry

class CoapServer(port: Int) extends ServerEndpoint(port) {
  addResource(new HelloWorldResource())

  class HelloWorldResource extends LocalResource("helloWorld") {
    setTitle("Hello-World Resource")

    override def performGET(request: GETRequest) = {

      // respond to the request
      request.respond(CodeRegistry.RESP_CONTENT, "Hello World!");
    }
  }

}
