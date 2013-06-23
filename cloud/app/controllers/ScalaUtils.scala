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
