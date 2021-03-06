import scala.concurrent.ExecutionContext
import scala.concurrent.Future

trait Task[R, +A] {lhs =>
  def execute()(implicit ec: ExecutionContext): Future[A]

  def flatMap[S, T, B](f: A => Task[S, B])(implicit R: (R :+: S) :*> T): Task[T, B] =
    new Task[T, B] {
      def execute()(implicit ec: ExecutionContext): Future[B] = {
        lhs.execute().map(f).flatMap(_.execute())
      }
    }

  def map[S, T, B](f: A => B)(implicit R: (R :+: S) :*> T): Task[T, B] = flatMap(a => Task[S, B](f(a)))

  def sub[S](implicit R: (R :+: S) :*> S): Task[S, A] =
    new Task[S, A] {
      def execute()(implicit ec: ExecutionContext): Future[A] = {
        lhs.execute()
      }
    }

  def run()(implicit runner: TaskRunner[R]): Future[A] = runner.run(this)
}

object Task {
  def apply[R, A](a: => A): Task[R, A] =
    new Task[R, A] {
      def execute()(implicit ec: ExecutionContext): Future[A] =
        Future(a)
    }

  def failed(a: Throwable): Task[Any, Nothing] =
    new Task[Any, Nothing] {
      def execute()(implicit ec: ExecutionContext): Future[Nothing] =
        Future.failed(a)
    }
}
