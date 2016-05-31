import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait TaskRunner[+R] {
  def run[A](task: Task[R, A]): Future[A]
}

object TaskRunner {
  implicit def readRunner[R](implicit R: ReadTransaction <+< R) = new TaskRunner[R] {
    def run[A](task: Task[R, A]): Future[A] = {
      println("read task")
      task.execute(ReadTransaction())
    }
  }

  implicit def writeRunner[R](implicit R: ReadWriteTransaction <+< R) = new TaskRunner[R] {
    def run[A](task: Task[R, A]): Future[A] = {
      println("write task")
      task.execute(ReadWriteTransaction())
    }
  }
}
