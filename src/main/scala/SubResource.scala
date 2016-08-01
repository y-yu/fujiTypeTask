trait <-<[A, B]

trait <*<[A, B]

trait :+:[A, B]

trait :->[A <: (_ :+: _), B]

trait :*>[A, B]

object <*< {
  implicit def self[A]: A <*< A = new (A <*< A) {}

  implicit def transitive[A, B, C](implicit A: A <-< B, B: B <*< C): A <*< C = new (A <*< C) {}
}

object :-> {
  implicit def order[A, B, C](implicit A: C <*< A, B: C <*< B): ((A :+: B) :-> C) = new ((A :+: B) :-> C) {}
}

object :*> {
  implicit def native[A, B, C](implicit R: (A :+: B) :-> C): ((A :+: B) :*> C) = new ((A :+: B) :*> C) {}

  implicit def commutative[A, B, C](implicit R: (A :+: B) :-> C): ((B :+: A) :*> C) = new ((B :+: A) :*> C) {}

  implicit def self[A]: (A :+: A) :*> A = new ((A :+: A) :*> A) {}
}
