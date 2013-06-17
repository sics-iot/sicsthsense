package protocol

import java.net.URI
import scala.util.Try
import controllers.Utils

trait Response {
  /** Returns the request method, e.g. GET/POST/PUT/DELETE/... */
  def uri: URI

  /** Returns the [[protocol.Request]] corresponding to this response. */
  def request: Request

  /** Returns the Http status code. */
  def status: Int
  /** Returns the Http status text. */
  def statusText: String

  /**
   * Returns the string value of the header,
   *  concatenates multiple values with ",",
   *  raises Exception of header is not found.
   */
  def header(key: String): String =
    Option(headers.get(key))
      .map(_.mkString(","))
      .getOrElse(null)

  /** Returns a map of all headers. */
  def headers: java.util.Map[String, Array[String]]

  /** Returns the Content-Type header or application/octetstream as default. */
  def contentType: String
  /** Returns the length of the body. */
  def contentLength: Long
  /** Returns the Content-Encoding header or null. */
  def contentEncoding: String

  /** Returns the request body decoded using contentEncoding. */
  def body: String
  
  def receivedAt: Long

  private val maxAge = """max-age=(\\d+)""".r

  def expires(): Long = {
    val ma = for {
      h <- Option(header("Cache-Control"))
      m <- maxAge.findFirstMatchIn(h)
      
      if m.groupCount > 1
      age <- Try(m.group(1).toLong).toOption
      
      if age > 0 && receivedAt > 0
    } yield age + receivedAt

    return ma.getOrElse(0)
  }
}