package observing.impl

import observing.Observer
import observing.Disposable
import scala.concurrent.stm.Ref
import scala.util.Try

abstract class ObserverBase[A] extends Observer[A] with Disposable {

  protected val isStopped = Ref(false)

  def onNext(value: A): Unit =
    if (!isStopped.single())
      onNextCore(value)

  protected def onNextCore(value: A): Unit

  def onError(t: Throwable): Unit =
    if (!isStopped.single.compareAndSet(false, true))
      onErrorCore(t)

  protected def onErrorCore(t: Throwable): Unit

  def onCompleted(): Unit =
    if (!isStopped.single.compareAndSet(false, true))
      onCompletedCore()

  protected def onCompletedCore(): Unit

  def dispose() {
    isStopped.single() = true
  }

  private[observing] def fail(t: Throwable): Boolean = {
    val stopped = isStopped.single.swap(true)

    if (!stopped)
      onErrorCore(t)

    !stopped
  }
}

case class AnonymousObserver[A](
    next: A => Unit,
    error: Throwable => Unit = t => throw t,
    completed: () => Unit = () => Unit) extends ObserverBase[A] {

  protected def onNextCore(value: A) {
    next(value)
  }

  protected def onErrorCore(t: Throwable) {
    error(t)
  }

  protected def onCompletedCore() {
    completed()
  }
}

case class AutoDetachObserver[A](observer: Observer[A]) extends ObserverBase[A] {
  private val m = SingleAssignmentDisposable()

  def disposable(value: Disposable) {
    m.disposable(value)
  }

  protected def onNextCore(value: A) {
    Try(observer.onNext(value)).recover {
      case t: Throwable => dispose()
    }
  }

  protected def onErrorCore(t: Throwable) {
    Try(observer.onError(t))
    dispose()
  }

  protected def onCompletedCore() {
    Try(observer.onCompleted())
    dispose()
  }

  override def dispose() {
    super.dispose()
    m.dispose()
  }
}
