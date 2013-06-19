package logic

import scala.util.Try
import scala.util.Success
import scala.util.Failure

trait Default[A] {
  def value: A
}

object Default {
  class ScalaDefault[A] extends Default[A] {
    private var v: A = _
    def value = v
  }

  def withValue[A](a: A) = new Default[A] {
    def value = a
  }

  implicit val forBoolean: Default[Boolean] = Default withValue false
  implicit val forChar: Default[Char] = Default withValue ' '
  implicit def forNumeric[A](implicit n: Numeric[A]): Default[A] = Default withValue n.zero
  implicit val forString: Default[String] = Default withValue ""
  implicit def forList[A]: Default[List[A]] = Default withValue List.empty[A]
  implicit def forMap[A, B]: Default[Map[A, B]] = Default withValue Map.empty[A, B]
  implicit def forOption[A]: Default[Option[A]] = Default withValue (None: Option[A])
  implicit def forAny[A <: Any]: Default[A] = new ScalaDefault[A]
}

class Result[T](val code: ResultCode, val message: String, val data: T, val exception: Throwable) {
  def isSuccess: Boolean = code == ResultCode.Ok
  def isFailure: Boolean = !isSuccess

  def fold[U](onFailure: (ResultCode, String, Throwable) => U, onSuccess: T => U) =
    if (isSuccess)
      onSuccess(data)
    else
      onFailure(code, message, exception)
}

object Result {
  def default[A: Default] = implicitly[Default[A]].value

  def apply[T](data: T): Result[T] = new Result[T](ResultCode.Ok, "", data, null)

  def apply[T](ex: Throwable): Result[T] = new Result[T](ResultCode.InternalError, ex.getMessage(), default[T], ex)
  def apply[T](ex: Throwable, msg: String): Result[T] = new Result[T](ResultCode.InternalError, msg, default[T], ex)

  def apply[T](code: ResultCode, ex: Throwable): Result[T] = new Result[T](code, ex.getMessage(), default[T], ex)
  def apply[T](code: ResultCode, ex: Throwable, msg: String): Result[T] = new Result[T](code, msg, default[T], ex)

  def apply[T](code: ResultCode): Result[T] = new Result[T](code, code.toString(), default[T], null)
  def apply[T](code: ResultCode, msg: String): Result[T] = new Result[T](code, msg, default[T], null)

  def apply[T](action: Try[T]): Result[T] = action match {
    case Success(data) => Result(data)
    case Failure(t)    => Result(t)
  }
}
