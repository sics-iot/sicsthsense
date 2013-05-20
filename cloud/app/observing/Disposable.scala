package observing

import observing.impl.AnonymouseDisposable
import observing.impl.BooleanDisposable

/**
 * Represents a disposable object. The implementation of dispose() should only
 * be executed once. This means that the user does not need to know of the object
 * is already disposed but it can simply call dispose() if he wants to dispose it.
 */
trait Disposable {
  /**
   * Disposes the object. The effect of disposing should only happen once and subsequent
   * calls to this function should be NOPs.
   */
  def dispose(): Unit
}

/**
 * Extends Disposable with a flag that returns if the object is already disposed.
 */
trait Cancelable extends Disposable {
  /** Returns if the object is already disposed. */
  def isDisposed: Boolean
}

object Disposable {
  /** Returns a static Disposable that does nothing on dispose(). */
  val empty: Disposable = new Disposable { def dispose() {} }

  /** Returns a Disposable that has a Boolean flag inside that is switches to true on dispose(). */
  def boolean(initial: Boolean = false): Cancelable = new BooleanDisposable(initial)

  /** Returns a Disposable that executes {{action}} exactly once at the first call to dispose(). */
  def create(action: () => Unit): Cancelable = new AnonymouseDisposable(action)

  /** Returns a static Cancelable that is already disposed. */
  private[observing] val disposed: Cancelable = new BooleanDisposable(true)
}

case class AlreadyDisposedException(message: String) extends Exception(message)
