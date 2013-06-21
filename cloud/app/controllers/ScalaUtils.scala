package controllers

import play.core.parsers.FormUrlEncodedParser
import rx.Observable
import scala.concurrent.Future
import scala.concurrent.Promise
import rx.Observer

object ScalaUtils {
  def parseQueryString(queryString: String): java.util.Map[String, Array[String]] = {
    val map = new java.util.HashMap[String, Array[String]]

    if (queryString == null || queryString == "")
      return map;

    val qs = queryString.split('?').last

    for ((key, values) <- FormUrlEncodedParser.parse(qs)) {
      map.put(key, values.toArray)
    }

    map
  }

  def observableToFuture[A](observable: Observable[A]): Future[A] = {
    val promise = Promise[A]

    observable.take(1).subscribe(new Observer[A] {
      def onNext(value: A) {
        promise.success(value)
      }

      def onError(e: Exception) {
        promise.failure(e)
      }

      def onCompleted() {
        if (!promise.isCompleted)
          promise.failure(new Exception("No elements in Observable"))
      }
    })

    promise.future
  }
}