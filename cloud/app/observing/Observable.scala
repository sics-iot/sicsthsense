package observing

import scala.concurrent.Future
import scala.util.Failure
import scala.util.Success
import scala.util.Try

import observing.impl.AnonymousObserver
import observing.impl.Filter
import observing.impl.ObservableBase

/**
 * Represents an observable object. The only method required from
 * implementations is {{{subscribe(observer: Observer[A]): Disposable}}}.
 */
trait Observable[A] {
  /**
   * Returns a Disposable that unsubscribes the observer on dispose().
   *
   * @param observer the observer that wants to subscribe to this Observable.
   * @return a [[observing.Disposable]] that cancels the subscription.
   */
  def subscribe(observer: Observer[A]): Disposable

  /**
   * Convenience method to subscribe directly with lambda functions.
   *
   * @param onNext the callback that is called for each new element.
   * @return a [[observing.Disposable]] that cancels the subscription.
   */
  def subscribe(onNext: A => Unit): Disposable = {
    assert(onNext != null)

    this.subscribe(AnonymousObserver[A](onNext))
  }

  /**
   * Convenience method to subscribe directly with lambda functions.
   *
   * @param onNext the callback that is called for each new element.
   * @param onError the callback that is called when a error happens.
   * @return a [[observing.Disposable]] that cancels the subscription.
   */
  def subscribe(onNext: A => Unit, onError: Throwable => Unit): Disposable = {
    assert(onNext != null)
    assert(onError != null)

    this.subscribe(AnonymousObserver[A](onNext, onError))
  }

  /**
   * Convenience method to subscribe directly with lambda functions.
   *
   * @param onNext the callback that is called for each new element.
   * @param onCompleted the callback that is called when the observable finished.
   * @return a [[observing.Disposable]] that cancels the subscription.
   */
  def subscribe(onNext: A => Unit, onCompleted: () => Unit): Disposable = {
    assert(onNext != null)
    assert(onCompleted != null)

    this.subscribe(AnonymousObserver[A](onNext, completed = onCompleted))
  }

  /**
   * Convenience method to subscribe directly with lambda functions.
   *
   * @param onNext the callback that is called for each new element.
   * @param onError the callback that is called when a error happens.
   * @param onCompleted the callback that is called when the observable finished.
   * @return a [[observing.Disposable]] that cancels the subscription.
   */
  def subscribe(onNext: A => Unit, onError: Throwable => Unit, onCompleted: () => Unit): Disposable = {
    assert(onNext != null)
    assert(onError != null)
    assert(onCompleted != null)

    this.subscribe(AnonymousObserver[A](onNext, onError, onCompleted))
  }

  /**
   * Subscribe method that is mainly used internally. It redirects errors that happen on
   * subscribe into the observer.
   *
   * @param observer the observer that wants to subscribe to this Observable.
   * @return a [[observing.Disposable]] that cancels the subscription.
   */
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

  /**
   * Returns a [[scala.concurrent.Future[A]]] that eventually holds the first value
   * of the sequence.
   */
  def head: Future[A] = new impl.ToFuture(this).future

  /** Returns a filtered sequence. */
  def filter(pred: A => Boolean): Observable[A] =
    if (this.isInstanceOf[Filter[A]])
      this.asInstanceOf[Filter[A]].concat(pred)
    else
      Filter(this, pred)
}
