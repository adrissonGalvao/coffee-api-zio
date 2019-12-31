package service

import domain.UserCommand
import environment.Environments.UserEnvironment
import failures.UserFailure
import zio.ZIO

object UserService {
  import pure.Validate._
  def createUser(userC: UserCommand): ZIO[UserEnvironment, UserFailure, Unit] = ZIO.accessM { env =>
    def validateIsUniqueEmail(email: String): ZIO[UserEnvironment, UserFailure, Unit] = {
      for {
        user <- env.userRep.findUserByEmail(email).mapError(e => UserFailure.RepositoryInvalid(e))
        _    <- ZIO.succeed(println(user))
        _ <- user match {
              case Some(_) => ZIO.fail(UserFailure.FieldInvalid("Email not is Unique"))
              case None    => ZIO.succeed(())
            }
      } yield ()
    }
    def validatedCreateUser(userC: UserCommand): ZIO[UserEnvironment, UserFailure, Unit] = {
      for {
        _ <- validateField(userC.fullName).mapError(e => UserFailure.FieldInvalid(e.getMessage))
        _ <- validateField(userC.email).mapError(e => UserFailure.FieldInvalid(e.getMessage))
        _ <- validateField(userC.password).mapError(e => UserFailure.FieldInvalid(e.getMessage))
        _ <- validateIsUniqueEmail(userC.email)
      } yield ()
    }

    for {
      _ <- validatedCreateUser(userC)
      _ <- env.userRep.createUser(userC).mapError(e => UserFailure.RepositoryInvalid(e))
    } yield ()
  }
}
