package observing.impl

import scala.concurrent.ExecutionContext.Implicits.global

import observing.Disposable
import observing.Observable
import observing.Observer
import play.api.libs.iteratee.Cont
import play.api.libs.iteratee.Done
import play.api.libs.iteratee.Enumerator
import play.api.libs.iteratee.Input
import play.api.libs.iteratee.Iteratee

class EnumeratorObservable[+A](enumerator: Enumerator[A]) extends Observable[A] {
  def subscribe(observer: Observer[A]): Disposable = {
    val cancel = Disposable.boolean()

    def fold(input: Input[A]): Iteratee[A, Unit] = input match {
      case in @ Input.El(value) =>
        if (cancel.isDisposed) {
          Done((), in)
        } else {
          observer.onNext(value)
          Cont(fold)
        }
      case in @ Input.EOF =>
        if (cancel.isDisposed) {
          Done((), in)
        } else {
          observer.onCompleted()
          Done((), in)
        }
      case Input.Empty =>
        Cont(fold)
    }

    val promise = enumerator(Cont(fold))

    promise.onFailure[Unit] {
      case exception: Exception if !cancel.dispose() => observer.onError(exception)
    }

    cancel
  }
}