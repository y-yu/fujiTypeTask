case class Transaction()

case class ReadTransaction()

case class ReadWriteTransaction()

case class ReadBatchTransaction()

object Implicits {
  implicit val read = new ((ReadTransaction :+: Transaction) :-> ReadTransaction) {}

  implicit val readwrite = new ((ReadWriteTransaction :+: ReadTransaction) :-> ReadWriteTransaction) {}

  implicit val readBatch = new ((ReadBatchTransaction :+: ReadTransaction) :-> ReadBatchTransaction) {}
}

