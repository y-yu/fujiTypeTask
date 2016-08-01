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

  def map[B](f: A => B): Task[R, B] = flatMap[R, R, B](a => Task[R, B](f(a)))

  def sub[S, T](implicit R: (R :+: S) :*> T): Task[T, A] =
    new Task[T, A] {
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
