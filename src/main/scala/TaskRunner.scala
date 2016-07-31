import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait TaskRunner[R] {
  def run[A](task: Task[R, A]): Future[A]
}

object TaskRunner {
  implicit val readRunner = new TaskRunner[ReadTransaction] {
    def run[A](task: Task[ReadTransaction, A]): Future[A] = {
      println("read task")
      task.execute()
    }
  }

  implicit val readWriteRunner = new TaskRunner[ReadWriteTransaction] {
    def run[A](task: Task[ReadWriteTransaction, A]): Future[A] = {
      println("write task")
      task.execute()
    }
  }

  implicit val readBatchRunner = new TaskRunner[ReadBatchTransaction] {
    def run[A](task: Task[ReadBatchTransaction, A]): Future[A] = {
      println("read batch task")
      task.execute()
    }
  }
}
