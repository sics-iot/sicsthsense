package protocol

import java.net.URI
import scala.util.Try

trait Message {
  /** Returns the request uri.  */
  def uri: URI

  /**
   * Returns the header value concatenated,
   * @param key the header name
   * @return the values of the header concatenated with ',' or "" if the key is not found
   */
  def header(key: String): String =
    headers.get(key)
      .map(_.mkString(","))
      .getOrElse(null)

  /**
   * Returns the header value converted to an integer,
   * @param key the header name
   * @param default the default value returned if the header is not found or conversion fails
   * @return the converted header value or default
   */
  def intHeader(key: String, default: Int): Int =
    Option(header(key)).flatMap(v => Try(v.toInt).toOption).getOrElse(default)

  /**
   * Returns the header value converted to a long,
   * @param key the header name
   * @param default the default value returned if the header is not found or conversion fails
   * @return the converted header value or default
   */
  def longHeader(key: String, default: Long): Long =
    Option(header(key)).flatMap(v => Try(v.toLong).toOption).getOrElse(default)

  /** Returns a map of all headers. */
  def headers: Map[String, Array[String]]

  /** Returns the Content-Type header or application/octetstream as default. */
  def contentType: String

  /** Returns the length of the body. */
  def contentLength: Long

  /** Returns the request body. */
  def body: String
}
