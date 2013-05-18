package protocol

import java.net.URI

trait Response {
  /** Returns the request method, e.g. GET/POST/PUT/DELETE/... */
  def uri: URI
  
  /** Returns the [[protocol.Request]] corresponding to this response. */
  def request: Request
  
  /** Returns the Http status code. */
  def status: Int
  /** Returns the Http status text. */
  def statusText: String
  
  /** Returns the string value of the header,
   *  concatenates multiple values with ",",
   *  raises Exception of header is not found.
   */
  def header(key: String): String =
    headers.get(key).mkString(",")

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
}