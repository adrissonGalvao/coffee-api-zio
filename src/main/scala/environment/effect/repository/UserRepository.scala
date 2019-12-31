package environment.effect.repository

import domain.{User, UserCommand}
import doobie.util.transactor.Transactor
import environment.Environments.UserEnvironment
import failures.environmental.{RepositoryFailure}
import zio.{Task, ZIO}

trait UserRepository {
  val userRep: UserRepository.Effect
}

object UserRepository {
  trait Effect {
    def createUser(userC: UserCommand): ZIO[UserEnvironment, RepositoryFailure, Unit]
    def findUserByEmail(email: String): ZIO[UserEnvironment, RepositoryFailure, Option[User]]
  }

  trait Live {

    def userRep(xa: Transactor[Task]) = new Effect {

      import doobie.implicits._
      import zio.interop.catz._

      override def createUser(userC: UserCommand): ZIO[UserEnvironment, RepositoryFailure, Unit] = ZIO.accessM { _ =>
        def query =
          sql"""
               |INSERT INTO user
               | (full_name,email,password)
               | values (${userC.fullName},${userC.email},${userC.password})
      """.stripMargin

        query.update.run
          .transact(xa)
          .map(_ => ())
          .mapError(_ => RepositoryFailure.RepositoryDefect("noT create user "))
      }

      override def findUserByEmail(email: String): ZIO[UserEnvironment, RepositoryFailure, Option[User]] =
        ZIO.accessM { _ =>
          def statement =
            sql"""
               |SELECT 
               |u.id,
               |u.full_name,
               |u.password,
               |u.email
               |FROM user u where u.email = $email
               |""".stripMargin
          statement
            .query[User]
            .to[List]
            .transact(xa)
            .flatMap {
              case u :: Nil => ZIO.succeed(Some(u))
              case _        => ZIO.succeed(None)
            }
            .mapError(_ => RepositoryFailure.RepositoryDefect("not select user "))
        }
    }

  }
  object Live extends Live
}
