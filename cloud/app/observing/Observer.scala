package observing

import controllers.Utils
import models.Resource
import play.Logger
import protocol.Response
import observing.impl.AnonymousObserver

/**
 * Represents an observer.
 * If onError is called the sequence is also completed.
 * This means that when either onError or onCompleted is called, no further methods
 * on the Observer are called.
 */
trait Observer[A] {
  /** Callback that receives the values from the sequence. */
  def onNext(value: A): Unit

  /** Callback that receives the first error on the sequence. */
  def onError(exception: Throwable): Unit

  /** Callback that is called when the sequence ends normally. */
  def onCompleted(): Unit
}

object Observer {
  /**
   * Returns a new [[observing.Observer]].
   *
   * @param onNext the callback that is called for each new element.
   * @param onError the callback that is called when a error happens.
   * @param onCompleted the callback that is called when the observable finished.
   * @return a [[observing.Observer]].
   */
  def create[A](onNext: A => Unit, onError: Throwable => Unit = t => throw t, onCompleted: () => Unit = () => {}): Observer[A] = {
    assert(onNext != null)
    assert(onError != null)
    assert(onCompleted != null)

    AnonymousObserver[A](onNext, onError, onCompleted)
  }

  /**
   * Returns a new [[observing.Observer[A]]] that ignores all messages in the sequence.
   */
  def nop[A]: Observer[A] = new Observer[A] {
    def onNext(value: A) {}
    def onError(t: Throwable) {}
    def onCompleted() {}
  }
}
