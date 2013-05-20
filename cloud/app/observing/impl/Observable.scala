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

/**
 * Represents a Sink that wraps an [[observing.Observer]]. Many of the operators
 * defined on [[observing.Observable]] internally extend SinkBase and implement
 * [[observing.Observer]] and then subscribe the Sink to the original Observable.
 *
 * So for example the operator Observable.filter(predicate) actually creates a [[observing.impl.Filter]] that
 * wraps the current Observable. [[observing.impl.Filter]] extends itself [[observing.Observable]].
 * Internally it defines a class FilterSink extending SinkBase.
 *
 * Every new subscriber to [[observing.impl.Filter]] is wrapped inside FilterSink and then subscribed to
 * the source Observable. This way one could see the Sinks as logic implementation of the respective
 * operators.
 */
private[impl] abstract class SinkBase[A](observer: Observer[A], cancel: Disposable) extends Disposable {
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

/**
 * The Observable base class that redirects errors happening on subscribe
 * to the observer.
 */
private[observing] abstract class ObservableBase[A] extends Observable[A] {
  final def subscribe(observer: Observer[A]): Disposable = {
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

/**
 * The Producer base class that most operators extends from. It
 * provides the subscribe method that is required when implementing
 * the operator logic inside a Sink.
 */
private[observing] abstract class Producer[A] extends Observable[A] {
  final def subscribe(observer: Observer[A]): Disposable = {
    assert(observer != null)

    val sink = SingleAssignmentDisposable()
    val subscription = SingleAssignmentDisposable()

    subscription.disposable(run(observer, subscription, s => sink.disposable(s)))

    CompositeDisposable(sink, subscription)
  }

  protected def run(observer: Observer[A], cancel: Disposable, setSink: Disposable => Unit): Disposable
}

/**
 * Represents a filtered [[observing.Observable]].
 *
 * @constructor creates a filtered [[observing.Observable]].
 * @param source the source [[observing.Observable]].
 * @param pred the predicate that should return true for all elements that should be passed on.
 *
 */
private[observing] case class Filter[A](source: Observable[A], pred: A => Boolean) extends Producer[A] {

  protected def run(observer: Observer[A], cancel: Disposable, setSink: Disposable => Unit): Disposable = {
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

/**
 * Represents the first element in an observable sequence.
 */
private[observing] case class ToFuture[A](observable: Observable[A]) {
  private val promise = Promise[A]
  private val disposable = SingleAssignmentDisposable()

  private val observer = Observer.create[A]({ value =>
    if (!promise.isCompleted) {
      promise.success(value)
      disposable.dispose()
    }
  }, { t =>
    if (!promise.isCompleted) {
      promise.failure(t)
      disposable.dispose()
    }
  }, { () =>
    if (!promise.isCompleted) {
      promise.failure(new Exception("No elements in sequence"))
      disposable.dispose()
    }
  })

  disposable.disposable(observable.subscribe(observer))

  /** Returns the future that eventually holds the first value in the sequence. */
  def future = promise.future
}
