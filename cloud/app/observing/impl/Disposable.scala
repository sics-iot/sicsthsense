package observing.impl

import scala.concurrent.stm.Ref
import scala.concurrent.stm.TSet
import scala.concurrent.stm.atomic

import observing.AlreadyDisposedException
import observing.Cancelable
import observing.Disposable

/**
 * Wraps an action into a Disposable. The given action is only executed
 * once at the first call to dispose().
 */
case class AnonymouseDisposable(action: () => Unit) extends Cancelable {
  private val actionRef: Ref[Option[Function0[Unit]]] = Ref(Some(action))

  def dispose() {
    actionRef.single.swap(None) match {
      case Some(a) => a()
      case None    =>
    }
  }

  def isDisposed: Boolean = actionRef.single().isEmpty
}

/**
 * Wraps a Boolean into a Disposable. The boolean is set to true at
 * the first call to dispose().
 */
case class BooleanDisposable(initial: Boolean) extends Cancelable {
  private val disposed = Ref(initial)

  def dispose() {
    disposed.single() = true
  }

  def isDisposed = disposed.single()
}

/**
 * Wraps multiple disposables. All disposables are disposed at the first
 * call to dispose().
 */
case class CompositeDisposable(initial: Disposable*) extends Cancelable with Traversable[Disposable] {
  private val disposed = Ref(false)
  private val disposables = TSet(initial: _*)

  /** Iterate over a snapshot of the currently stored disposables. */
  def foreach[U](f: Disposable => U) = disposables.single.foreach(f)

  /**
   * Add a Disposable to the internal list of disposables. If the
   * CompositeDisposable is already disposed, the given value is also disposed.
   */
  def add(value: Disposable) {
    assert(value != null)

    val shouldDispose = atomic { implicit tx =>
      if (!disposed()) {
        disposables.add(value)
      }

      disposed()
    }

    if (shouldDispose)
      value.dispose()
  }

  /**
   * Removes the given disposable to the internal list of disposables.
   * If the disposable is found, it is disposed and true is returned.
   */
  def remove(value: Disposable): Boolean = {
    assert(value != null)

    val shouldDispose = disposables.single.remove(value)

    if (shouldDispose)
      value.dispose

    shouldDispose
  }

  /** Removes and disposes all internally stored disposables. */
  def clear() {
    disposables.single.empty.foreach(_.dispose())
  }

  /** 
   * Removes and disposes all internally stored disposables and markes
   * the CompositeDisposable as disposed.
   */
  def dispose() {
    atomic { implicit tx =>
      disposed() = true
      disposables.single.empty
    }.foreach(_.dispose())
  }

  def isDisposed = disposed.single()
}

/**
 * Wraps a single Disposable, initially Disposable.empty. Whenever the internal disposable
 * is set through the setter disposable(...) the currently stored disposable is disposed.
 * If the SerialDisposable is already disposed then setting the current disposable to a
 * new value instantly disposes that new disposable.
 */
case class SerialDisposable() extends Cancelable {
  private val current = Ref(Disposable.empty)

  /** Returns the currently stored [[observing.Disposable]]. */
  def disposable(): Disposable =
    current.single() match {
      case Disposable.empty    => Disposable.empty
      case Disposable.disposed => Disposable.empty
      case actual              => actual
    }

  /** Sets the currently stored [[observing.Disposable]]. */
  def disposable(value: Disposable) {
    assert(value != null)

    atomic { implicit tx =>
      if (current() != Disposable.disposed) {
        current.swap(value)
      } else {
        Disposable.disposed
      }
    } match {
      case Disposable.empty    =>
      case Disposable.disposed =>
      case actual              => actual.dispose
    }
  }

  def dispose() {
    current.single.swap(Disposable.disposed) match {
      case Disposable.empty    =>
      case Disposable.disposed =>
      case actual              => actual.dispose
    }
  }

  def isDisposed = current.single() == Disposable.disposed
}

/**
 * Wraps a single [[observing.Disposable]] which is initially Disposable.empty.
 * One assignment to the disposable property is allowed. Subsequent assignments
 * raise an exception.
 */
case class SingleAssignmentDisposable() extends Cancelable {
  private val current = Ref(Disposable.empty)

  /** Returns the currently stored [[observing.Disposable]]. */
  def disposable(): Disposable =
    current.single() match {
      case Disposable.empty    => Disposable.empty
      case Disposable.disposed => Disposable.empty
      case actual              => actual
    }

  /** 
   * Sets the currently stored [[observing.Disposable]].
   * Throws [[observing.AlreadyDisposedException]] if called more than once.
   */
  def disposable(value: Disposable) {
    assert(value != null)

    atomic { implicit tx =>
      if (current() == Disposable.empty) {
        current() = value
      } else {
        if (value != null) value.dispose()

        throw new AlreadyDisposedException("")
      }
    }
  }

  def dispose() {
    current.single.swap(Disposable.disposed) match {
      case Disposable.empty    =>
      case Disposable.disposed =>
      case actual              => actual.dispose
    }
  }

  def isDisposed: Boolean = current.single() == Disposable.disposed
}

