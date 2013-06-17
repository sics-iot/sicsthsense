/**
 *
 */
package protocol.coap

import java.net.URI
import java.util.Date
import scala.collection.JavaConversions.mapAsScalaMap
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.Promise
import scala.concurrent.stm.TMap
import scala.concurrent.stm.Txn
import scala.concurrent.stm.atomic
import scala.util.Failure
import scala.util.Success
import scala.util.Try
import ch.ethz.inf.vs.californium.coap
import ch.ethz.inf.vs.californium.coap.DELETERequest
import ch.ethz.inf.vs.californium.coap.GETRequest
import ch.ethz.inf.vs.californium.coap.POSTRequest
import ch.ethz.inf.vs.californium.coap.PUTRequest
import ch.ethz.inf.vs.californium.coap.ResponseHandler
import ch.ethz.inf.vs.californium.coap.registries.OptionNumberRegistry
import ch.ethz.inf.vs.californium.util.HttpTranslator
import controllers.ScalaUtils
import protocol.GetRequest
import protocol.Protocol
import protocol.Request
import protocol.Response
import rx.Observable
import rx.Observer
import rx.subscriptions.Subscriptions
import rx.util.functions.Action0
import java.util.Collections

object CoapProtocol extends Protocol[coap.Message, coap.Response] {

  private[coap] def createRequest(request: Request): coap.Request = {
    // Create the appropriate request type. Only GET, POST, PUT, and DELETE are supported
    val req = request.method match {
      case "GET"    => new GETRequest()
      case "POST"   => new POSTRequest()
      case "PUT"    => new PUTRequest()
      case "DELETE" => new DELETERequest()
      case _        => throw new IllegalArgumentException(s"Unknown request type: ${request.method}")
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

  /**
   * Returns either the cached response, a reference to a currently running request
   * or a reference to a new request that is started.
   */
  private def get(request: Request): Future[Response] =
    RequestStore.getOrCreateRequest(request)

  def request(request: Request): Future[Response] = request.method match {
    // Because the semantics of GET in CoAP force a concurrently running Observer
    // to cancel as soon as we make a GET request we need to handle GET request
    // as special cases.
    case "GET" => get(request)
    // All other cases just simply execute the request.
    case _     => executeRequest(createRequest(request))
  }

  def observe(uri: URI, queryString: java.util.Map[String, Array[String]]): Observable[Response] =
    RequestStore.getOrCreateObserve(GetRequest(uri, Collections.emptyMap[String, Array[String]], queryString, ""))

  def translateRequest(request: coap.Message): Request = new CoapRequest(request)

  def translateResponse(response: coap.Response): Response = new CoapResponse(response)
}

private object RequestStore {
  import scala.concurrent.stm.atomic

  val responses = TMap.empty[String, (Long, Response)]
  val connections = TMap.empty[String, Either[Future[Response], Observable[Response]]]

  private def createRequest(request: Request): (Future[Response], coap.Request) = {
    // We need to prepare the actual request object. 
    // We make it lazy so that we only pay the price if we need to create a request.
    val actualRequest = CoapProtocol.createRequest(request)

    // We store the response into this promise
    val promise = Promise[Response]

    // Register an response handler that stores the response into the promise
    actualRequest.registerResponseHandler(new ResponseHandler() {
      override def handleResponse(resp: coap.Response): Unit =
        promise.success(CoapProtocol.translateResponse(resp))
    })

    (promise.future, actualRequest)
  }

  private def createObserve(request: Request): Observable[Response] = {
    assert(request.method == "GET")

    // Create the observing actor
    // Execute the request when the actor receives the first connect request.
    // It is possible that the server never response with an Observe
    // option and thus we can only guarantee at least one Response to all observers.
    Observable.create[Response] { observer: Observer[Response] =>
      // Create the coap request from a generic request object
      val req = CoapProtocol.createRequest(request)

      // Register a response handler that pushes Responses into the channel
      val responseHandler = new ResponseHandler() {
        override def handleResponse(resp: coap.Response): Unit = {
          // On each notification CoAP sends a full response containing the resources new state
          val response = CoapProtocol.translateResponse(resp)

          // Push the translated response into the channel
          observer.onNext(response)

          // If the server stopped sending notifications, end the actor
          if (!response.headers.containsKey("Observe")) {
            observer.onCompleted()
          }
        }
      }

      req.registerResponseHandler(responseHandler)
      req.setObserve()
      req.execute()

      Subscriptions.create {
        val req = CoapProtocol.createRequest(request)
        req.removeOptions(OptionNumberRegistry.OBSERVE)
        req.execute()
        req.getResponse()
      }
    }
  }

  /**
   * Get the Response cached for the give request. If it is expired the Response
   * is removed from the cache and None is returned.
   */
  private def getCached(request: Request): Option[Response] = {
    val key = s"COAP.GET.${id(request)}"
    val now = new Date().getTime() / 1000

    responses.single.get(key).filter(_._1 >= now).map(_._2)
  }

  /**
   * Set the Response that is cached for a specific request. The time
   * it is expired is new Date().getTime() + Max-Age Header or
   * 60 seconds as written in the CoAP specification.
   */
  private def setCached(request: Request, response: Response): Unit = {
    val key = s"COAP.GET.${id(request)}"
    val now = (new Date().getTime() / 1000).toInt
    val expires = Try(response.header("Max-Age").toInt).getOrElse(60)

    responses.single.put(key, (expires, response))
  }

  /**
   * Return the String that uniquely identifies the given request.
   *
   * Currently it is just the complete uri without the querystring.
   */
  def id(request: Request): String =
    request.uri.getHost() + request.uri.getPath()

  /**
   * Returns the future that is remembered for the given id. If no simple GET request
   * or Observe request is running then a new request is executed and remembered.
   */
  def getOrCreateRequest(request: Request): Future[Response] = {
    // We need to prepare the actual request object. 
    // We make it lazy so that we only pay the price if we need to create a request.
    lazy val (actualFuture, actualRequest) = createRequest(request)

    // Check the cache and the currently open connections or create a new request atomically
    atomic { implicit tx =>
      // Load the value from the cache
      val cached = getCached(request)
      // Prepare the load of the open connection for a more fluent syntax later
      lazy val inFlight = connections.get(id(request))

      if (cached.isDefined) {
        // If we found a value in cache, return it. The cache only returns non-expired responses.
        Future(cached.get)

      } else if (inFlight.isDefined) {
        // If there is a currently open connection, make a future out of it
        inFlight.get match {
          // If the currently open connection is also just a simple get request, just use it
          case Left(f) =>
            f
          // If the currently open connection is an Observable, convert it into a Future by taking its next value.
          case Right(obs) =>
            ScalaUtils.observableToFuture(obs)
        }

      } else {
        // Try to store the future of the response inside the map of open connections
        connections.put(id(request), Left(actualFuture))

        // Attach a continuation function to the future that removes the future from the
        // connections as soon as we got a response. It should also store a successful 
        // response into the cache
        actualFuture.andThen {
          case Success(response) =>
            setCached(request, response)
            removeSafe(id(request), actualFuture)
          case Failure(t) =>
            removeSafe(id(request), actualFuture)
        }

        // Register the request execution to run after a successful commit
        Txn.afterCommit { _ =>
          actualRequest.execute()
        }

        // Return the newly created future
        actualFuture
      }
    }
  }

  /**
   * Returns the Observable[Response] that currently is responsible for observing the given id. If
   * a simple GET request is running concurrently, the response of the GET request is ignored
   * and a new Observe request is started by evaluating the given observable.
   * The observable is then remembered.
   */
  def getOrCreateObserve(request: Request): Observable[Response] =
    // Check the cache and the currently open connections or create a new request atomically
    atomic { implicit tx =>
      def createNew() = {
        var observable: Observable[Response] = null

        observable = createObserve(request).map[Response] { response: Response =>
          setCached(request, response)
          response
        }.finallyDo(new Action0 {
          def call() { removeSafe(id(request), observable) }
        })

        connections.put(id(request), Right(observable))

        observable
      }

      // If there is a currently open connection that is an Observable then use it.
      // Otherwise overwrite existing Futures
      connections.get(id(request)) match {
        // If the currently open connection is an Observable, use it
        // Return false to signal that we did not create a new request
        case Some(Right(obs)) =>
          obs
        // If the currently open connection is a Future, overwrite it
        // Return true to signal that we did create a new request
        case Some(Left(f)) =>
          createNew()
        // If there is no currently open connection, store the lazy value
        // Return true to signal that we did create a new request
        case None =>
          createNew()
      }
    }

  /**
   * Removes what ever is remembered for the given id.
   */
  def remove(id: String): Unit =
    connections.single -= id

  def removeSafe(id: String, future: Future[Response]): Unit =
    atomic { implicit tx =>
      connections.get(id) match {
        case Some(Left(f)) if f == future =>
          connections -= id
        case _ =>
      }
    }

  def removeSafe(id: String, observable: Observable[Response]): Unit =
    atomic { implicit tx =>
      connections.get(id) match {
        case Some(Right(obs)) if obs == observable =>
          connections -= id
        case _ =>
      }
    }

  /**
   * Removes everything that is remembered.
   */
  def clear(): Unit =
    connections.single.empty
}
