package failures

import failures.environmental.RepositoryFailure

sealed trait ProductFailure

object ProductFailure {
  case class FieldInvalid(msg: String)                extends ProductFailure
  case class NotFoundProduct(msg: String)             extends ProductFailure
  case class ProductParserInvalid(msg: String)        extends ProductFailure
  case class RepositoryInvalid(er: RepositoryFailure) extends ProductFailure
}
