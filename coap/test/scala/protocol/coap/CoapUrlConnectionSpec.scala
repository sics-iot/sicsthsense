package protocol.coap

import org.specs2.mutable.Specification
import java.net.URL

object CoapUrlConnectionSpec extends Specification {
  "CoapUrlConnection" should {
    "be created from url" in {
      val u = CoapProtocol.createUrl("coap://127.0.0.1:5683/helloWorld")
      val conn = u.openConnection()
      
      conn must not beNull
    }
    
    "connect" in {
      val u = CoapProtocol.createUrl("coap://127.0.0.1:5683/helloWorld")
      val conn = u.openConnection()
      conn.connect()
      
      conn must not beNull
    }
    
    "have contentType unknown" in {
      val conn = CoapProtocol.createConnection("coap://127.0.0.1:5683/helloWorld")
      conn.connect()
      
      conn.getContentType must beEqualTo("unknown").ignoreCase
    }
  }
}