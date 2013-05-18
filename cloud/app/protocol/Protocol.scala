package protocol

import scala.concurrent.Future

trait Protocol[TRequest, TResponse] {
  def request(request: Request): Future[Response]

  def translateRequest(request: TRequest): Request

  def translateResponse(response: TResponse): Response
}