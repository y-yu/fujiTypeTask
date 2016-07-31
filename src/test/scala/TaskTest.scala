import scalaprops._

object TaskTest extends Scalaprops {
  import scalaz._
  import scalaz.std.anyVal._
  import TaskTestHelper._

  implicit def taskMonadInstance[R, A] = new Monad[({type L[B] = Task[R, B]})#L] {
    def point[B](a: => B): Task[R, B] = Task(a)
    def bind[B, C](a: Task[R, B])(f: B => Task[R, C]): Task[R, C] = a.flatMap(f)
  }

  type ReadTransactionTask[A] = Task[ReadTransaction, A]
  val readTransactionTaskMonadLawsTest = Properties.list(
    scalazlaws.monad.all[ReadTransactionTask]
  )

  type ReadWriteTransactionTask[A] = Task[ReadWriteTransaction, A]
  val readWriteTransactionTaskMonadLawsTest = Properties.list(
    scalazlaws.monad.all[ReadWriteTransactionTask]
  )
}
