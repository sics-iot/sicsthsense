package logic

object Argument {
  def notNull[A](a: A): Unit = {
    if (a == null) {
      throw new NullPointerException()
    }
  }

  def notEmpty(s: String): Unit = {
    if (s == null) {
      throw new NullPointerException()
    }

    if (s.trim().length == 0) {
      throw new IllegalArgumentException("String is empty")
    }
  }
  
  def positive(i: Int): Unit = {
    if (i < 0) {
      throw new IllegalArgumentException("Number is negative")
    }
  }
  
  def positive(i: Long): Unit = {
    if (i < 0) {
      throw new IllegalArgumentException("Number is negative")
    }
  }
}