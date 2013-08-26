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

package protocol.http

import protocol.Response
import play.api.libs.ws
import java.net.URI
import org.apache.http.entity.ContentType
import protocol.Request
import controllers.Utils
import scala.collection.JavaConversions.asScalaSet
import scala.util.Try
import scala.concurrent.duration._

class HttpResponse(response: ws.Response, req: Option[Request] = None) extends Response {
  override def request: Request = req.get

  override def uri: URI = response.getAHCResponse.getUri()

  override def status: Int = response.status

  override def statusText: String = response.statusText

  override def headers: Map[String, Array[String]] =
    response.ahcResponse.getHeaders.entrySet()
      .map { entry =>
      (entry.getKey, entry.getValue.toArray(Array.empty[String]))
    }.toMap[String, Array[String]]

  private lazy val ct =
    Option(response.getAHCResponse.getContentType())
      .map(ContentType.create)
      .getOrElse(ContentType.APPLICATION_OCTET_STREAM)

  override def contentType: String = ct.getMimeType()

  override def contentLength: Long = body.length()

  def date: Long = longHeader("Date", receivedAt)

  override val receivedAtAsDuration: FiniteDuration = Utils.currentTimeAsDuration

  private val maxAgeR = """max-age=(\d+)""".r

  override def expires: Long = {
    val ma = for {
      hs <- headers.get("Cache-Control")
      m <- maxAgeR.findFirstMatchIn(hs.mkString(","))

      if m.subgroups.length > 0
      maxAge <- Try(m.subgroups(0).toLong).toOption

      if receivedAt > 0
    } yield maxAge.seconds + receivedAtAsDuration

    ma.map(_.toSeconds).getOrElse(longHeader("Expires", 0))
  }

  override def body: String = response.body
}
