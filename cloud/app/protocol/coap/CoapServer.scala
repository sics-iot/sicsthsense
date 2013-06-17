package protocol.coap

import ch.ethz.inf.vs.californium.endpoint.ServerEndpoint

class CoapServer(port: Int) extends ServerEndpoint(port) {
  addResource(new ResourcesResource())

  addResource(new StreamsResource())

  addResource(new ProxyResource())
}