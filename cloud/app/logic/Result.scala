/*
 * Copyright (c) 2013, Institute for Pervasive Computing, ETH Zurich.
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of The Swedish Institute of Computer Science nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE SWEDISH INSTITUTE OF COMPUTER SCIENCE BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 * Authors:
 *  26/08/2013 Adrian Kündig (adkuendi@ethz.ch)
 */

package logic

import scala.util.Try
import scala.util.Success
import scala.util.Failure

import play.api.Logger

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
  private val logger = Logger(this.getClass())

  if (logger.isDebugEnabled && exception != null) logger.debug("Error Result", exception)

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
