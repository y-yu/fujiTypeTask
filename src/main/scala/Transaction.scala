case class Transaction()

case class ReadTransaction()

case class ReadWriteTransaction()

case class ReadBatchTransaction()

case class ReadWriteBatchTransaction()

object Implicits {
  implicit val read = new (ReadTransaction <-< Transaction) {}

  implicit val readwrite = new (ReadWriteTransaction <-< ReadTransaction) {}

  implicit val readBatch = new (ReadBatchTransaction <-< ReadTransaction) {}

  implicit val readWriteBatch1 = new (ReadWriteBatchTransaction <-< ReadWriteTransaction) {}

  implicit val readWriteBatch2 = new (ReadWriteBatchTransaction <-< ReadBatchTransaction) {}
}

