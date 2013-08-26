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
import scala.concurrent.Future
import scala.concurrent.Promise
import ch.ethz.inf.vs.californium.coap
import ch.ethz.inf.vs.californium.coap.DELETERequest
import ch.ethz.inf.vs.californium.coap.GETRequest
import ch.ethz.inf.vs.californium.coap.POSTRequest
import ch.ethz.inf.vs.californium.coap.PUTRequest
import ch.ethz.inf.vs.californium.coap.ResponseHandler
import ch.ethz.inf.vs.californium.coap.registries.OptionNumberRegistry
import ch.ethz.inf.vs.californium.util.HttpTranslator
import protocol.GetRequest
import protocol.Protocol
import protocol.Request
import protocol.Response
import rx.Observable
import rx.Observer
import rx.subscriptions.Subscriptions
import rx.util.functions.Action0
import play.api.Logger

object CoapProtocol extends Protocol[coap.Message, coap.Response] {
  private val logger = Logger(this.getClass)

  private def createRequest(request: Request): coap.Request = {
    // Create the appropriate request type. Only GET, POST, PUT, and DELETE are supported
    val req = request.method match {
      case "GET" => new GETRequest()
      case "POST" => new POSTRequest()
      case "PUT" => new PUTRequest()
      case "DELETE" => new DELETERequest()
      case _ => throw new IllegalArgumentException(s"Unknown request type: ${request.method}")
    }

    // Concatenate the querystring values, if we have multiple values for the same
    // key, print them as array inside [ ].
    val qs = request.params.map {
      case ((key, values)) =>
        key + "=" + (values.length match {
          case 0 => ""
          case 1 => values(0)
          case _ => "[" + values.mkString(",") + "]"
        })
    }.mkString("&")

    // Convert the headers to option values, concatenate multiple values for the
    // same key with ,.
    val ops = HttpTranslator.getCoapOptions(request.headers.map {
      case ((key, values)) => new org.apache.http.message.BasicHeader(key, values.mkString(","))
    }.toArray)

    // Set the request uri
    req.setURI(request.uri)
    // Override the querystring
    req.setUriQuery(qs)
    // Attach the headers
    req.setOptions(ops)

    // return the created request object
    req
  }

  private def executeRequest(req: coap.Request): Future[Response] = {
    // We store the response into this promise
    val promise = Promise[Response]

    // Register an response handler that stores the response into the promise
    req.registerResponseHandler(new ResponseHandler() {
      override def handleResponse(resp: coap.Response): Unit =
        promise.success(translateResponse(resp))
    })

    // Start executing the request
    req.execute

    // Return the future of the promise
    promise.future
  }

  override def request(request: Request): Future[Response] =
    executeRequest(createRequest(request))

  def observe(uri: URI, queryString: Map[String, Array[String]]): Observable[Response] = {
    val request = GetRequest(uri, Map.empty, queryString, "")

    // Create the observing actor
    // Execute the request when the actor receives the first connect request.
    // It is possible that the server never response with an Observe
    // option and thus we can only guarantee at least one Response to all observers.
    Observable.create[Response] {
      observer: Observer[Response] =>
        try {
          // Create the coap request from a generic request object
          val req = CoapProtocol.createRequest(request)

          // Register a response handler that pushes Responses into the channel
          val responseHandler = new ResponseHandler() {
            override def handleResponse(resp: coap.Response): Unit = {
              logger.debug(s"Observe on $uri sent next notification")

              // On each notification CoAP sends a full response containing the resources new state
              val response = CoapProtocol.translateResponse(resp)

              response.headers
              response.statusText

              // Push the translated response into the channel
              observer.onNext(response)

              // If the server stopped sending notifications, end the actor
              if (!resp.hasOption(OptionNumberRegistry.OBSERVE)) {
                logger.debug(s"Observe on $uri sent next notification but did not contain any Observe Header")
                observer.onCompleted()
              }
            }
          }

          req.registerResponseHandler(responseHandler)
          req.setObserve()
          req.execute()

          Subscriptions.create(new Action0 {
            def call() {
              val req = CoapProtocol.createRequest(request)
              req.removeOptions(OptionNumberRegistry.OBSERVE)
              req.execute()

              logger.debug(s"Subscription disposed. Stopped observing $uri")
            }
          })
        } catch {
          case e: Exception =>
            observer.onError(e)
            Subscriptions.empty()
        }
    }
  }

  def translateRequest(request: coap.Message): Request = new CoapRequest(request)

  def translateResponse(response: coap.Response): Response = new CoapResponse(response)
}
