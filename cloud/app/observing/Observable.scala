package observing

import scala.concurrent.Future
import akka.actor.ActorRef
import play.api.libs.iteratee.Enumerator
import observing.impl.ObservableBase
import observing.impl.AnonymousObserver
import observing.impl.Producer
import scala.util.Try
import scala.util.Success
import scala.util.Failure
import observing.impl.Filter

trait Observable[A] {
  def subscribe(observer: Observer[A]): Disposable

  def subscribe(onNext: A => Unit): Disposable = {
    assert(onNext != null)

    this.subscribe(AnonymousObserver[A](onNext))
  }

  def subscribe(onNext: A => Unit, onError: Throwable => Unit): Disposable = {
    assert(onNext != null)
    assert(onError != null)

    this.subscribe(AnonymousObserver[A](onNext, onError))
  }

  def subscribe(onNext: A => Unit, onCompleted: () => Unit): Disposable = {
    assert(onNext != null)
    assert(onCompleted != null)

    this.subscribe(AnonymousObserver[A](onNext, completed = onCompleted))
  }

  def subscribe(onNext: A => Unit, onError: Throwable => Unit, onCompleted: () => Unit): Disposable = {
    assert(onNext != null)
    assert(onError != null)
    assert(onCompleted != null)

    this.subscribe(AnonymousObserver[A](onNext, onError, onCompleted))
  }

  def subscribeSafe(observer: Observer[A]): Disposable = {
    assert(observer != null)

    if (this.isInstanceOf[ObservableBase[A]])
      return this.subscribe(observer)

    Try(this.subscribe(observer)) match {
      case Success(d) =>
        d
      case Failure(t) =>
        observer.onError(t)
        Disposable.empty
    }
  }

  def head: Future[A] = new impl.ToFuture(this).future

  def filter(pred: A => Boolean): Observable[A] =
    if (this.isInstanceOf[Filter[A]])
      this.asInstanceOf[Filter[A]].concat(pred)
    else
      Filter(this, pred)
}

object Observable {

}
