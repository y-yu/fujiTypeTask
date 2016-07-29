
object Main {
  def main(args: Array[String]): Unit = {
    val t1 = Task[ReadTransaction, Int](1)
    val t2 = Task[ReadTransaction, String]("string")
    val t3 = Task[ReadWriteTransaction, Boolean](true)
    val t4 = Task[ReadBatchTransaction, Double](1.0)

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
  }
}
