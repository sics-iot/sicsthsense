package logic

object ResultCode extends Enumeration {
  type Type = Value
  val Ok, NotFound, InternalError, TimedOut = Value
}