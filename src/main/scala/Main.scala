import scala.concurrent.Await
import scala.concurrent.duration._

object Main {
  def main(args: Array[String]): Unit = {
    val t1 = Task[ReadTransaction, Int](1)
    val t2 = Task[ReadTransaction, String]("string")
    val t3 = Task[ReadWriteTransaction, Boolean](true)
    val t4 = Task[ReadBatchTransaction, Double](1.0)
    val t5 = Task[ReadWriteBatchTransaction, Unit]()

    {
      import TaskRunner.readRunner

      (for {
        a <- t1
      } yield ()).run()
    }

    {
      import TaskRunner.readRunner

      (for {
        a <- t1
        b <- t2
      } yield ()).run()
    }

    {
      import Implicits._
      import TaskRunner.readWriteRunner

      (for {
        a <- t1
        b <- t2
        c <- t3
      } yield ()).run()
    }

    {
      import Implicits._
      import TaskRunner.readWriteBatchRunner

      (for {
        a <- t5
        b <- t2.sub[ReadWriteTransaction]
        c <- t4
        e <- t3
      } yield ()).run()
    }

    {
      import Implicits._
      import TaskRunner.readRunner

      def f(x: Int): Task[ReadTransaction, String] = Task(x.toString)
      Await.ready((for {
        a <- Task[Transaction, Int](5).flatMap(f)
        b <- f(5)
      } yield {
        println(a == b)
      }).run(), 5 seconds)
    }

    {
      import Implicits._
      import TaskRunner.readRunner

      Await.ready((for {
        a <- t1.flatMap(a => Task[Transaction, Int](a))
        b <- t1
      } yield {
        println(a == b)
      }).run(), 5 seconds)
    }

    {
      import Implicits._
      import TaskRunner.readWriteRunner

      def f(x: Int): Task[ReadTransaction, String] = Task(x.toString)
      def g(x: String): Task[ReadWriteTransaction, Double] = Task(1.5)

      Await.ready((for {
        a <- t1.flatMap(f).flatMap(g)
        b <- t1.flatMap(x => f(x).flatMap(g))
      } yield {
        println(a == b)
      }).run(), 5 seconds)
    }
  }
}
