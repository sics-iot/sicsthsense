package protocol

import java.net.URI
import scala.util.Try

trait Request {
  /** Returns the request method, e.g. GET/POST/PUT/DELETE/... */
  def method: String

  /** Returns the request [[java.net.URI]]. */
  def uri: URI

  /**
   * Returns the string value of the header,
   *  concatenates multiple values with ",",
   *  raises Exception of header is not found.
   */
  def header(key: String): String =
    headers.get(key).mkString(",")

  /** Returns a map of all headers. */
  def headers: java.util.Map[String, Array[String]]

  /**
   * Returns the string value of the querystring parameter,
   *  concatenates multiple values with ",",
   *  raises Exception of header is not found.
   */
  def param(key: String): String =
    params.get(key).mkString("[", ",", "]")

  /** Returns a map of all query string parameters. */
  def params: java.util.Map[String, Array[String]]

  /** Returns the Content-Type header or application/octetstream as default. */
  def contentType: String =
    Try(header("Content-Type")).getOrElse("application/octetstream")
  /** Returns the length of the body. */
  def contentLength: Long =
    body.length
  /** Returns the Content-Encoding header or null. */
  def contentEncoding: String =
    Try(header("Content-Encoding")).getOrElse(null)

  /** Returns the request body decoded using contentEncoding. */
  def body: String
}

object Request {
  def apply(
    uri: URI,
    method: String,
    headers: java.util.Map[String, Array[String]],
    params: java.util.Map[String, Array[String]],
    body: String): Request = GenericRequest(uri, method, headers, params, body)
}

case class GenericRequest(
  uri: URI,
  method: String,
  headers: java.util.Map[String, Array[String]],
  params: java.util.Map[String, Array[String]],
  body: String) extends Request

case class GetRequest(
    uri: URI,
    headers: java.util.Map[String, Array[String]],
    params: java.util.Map[String, Array[String]],
    body: String) extends Request {
  val method = "GET"
}

case class PostRequest(
    uri: URI,
    headers: java.util.Map[String, Array[String]],
    params: java.util.Map[String, Array[String]],
    body: String) extends Request {
  val method = "POST"
}

case class PutRequest(
    uri: URI,
    headers: java.util.Map[String, Array[String]],
    params: java.util.Map[String, Array[String]],
    body: String) extends Request {
  val method = "PUT"
}

case class DeleteRequest(
    uri: URI,
    headers: java.util.Map[String, Array[String]],
    params: java.util.Map[String, Array[String]],
    body: String) extends Request {
  val method = "DELETE"
}
