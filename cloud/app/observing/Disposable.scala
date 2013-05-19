package observing

import observing.impl.AnonymouseDisposable
import observing.impl.BooleanDisposable

trait Disposable {
  def dispose(): Unit
}

trait Cancelable extends Disposable {
  def isDisposed: Boolean
}

object Disposable {
  val empty: Disposable = new Disposable { def dispose() {} }

  def boolean(initial: Boolean = false) = new BooleanDisposable(initial)

  private[observing] val disposed: Cancelable = new BooleanDisposable(true)

  def create(action: () => Unit): Cancelable = new AnonymouseDisposable(action)
}

case class AlreadyDisposedException(message: String) extends Exception(message)
