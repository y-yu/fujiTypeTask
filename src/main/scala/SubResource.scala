trait <-<[-A, +B]

trait <*<[-A, +B]

object <*< {
  implicit def self[A] = new (A <*< A) {}

  implicit def any[A] = new (A <*< Any) {}

  implicit def nothing[A] = new (Nothing <*< A) {}

  implicit def transitive[A, B, C](implicit A: A <-< B, B: B <*< C) = new (A <*< C) {}
}
