package failures.environmental

sealed trait UserRepositoryFailure
object UserRepositoryFailure {
  case class RepositoryDefect(msg: String) extends UserRepositoryFailure

}
