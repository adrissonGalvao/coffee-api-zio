package failures

import failures.environmental.RepositoryFailure

sealed trait UserFailure

object UserFailure {
  case class FieldInvalid(msg: String)                extends UserFailure
  case class UserParserInvalid(msg: String)           extends UserFailure
  case class RepositoryInvalid(er: RepositoryFailure) extends UserFailure
}
