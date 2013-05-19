package observing.impl

import scala.concurrent.stm.Ref
import scala.concurrent.stm.atomic

import observing.AlreadyDisposedException
import observing.Disposable
import observing.Observable
import observing.Observer

class Subject[A] extends Observable[A] with Observer[A] with Disposable {
  private val error = Ref(None: Option[Throwable])
  private val observers = Ref(Set.empty[Observer[A]])

  private val disposed = Ref(false)
  private val isStopped = Ref(false)

  def subscribe(observer: Observer[A]): Disposable =
    atomic { implicit tx =>
      checkDisposed()

      if (isStopped()) {
        (true, error())
      } else {
        observers() = observers() + observer
        (false, error())
      }
    } match {
      case (true, Some(t)) =>
        observer.onError(t)
        Disposable.empty
      case (true, None) =>
        observer.onCompleted()
        Disposable.empty
      case (false, _) =>
        Disposable.create(() => observers.single.transform(_ - observer))
    }

  def onNext(v: A): Unit =
    atomic { implicit tx =>
      checkDisposed()

      if (isStopped()) {
        Set.empty[Observer[A]]
      } else {
        observers()
      }
    }.foreach(o => o.onNext(v))

  def onError(t: Throwable): Unit =
    atomic { implicit tx =>
      checkDisposed()

      if (isStopped()) {
        Set.empty[Observer[A]]
      } else {
        error() = Some(t)
        isStopped() = true
        observers.swap(Set.empty[Observer[A]])
      }
    }.foreach(o => o.onError(t))

  def onCompleted(): Unit =
    atomic { implicit tx =>
      checkDisposed()

      if (isStopped()) {
        Set.empty[Observer[A]]
      } else {
        isStopped() = true
        observers.swap(Set.empty[Observer[A]])
      }
    }.foreach(o => o.onCompleted())

  def dispose() {
    atomic { implicit tx =>
      observers() = Set.empty[Observer[A]]
      error() = None
      disposed() = true
    }
  }

  private def checkDisposed() {
    if (disposed.single())
      throw new AlreadyDisposedException("")
  }
}

class MemoSubject[A] extends Observer[A] with Observable[A] with Disposable {
  private val value = Ref(None: Option[A])
  private val error = Ref(None: Option[Throwable])
  private val observers = Ref(Set.empty[Observer[A]])

  private val disposed = Ref(false)
  private val isStopped = Ref(false)

  def subscribe(observer: Observer[A]): Disposable =
    atomic { implicit tx =>
      checkDisposed()

      if (isStopped()) {
        (true, value(), error())
      } else {
        observers() = observers() + observer
        (false, value(), error())
      }
    } match {
      case (true, _, Some(t)) =>
        observer.onError(t)
        Disposable.empty
      case (true, _, None) =>
        observer.onCompleted()
        Disposable.empty
      case (false, valueOption, _) =>
        valueOption.foreach(observer.onNext)
        Disposable.create(() => observers.single.transform(_ - observer))
    }

  def onNext(v: A): Unit =
    atomic { implicit tx =>
      checkDisposed()

      if (isStopped()) {
        Set.empty[Observer[A]]
      } else {
        value() = Some(v)
        observers()
      }
    }.foreach(o => o.onNext(v))

  def onError(t: Throwable): Unit =
    atomic { implicit tx =>
      checkDisposed()

      if (isStopped()) {
        Set.empty[Observer[A]]
      } else {
        error() = Some(t)
        isStopped() = true
        observers.swap(Set.empty[Observer[A]])
      }
    }.foreach(o => o.onError(t))

  def onCompleted(): Unit =
    atomic { implicit tx =>
      checkDisposed()

      if (isStopped()) {
        Set.empty[Observer[A]]
      } else {
        isStopped() = true
        observers.swap(Set.empty[Observer[A]])
      }
    }.foreach(o => o.onCompleted())

  def dispose() {
    atomic { implicit tx =>
      observers() = Set.empty[Observer[A]]
      value() = None
      error() = None
      disposed() = true
    }
  }

  private def checkDisposed() {
    if (disposed.single())
      throw new AlreadyDisposedException("")
  }
}
