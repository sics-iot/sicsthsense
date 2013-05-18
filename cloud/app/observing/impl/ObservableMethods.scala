package observing.impl

import scala.concurrent.Promise
import observing.Observable
import observing.Observer
import scala.concurrent.Future

class ToFuture[A](observable: Observable[A]) {
  private val promise = Promise[A]

  observable.subscribe(
    Observer.create(
      { value => if (!promise.isCompleted) promise.success(value) },
      { t => if (!promise.isCompleted) promise.failure(t) }
    )
  )

  def future = promise.future
}
