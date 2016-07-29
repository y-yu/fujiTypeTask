case class Transaction()

case class ReadTransaction()

case class ReadWriteTransaction()

case class ReadBatchTransaction()

object Implicits {
  implicit val read = new (ReadTransaction <-< Transaction) {}

  implicit val readwrite = new (ReadWriteTransaction <-< ReadTransaction) {}

  implicit val readBatch = new (ReadBatchTransaction <-< ReadTransaction) {}

  //implicit val ng = new (Transaction <-< ReadBatchTransaction) {}
}

