package observing.impl

import akka.actor.ActorRef
import akka.actor.actorRef2Scala
import observing.Disposable
import observing.Observable
import observing.Observer

class ActorObservable[A](actor: ActorRef) extends Observable[A] {
  def subscribe(observer: Observer[A]): Disposable = {
    actor ! Connect(observer)
    
    Disposable.create {
      actor ! Disconnect(observer)
    }
  }
}
