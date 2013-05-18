package observing

import java.util.concurrent.atomic.AtomicBoolean

trait Disposable {
  def dispose(): Boolean
  def isDisposed: Boolean = disposed.get

  protected val disposed = new AtomicBoolean
}

object Disposable {
  def create(action: => Unit): Disposable = new Disposable {
    def dispose(): Boolean =
      if (!disposed.compareAndSet(false, true)) {
        action
        false
      } else {
        true
      }
  }

  def boolean(): Disposable = new Disposable {
    def dispose(): Boolean = disposed.getAndSet(true)
  }
}
