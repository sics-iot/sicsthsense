/*
 * Copyright (c) 2013, Swedish Institute of Computer Science
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of The Swedish Institute of Computer Science nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE SWEDISH INSTITUTE OF COMPUTER SCIENCE BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

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
