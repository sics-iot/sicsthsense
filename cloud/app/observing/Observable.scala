package observing

import scala.concurrent.Future

import akka.actor.ActorRef
import observing.impl.ActorObservable
import observing.impl.EnumeratorObservable
import play.api.libs.iteratee.Enumerator

trait Observable[+A] {
  def subscribe(observer: Observer[A]): Disposable

  def head: Future[A] = new impl.ToFuture(this).future
}

object Observable {
  def fromEnumerator[A](en: Enumerator[A]): Observable[A] =
    new EnumeratorObservable(en)

  def fromActor[A](actor: ActorRef): Observable[A] =
    new ActorObservable(actor)
}
