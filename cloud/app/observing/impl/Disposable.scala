package observing.impl

import scala.concurrent.stm.Ref
import scala.concurrent.stm.TSet
import scala.concurrent.stm.atomic

import observing.AlreadyDisposedException
import observing.Cancelable
import observing.Disposable

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

case class BooleanDisposable(initial: Boolean) extends Cancelable {
  private val disposed = Ref(initial)

  def dispose() {
    disposed.single() = true
  }

  def isDisposed = disposed.single()
}

case class CompositeDisposable(initial: Disposable*) extends Cancelable with Traversable[Disposable] {
  private val disposed = Ref(false)
  private val disposables = TSet(initial: _*)

  def foreach[U](f: Disposable => U) = disposables.single.foreach(f)

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

  def remove(value: Disposable): Boolean = {
    assert(value != null)

    val shouldDispose = disposables.single.remove(value)

    if (shouldDispose)
      value.dispose

    shouldDispose
  }

  def clear() {
    disposables.single.empty.foreach(_.dispose())
  }

  def dispose() {
    atomic { implicit tx =>
      disposed() = true
      disposables.single.empty
    }.foreach(_.dispose())
  }

  def isDisposed = disposed.single()
}

case class SerialDisposable() extends Cancelable {
  private val current = Ref(Disposable.empty)

  def disposable(): Disposable =
    current.single() match {
      case Disposable.empty    => Disposable.empty
      case Disposable.disposed => Disposable.empty
      case actual              => actual
    }

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

case class SingleAssignmentDisposable() extends Cancelable {
  private val current = Ref(Disposable.empty)

  def disposable(): Disposable =
    current.single() match {
      case Disposable.empty    => Disposable.empty
      case Disposable.disposed => Disposable.empty
      case actual              => actual
    }

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

