import scala.concurrent.ExecutionContext
import scala.concurrent.Future

trait Task[-R, +A] {lhs =>
  def execute[RR](resource: RR)(implicit ec: ExecutionContext, RR: RR <*< R): Future[A]

  def flatMap[RR, B](f: A => Task[RR, B])(implicit RR: RR <*< R): Task[RR, B] =
    new Task[RR, B] {
      def execute[RRR](resource: RRR)(implicit ec: ExecutionContext, RRR: RRR <*< RR): Future[B] = {
        implicit def specific(implicit RR: RR <*< R, RRR: RRR <*< RR) = new (RRR <*< R) {}

        lhs.execute(resource).map(f).flatMap(_.execute(resource))
      }
    }

  def map[B](f: A => B): Task[R, B] = flatMap(a => Task(f(a)))

  def run()(implicit runner: TaskRunner[R]): Future[A] = runner.run(this)
}

object Task {
  def apply[R, A](a: => A): Task[R, A] =
    new Task[R, A] {
      def execute[RR](resource: RR)(implicit ec: ExecutionContext, RR: RR <*< R): Future[A] =
        Future(a)
    }
}
