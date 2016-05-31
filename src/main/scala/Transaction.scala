case class Transaction()

case class ReadTransaction()

case class ReadWriteTransaction()

object Implicits {
  implicit val read = new (ReadTransaction <-< Transaction) {}

  implicit val readwrite = new (ReadWriteTransaction <-< ReadTransaction) {}
}

