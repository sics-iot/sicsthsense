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

import scala.util.Try

import ch.ethz.inf.vs.californium.coap.DELETERequest
import ch.ethz.inf.vs.californium.coap.GETRequest
import ch.ethz.inf.vs.californium.coap.POSTRequest
import ch.ethz.inf.vs.californium.coap.PUTRequest
import ch.ethz.inf.vs.californium.coap.registries.CodeRegistry
import ch.ethz.inf.vs.californium.endpoint.resources.LocalResource
import controllers.ScalaUtils
import models.Stream
import play.core.PathPattern

class StreamsResource(subpath: Option[String] = None) extends CoapResource("api/streams") {

  protected val pathPattern = PathPattern(Seq(singleComponentPathPart("key")))

  protected lazy val params = pathPattern(subpath.getOrElse(""))
  protected lazy val keyParam = params.get("key").fold(_ => None, Some(_))

  override def getResource(path: String, last: Boolean): LocalResource =
    if (subpath.isDefined) StreamsResource.this
    else new StreamsResource(Option(path))

  override def performGET(request: GETRequest): Unit = secured(request) {
    if (keyParam.isEmpty) {
      return respond(request, CodeRegistry.RESP_BAD_REQUEST, "Key required")
    }

    val stream = Stream.getByKey(keyParam.get)

    if (stream == null) {
      return respond(request, CodeRegistry.RESP_NOT_FOUND, "Key does not exist")
    }

    val qs = ScalaUtils.parseQueryString(request.getUriQuery())

    if (qs.contains("tail")) {
      val tail = qs.get("tail").flatMap(v => Try(Integer.parseInt(v.mkString)).toOption)

      if (tail.isDefined) {
        return respond(request, stream.getDataPointsTail(tail.get))
      } else {
        return respond(request, CodeRegistry.RESP_BAD_REQUEST, "Query parameter 'tail' is maleformed.'")
      }
    } else if (qs.contains("last")) {
      val last = qs.get("last").flatMap(v => Try(Integer.parseInt(v.mkString)).toOption)

      if (last.isDefined) {
        return respond(request, stream.getDataPointsTail(last.get))
      } else {
        return respond(request, CodeRegistry.RESP_BAD_REQUEST, "Query parameter 'last' is maleformed.'")
      }
    } else if (qs.contains("since")) {
      val since = qs.get("since").flatMap(v => Try(Integer.parseInt(v.mkString)).toOption)

      if (since.isDefined) {
        return respond(request, stream.getDataPointsTail(since.get))
      } else {
        return respond(request, CodeRegistry.RESP_BAD_REQUEST, "Query parameter 'since' is maleformed.'")
      }
    }

    return respond(request, stream.getDataPointsTail(50))
  }

  override def performPOST(request: POSTRequest): Unit =
    request.respond(CodeRegistry.RESP_FORBIDDEN)

  // Don't allow PUT, it is not defined from the HTTP side
  override def performPUT(request: PUTRequest): Unit =
    request.respond(CodeRegistry.RESP_FORBIDDEN)

  // Don't allow DELETE, we have now possibility to check authorization
  override def performDELETE(request: DELETERequest): Unit =
    request.respond(CodeRegistry.RESP_FORBIDDEN)
}
