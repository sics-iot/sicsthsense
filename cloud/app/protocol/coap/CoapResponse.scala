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

package protocol.coap

import java.net.URI
import ch.ethz.inf.vs.californium.coap
import protocol.Response
import protocol.Request
import ch.ethz.inf.vs.californium.coap.registries.OptionNumberRegistry
import scala.concurrent.duration._
import java.util.concurrent.TimeUnit

class CoapResponse(response: coap.Response) extends Response {
  // Response Uri
  override def uri: URI =
    new URI("coap", response.getUriHost(), response.getUriPath(), response.getUriQuery(), null)

  // Request
  override def request: Request = new CoapRequest(response.getRequest())

  // Status code and text
  override def status: Int = CoapTranslator.getHttpStatusCode(response.getCode()).get

  override def statusText: String = CoapTranslator.getHttpStatusText(response.getCode()).get

  // Headers
  override def headers: Map[String, Array[String]] =
    CoapTranslator.getHttpHeaders(response.getOptions)

  // Content Type
  private lazy val ct = CoapTranslator.getContentType(response)

  override def contentType: String =
    if (ct == null) ""
    else ct.getMimeType()

  override def contentLength: Long = body.length

  override def receivedAtAsDuration: FiniteDuration = FiniteDuration(response.getTimestamp, TimeUnit.NANOSECONDS)

  override def expires: Long =
    Option(response.getFirstOption(OptionNumberRegistry.MAX_AGE))
      .map { v =>
      val maxAge = v.getIntValue.toLong.seconds
      maxAge + receivedAtAsDuration
    }
      .getOrElse(receivedAtAsDuration).toSeconds

  // Body
  override def body: String = CoapTranslator.getContent(response)

}
