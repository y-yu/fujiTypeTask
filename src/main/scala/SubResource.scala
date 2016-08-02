import scala.annotation.implicitNotFound

trait <-<[A, B]

@implicitNotFound("Cannot find instances of the type ${A} <*< ${B}")
trait <*<[A, B]

trait :+:[A, B]

trait :->[A <: (_ :+: _), B]

@implicitNotFound("Cannot find instances of the type ${A} :*> ${B}")
trait :*>[A <: (_ :+: _), B]

trait :!=:[A, B]

object <*< {
  implicit def one[A, B](implicit A: A <-< B): A <*< B = new (A <*< B) {}

  implicit def transitive[A, B, C](implicit A: A <-< B, B: B <*< C): A <*< C = new (A <*< C) {}
}

object :-> {
  implicit def order[A, B](implicit A: B <*< A): ((A :+: B) :-> B) = new ((A :+: B) :-> B) {}
}

object :*> {
  implicit def native[A, B, C](implicit R: (A :+: B) :-> C): ((A :+: B) :*> C) = new ((A :+: B) :*> C) {}

  implicit def commutative[A, B, C](implicit R: (A :+: B) :-> C): ((B :+: A) :*> C) = new ((B :+: A) :*> C) {}

  implicit def moreOneStep[A, B, C](implicit A: C <*< A, B: C <*< B): ((A :+: B) :*> C) = new ((A :+: B) :*> C) {}

  implicit def self[A]: (A :+: A) :*> A = new ((A :+: A) :*> A) {}
}

object :!=: {
  implicit def ambiguous1[A, B](implicit R: A =:= B): A :!=: B = sys.error("Unexpected call")
  implicit def ambiguous2[A, B](implicit R: A =:= B): A :!=: B = sys.error("Unexpected call")

  implicit def not[A, B]: A :!=: B = new (A :!=: B) {}
}
