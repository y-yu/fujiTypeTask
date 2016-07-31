trait :+:[A, B]

trait :->[A <: (_ :+: _), B]

trait :*>[A, B]

object :*> {
  implicit def native[A, B, C](implicit R: (A :+: B) :-> C): ((A :+: B) :*> C) = new ((A :+: B) :*> C) {}

  implicit def commutative[A, B, C](implicit R: (A :+: B) :-> C): ((B :+: A) :*> C) = new ((B :+: A) :*> C) {}

  implicit def self[A]: (A :+: A) :*> A = new ((A :+: A) :*> A) {}
}
