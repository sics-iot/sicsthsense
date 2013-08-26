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

import java.net.URL
import java.net.URLConnection
import scala.collection.JavaConversions.mapAsScalaMap
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import play.api.libs.ws.WS
import protocol.{GetRequest, Protocol, Request, Response}
import java.net.URI
import rx.Observable
import java.util.Collections
import controllers.ScalaUtils

object HttpProtocol extends Protocol[play.mvc.Http.Request, play.api.libs.ws.Response] {
  def createUrl(url: String): URL = ???
  def createConnection(url: String): URLConnection = ???

  def request(request: Request): Future[Response] = {

    val hs = request.headers.mapValues(_.mkString(",")).toSeq
    val qs = request.params.mapValues {
      values =>
        values.length match {
          case 0 => ""
          case 1 => values(0)
          case _ => "[" + values.mkString(",") + "]"
        }
    }.toSeq

    val req = WS.url(request.uri.toString()).withQueryString(qs: _*).withHeaders(hs: _*)

    val promise = request.method match {
      case "GET"    => req.get()
      case "POST"   => req.post(request.body)
      case "PUT"    => req.put(request.body)
      case "DELETE" => req.delete()
      case _        => throw new Exception("Unrecognized request method")
    }

    promise.map(translateResponse(_, request))
  }

  def observe(uri: URI, params: Map[String, Array[String]]): Observable[Response] =
    HttpObserver.observe(GetRequest(uri, Map.empty, params, ""))

  def translateRequest(request: play.mvc.Http.Request): Request = new HttpRequest(request)

  def translateResponse(response: play.api.libs.ws.Response): Response = new HttpResponse(response)

  def translateResponse(response: play.api.libs.ws.Response, request: Request): Response = new HttpResponse(response, Some(request))
}
