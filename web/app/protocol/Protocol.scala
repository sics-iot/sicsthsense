package protocol

import java.net.URL
import java.net.URLConnection

trait Protocol {
  def createUrl(url: String): URL
  def createConnection(url: String): URLConnection
}
