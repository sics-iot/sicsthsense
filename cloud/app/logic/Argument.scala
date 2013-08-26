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

import controllers.Utils

trait Require {
  protected def thr(message: String): Unit

  case class Is[A: Ordering](value: A) {
    private val ord = Ordering[A]

    def equalTo(other: A): Unit = {
      if (ord.equiv(value, other)) {
        thr(s"$value is not equal to $other")
      }
    }

    def greaterThan(other: A): Unit = {
      if (!ord.gt(value, other)) {
        thr(s"$value is not greater than $other")
      }
    }

    def greaterThanEqual(other: A): Unit = {
      if (!ord.gteq(value, other)) {
        thr(s"$value is not greater than $other")
      }
    }

    def lessThan(other: A): Unit = {
      if (!ord.lt(value, other)) {
        thr(s"$value is not greater than $other")
      }
    }

    def lessThanEqual(other: A): Unit = {
      if (!ord.lteq(value, other)) {
        thr(s"$value is not greater than $other")
      }
    }
  }

  def require(b: Boolean, message: String): Unit = {
    if (!b) {
      thr(message)
    }
  }

  def requireNot(b: Boolean, message: String): Unit = {
    if (b) {
      thr(message)
    }
  }

  def notNull[A](a: A): Unit = {
    if (a == null) {
      thr("Argument is null")
    }
  }

  def notEmpty(s: String): Unit = {
    if (Utils.isNullOrWhitespace(s)) {
      thr("String is empty")
    }
  }

  def positive(i: Int): Unit = {
    if (i < 0) {
      thr(s"Number $i is negative")
    }
  }

  def positive(i: Long): Unit = {
    if (i < 0) {
      thr(s"Number $i is negative")
    }
  }

  def absolutePath(path: String): Unit = {
    if (Utils.isNullOrWhitespace(path)) {
      thr("String is empty")
    }

    if (!path.startsWith("/")) {
      thr(s"Path $path is not an absolute path because it does not start with '/'")
    }

    if (path.length > 1 && path.endsWith("/")) {
      thr(s"Path $path is not a correct, absolute path because it ends with '/'")
    }
  }

  def is[A: Ordering](value: A): Is[A] = Is(value)
}

object Argument extends Require {
  def thr(message: String): Unit =
    throw new IllegalArgumentException(message)
}

object State extends Require {
  def thr(message: String): Unit =
    throw new IllegalStateException(message)
}
