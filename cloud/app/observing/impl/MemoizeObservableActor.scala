package observing.impl

import scala.util.Try
import akka.actor.Actor
import observing.Observer
import akka.actor.Props

sealed trait ObserveMessage
case class Connect[A](observer: Observer[A]) extends ObserveMessage
case class Disconnect[A](observer: Observer[A]) extends ObserveMessage
case class Next[A](value: A) extends ObserveMessage
case class Error(exception: Throwable) extends ObserveMessage
case object Complete extends ObserveMessage

class MemoizeObserver[A] extends Actor {
  var observers = Set.empty[Observer[A]]

  var isDone = false
  var lastValue: Option[A] = None
  var error: Option[Throwable] = None

  def receive = {
    case connect: Connect[A] =>
      val observer = connect.observer
      observers += observer

      lastValue match {
        case Some(value) => Try(observer.onNext(value))
        case None        =>
      }
      error match {
        case Some(exception) => Try(observer.onError(exception))
        case None            => if (isDone) Try(observer.onCompleted)
      }
    case disconnect: Disconnect[A] =>
      observers -= disconnect.observer
    case input: Next[A] =>
      val value = input.value

      for (observer <- observers) yield Try(observer.onNext(value))

      lastValue = Some(value)
    case Error(exception) =>
      for (observer <- observers) yield Try(observer.onError(exception))

      observers = Set.empty[Observer[A]]
      lastValue = None
      error = Some(exception)

    case Complete =>
      for (observer <- observers) yield Try(observer.onCompleted)

      observers = Set.empty[Observer[A]]
      isDone = true
  }
}
