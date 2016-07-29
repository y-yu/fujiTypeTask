class <-<[A, B](implicit R: B <*!< A)

trait <*<[A, B]

trait <*!<[A, B]

object <*< {
  implicit def self[A] = new (A <*< A) {}

  implicit def any[A] = new (A <*< Any) {}

  implicit def nothing[A] = new (Nothing <*< A) {}

  implicit def transitive[A, B, C](implicit A: A <-< B, B: B <*< C) = new (A <*< C) {}
}

object <*!< {
  implicit def ambiguous1[A, B](implicit R: A <*< B): A <*!< B = sys.error("Unexpected call")
  implicit def ambiguous2[A, B](implicit R: A <*< B): A <*!< B = sys.error("Unexpected call")

  implicit def not[A, B] = new (A <*!< B) {}
}
