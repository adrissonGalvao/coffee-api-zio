package failures.environmental

sealed trait RepositoryFailure
object RepositoryFailure {
  case class RepositoryDefect(msg: String) extends RepositoryFailure

}
