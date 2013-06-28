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
import play.Logger

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

  private val maxAge = """max-age=(\d+)""".r

  def expires(): Long = {
    val ma = for {
      h <- Option(header("Cache-Control"))
      m <- maxAge.findFirstMatchIn(h)

      if m.subgroups.length > 0
      age <- Try(m.subgroups(0).toLong).toOption

      if age > 0 && receivedAt > 0
    } yield age + receivedAt

    return ma.getOrElse(0)
  }
}
