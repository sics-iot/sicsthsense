package logic

import controllers.Utils

trait Require {
  protected def thr(message: String): Unit

  case class Is[A: Ordering](value: A) {
    private val num = Ordering[A]

    def greaterThan(other: A): Unit = {
      if (!num.gt(value, other)) {
        thr(s"$value is not greater than $other")
      }
    }

    def greaterThanEqual(other: A): Unit = {
      if (!num.gteq(value, other)) {
        thr(s"$value is not greater than $other")
      }
    }

    def lessThan(other: A): Unit = {
      if (!num.lt(value, other)) {
        thr(s"$value is not greater than $other")
      }
    }

    def lessThanEqual(other: A): Unit = {
      if (!num.lteq(value, other)) {
        thr(s"$value is not greater than $other")
      }
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
      thr("Number is negative")
    }
  }

  def positive(i: Long): Unit = {
    if (i < 0) {
      thr("Number is negative")
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
