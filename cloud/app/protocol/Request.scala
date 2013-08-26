/*
 * Copyright (c) 2013, Institute for Pervasive Computing, ETH Zurich.
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

 * Authors:
 *  26/08/2013 Adrian Kündig (adkuendi@ethz.ch)
 */

package protocol

import java.net.URI
import scala.util.Try
import org.apache.http.entity.ContentType
import controllers.ScalaUtils

trait Request extends Message {
  /** Returns the request method, e.g. GET/POST/PUT/DELETE/... */
  def method: String

  /**
   * Returns the querystring parameter concatenated,
   * @param key the parameter key
   * @return the values of the querystring concatenated with ',' or "" if the key is not found
   */
  def param(key: String): String =
    headers.get(key)
      .map(_.mkString(","))
      .getOrElse(null)

  /**
   * Returns the querystring parameter converted to an integer,
   * @param key the parameter key
   * @param default the default value returned if the parameter is not found or conversion fails
   * @return the converted querystring parameter or default
   */
  def intParam(key: String, default: Int): Int =
    Option(header(key)).flatMap(v => Try(v.toInt).toOption).getOrElse(default)

  /**
   * Returns the querystring parameter converted to a long,
   * @param key the parameter key
   * @param default the default value returned if the parameter is not found or conversion fails
   * @return the converted querystring parameter or default
   */
  def longParam(key: String, default: Long): Long =
    Option(header(key)).flatMap(v => Try(v.toLong).toOption).getOrElse(default)

  /** Returns a map of all headers. */
  def params: Map[String, Array[String]]

  private lazy val ct =
    Option(header("Content-Type"))
      .map(ContentType.create)
      .getOrElse(ContentType.APPLICATION_OCTET_STREAM)

  override def contentType: String = ct.getMimeType()

  override def contentLength: Long = body.length()
}

object Request {
  def apply(uri: URI,
            method: String,
            headers: java.util.Map[String, Array[String]],
            params: java.util.Map[String, Array[String]],
            body: String): Request =
    GenericRequest(uri, method, ScalaUtils.toScalaMap(headers), ScalaUtils.toScalaMap(params), body)

  def apply(uri: URI,
            method: String,
            headers: Map[String, Array[String]],
            params: Map[String, Array[String]],
            body: String): Request =
    GenericRequest(uri, method, headers, params, body)
}

case class GenericRequest(uri: URI,
                          method: String,
                          headers: Map[String, Array[String]],
                          params: Map[String, Array[String]],
                          body: String) extends Request

case class GetRequest(uri: URI,
                      headers: Map[String, Array[String]],
                      params: Map[String, Array[String]],
                      body: String) extends Request {
  val method = "GET"
}

case class PostRequest(uri: URI,
                       headers: Map[String, Array[String]],
                       params: Map[String, Array[String]],
                       body: String) extends Request {
  val method = "POST"
}

case class PutRequest(uri: URI,
                      headers: Map[String, Array[String]],
                      params: Map[String, Array[String]],
                      body: String) extends Request {
  val method = "PUT"
}

case class DeleteRequest(uri: URI,
                         headers: Map[String, Array[String]],
                         params: Map[String, Array[String]],
                         body: String) extends Request {
  val method = "DELETE"
}
