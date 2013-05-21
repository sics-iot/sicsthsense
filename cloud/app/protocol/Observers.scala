package protocol

import controllers.Utils
import models.Resource
import play.api.Logger
import rx.Observer

class StoringObserver(resourceId: Long) extends Observer[Response] {
  def onNext(response: Response) {
    Resource.getById(resourceId).parseAndStore(response.body, response.contentType, Utils.currentTime())
  }

  def onError(exception: Exception) {
    Logger.debug(s"An error occured while observing resource $resourceId", exception)
  }

  def onCompleted(): Unit = Unit
}
