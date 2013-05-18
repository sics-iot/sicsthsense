package observing

import scala.collection.concurrent

import controllers.Utils
import models.Resource
import play.Logger
import play.api.libs.iteratee.Concurrent
import play.api.libs.iteratee.Concurrent.Channel
import play.api.libs.iteratee.Enumerator
import protocol.Response

trait Observer[-A] {
  def onNext(value: A): Unit
  def onError(exception: Throwable): Unit
  def onCompleted(): Unit
}

object Observer {
  def create[A](
    next: A => Unit,
    error: Throwable => Unit = e => throw e,
    completed: () => Unit = () => Unit) =
    new Observer[A]() {
      def onNext(value: A): Unit = next(value)
      def onError(exception: Throwable): Unit = error(exception)
      def onCompleted(): Unit = completed()
    }
}

class StoringObserver(resourceId: Long) extends Observer[Response] {
  def onNext(response: Response): Unit = {
    Resource.getById(resourceId).parseAndStore(response.body, response.contentType, Utils.currentTime())
  }

  def onError(exception: Throwable): Unit = {
    Logger.debug(s"An error occured while observing resource $resourceId", exception)
  }
  
  def onCompleted(): Unit = Unit
}

object ResourceObservable {
  private val observables = concurrent.TrieMap[Long, (Enumerator[Resource], Channel[Resource])]()

  def getOrCreate(resourceId: Long): Observable[Resource] = {
    val (enumerator, channel) = observables.getOrElseUpdate(resourceId, create(resourceId))
    Observable.fromEnumerator(enumerator)
  }

  private def create(id: Long): (Enumerator[Resource], Channel[Resource]) = {
    val (enumerator, channel) = Concurrent.broadcast[Resource]

    enumerator.onDoneEnumerating { remove(id) }

    (enumerator, channel)
  }

  private def remove(id: Long): Unit =
    observables.remove(id)
}
