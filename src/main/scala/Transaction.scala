case class Transaction()

case class ReadTransaction()

case class ReadWriteTransaction()

case class ReadBatchTransaction()

case class ReadWriteBatchTransaction()

trait Base {
  implicit val readWriteBatch2 = new (ReadWriteBatchTransaction <-< ReadBatchTransaction) {}
}

object Implicits extends Base {
  implicit val read = new (ReadTransaction <-< Transaction) {}

  implicit val readWrite = new (ReadWriteTransaction <-< ReadTransaction) {}

  implicit val readBatch = new (ReadBatchTransaction <-< ReadTransaction) {}

  implicit val readWriteBatch1 = new (ReadWriteBatchTransaction <-< ReadWriteTransaction) {}
}

