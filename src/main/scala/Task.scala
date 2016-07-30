import scala.concurrent.ExecutionContext
import scala.concurrent.Future

trait Task[R, +A] {lhs =>
  def execute[RR](res: RR)(implicit ec: ExecutionContext, RR: RR <*< R): Future[A]

  def flatMap[RR, B](f: A => Task[RR, B])(implicit RR: RR <*< R): Task[RR, B] =
    new Task[RR, B] {
      def execute[RRR](res: RRR)(implicit ec: ExecutionContext, RRR: RRR <*< RR): Future[B] = {
        implicit def specific(implicit RR: RR <*< R, RRR: RRR <*< RR) = new (RRR <*< R) {}

        lhs.execute(res).map(f).flatMap(_.execute(res))
      }
    }

  def map[B](f: A => B): Task[R, B] = flatMap(a => Task(f(a)))

  def sub[RR](implicit RR: RR <*< R): Task[RR, A] =
    new Task[RR, A] {
      def execute[RRR](res: RRR)(implicit ec: ExecutionContext, RRR: RRR <*< RR): Future[A] = {
        implicit def specific(implicit RR: RR <*< R, RRR: RRR <*< RR) = new (RRR <*< R) {}

        lhs.execute(res)
      }
    }

  def run()(implicit runner: TaskRunner[R]): Future[A] = runner.run(this)
}

object Task {
  def apply[R, A](a: => A): Task[R, A] =
    new Task[R, A] {
      def execute[RR](resource: RR)(implicit ec: ExecutionContext, RR: RR <*< R): Future[A] =
        Future(a)
    }

  def ask[R]: Task[R, R] =
    new Task[R, R] { lhs =>
      def execute[RR](res: RR)(implicit ec: ExecutionContext, RRR: RR <*< R): Future[R] =
        lhs.execute(res)
    }
}
