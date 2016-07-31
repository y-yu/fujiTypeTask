import java.util.concurrent.TimeUnit
import scala.concurrent.Await
import scala.concurrent.ExecutionContext
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration.Duration
import scala.util.Failure
import scala.util.Success
import scala.util.Try
import scalaprops.Gen
import scalaz.Equal

object TaskTestHelper {
  implicit val genReadTransaction: Gen[ReadTransaction] = Gen.value(ReadTransaction())

  implicit val genReadWriteTransaction: Gen[ReadWriteTransaction] = Gen.value(ReadWriteTransaction())

  case class ErrorTask(value: Int) extends RuntimeException

  implicit def genErrorTask(implicit I: Gen[Int]): Gen[ErrorTask] =
    I.map(x => ErrorTask(x))

  implicit def equalErrorTask(implicit I: Equal[Int]): Equal[ErrorTask] =
    I.contramap(_.value)

  implicit def equalFuture[A](implicit A: Equal[A], E: Equal[ErrorTask]): Equal[Future[A]] =
    Equal.equal(
      (a, b) => (
        Try(Await.result(a, Duration(5, TimeUnit.SECONDS))),
        Try(Await.result(b, Duration(5, TimeUnit.SECONDS)))
      ) match {
        case (Success(va), Success(vb)) => A.equal(va, vb)
        case (Failure(ea@(ErrorTask(_))), Failure(eb@ErrorTask(_))) => E.equal(ea, eb)
        case _ => false
      }
    )

  implicit def genTaskRunner[R](implicit R: Gen[R]): Gen[TaskRunner[R]] =
    R.map[TaskRunner[R]] {
      r => new TaskRunner[R] {
        def run[A](f: Task[R, A]): Future[A] = f.execute(r)
      }
    }

  implicit def equalTask[R, A](implicit F: Equal[Future[A]], R: Gen[TaskRunner[R]]): Equal[Task[R, A]] =
    F.contramap(_.run()(R.sample()))

  implicit def genTask[R, A](implicit A: Gen[A], E: Gen[ErrorTask]): Gen[Task[R, A]] =
    Gen.frequency(
      1 -> E.map[Task[R, A]] { e =>
        new Task[R, A] {
          def execute[RR](res: RR)(implicit ec: ExecutionContext, RR: RR <*< R): Future[A] =
            Future.failed(e)
        }
      },
      2 -> A.map[Task[R, A]] { a =>
        new Task[R, A] {
          def execute[RR](res: RR)(implicit ec: ExecutionContext, RR: RR <*< R): Future[A] =
            Future.successful(a)
        }
      }
    )
}
