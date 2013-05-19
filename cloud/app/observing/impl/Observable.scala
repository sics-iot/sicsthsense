package observing.impl

import scala.concurrent.Promise
import scala.concurrent.stm.Ref
import scala.concurrent.stm.atomic
import scala.util.Failure
import scala.util.Success
import scala.util.Try

import observing.Disposable
import observing.Observable
import observing.Observer

private abstract class SinkBase[A](observer: Observer[A], cancel: Disposable) extends Disposable {
  protected val observerRef: Ref[Observer[A]] = Ref(observer)
  protected val cancelRef: Ref[Option[Disposable]] = Ref(Some(cancel))

  def dispose() {
    atomic { implicit tx =>
      observerRef() = Observer.nop[A]
      cancelRef.swap(None)
    } match {
      case Some(cancel) => cancel.dispose
      case None         =>
    }
  }
}

abstract class ObservableBase[A] extends Observable[A] {
  def subscribe(observer: Observer[A]): Disposable = {
    assert(observer != null)

    val ado = AutoDetachObserver(observer)

    try {
      ado.disposable(subscribeCore(ado))
    } catch {
      case t: Throwable if ado.fail(t) =>
    }

    ado
  }

  protected def subscribeCore(observer: Observer[A]): Disposable
}

abstract class Producer[A] extends Observable[A] {
  def subscribe(observer: Observer[A]): Disposable = {
    assert(observer != null)

    val sink = SingleAssignmentDisposable()
    val subscription = SingleAssignmentDisposable()

    subscription.disposable(run(observer, subscription, s => sink.disposable(s)))

    CompositeDisposable(sink, subscription)
  }

  def run(observer: Observer[A], cancel: Disposable, setSink: Disposable => Unit): Disposable
}

case class Filter[A](source: Observable[A], pred: A => Boolean) extends Producer[A] {

  def run(observer: Observer[A], cancel: Disposable, setSink: Disposable => Unit): Disposable = {
    val sink = Sink(this, observer, cancel)
    setSink(sink)
    return source.subscribeSafe(sink)
  }

  def concat(otherPred: A => Boolean): Observable[A] =
    Filter(source, v => otherPred(v) && pred(v))

  private case class Sink(parent: Filter[A], observer: Observer[A], cancel: Disposable)
      extends SinkBase[A](observer, cancel) with Observer[A] {

    def onNext(value: A) {
      Try(pred(value)) match {
        case Success(true) =>
          observer.onNext(value)
        case Success(false) =>
        case Failure(t) =>
          observer.onError(t)
          dispose()
      }
    }

    def onError(t: Throwable) {
      observer.onError(t)
      dispose()
    }

    def onCompleted() {
      observer.onCompleted()
      dispose()
    }
  }
}

case class ToFuture[A](observable: Observable[A]) {
  private val promise = Promise[A]

  observable.subscribe(
    { value => if (!promise.isCompleted) promise.success(value) },
    { t => if (!promise.isCompleted) promise.failure(t) }
  )

  def future = promise.future
}
