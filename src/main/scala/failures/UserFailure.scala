package failures

import failures.environmental.UserRepositoryFailure

sealed trait UserFailure

object UserFailure {
  case class FieldInvalid(msg: String)                        extends UserFailure
  case class UserParserInvalid(msg: String)                   extends UserFailure
  case class UserRepositoryInvalid(er: UserRepositoryFailure) extends UserFailure
}
