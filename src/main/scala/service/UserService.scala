package service

import domain.UserCommand
import environment.Environments.UserEnvironment
import failures.UserFailure
import zio.ZIO

object UserService {

  def createUser(userC: UserCommand): ZIO[UserEnvironment, UserFailure, Unit] = ZIO.accessM { env =>
    def validateField(value: String): ZIO[UserEnvironment, UserFailure, Unit] = {
      if (value.isEmpty) {
        ZIO.fail(UserFailure.FieldInvalid("Field is Empty"))
      } else {
        ZIO.succeed(())
      }
    }
    def validateIsUniqueEmail(email: String): ZIO[UserEnvironment, UserFailure, Unit] = {
      for {
        user <- env.userRep.findUserByEmail(email).mapError(e => UserFailure.UserRepositoryInvalid(e))
        _    <- ZIO.succeed(println(user))
        _ <- user match {
              case Some(_) => ZIO.fail(UserFailure.FieldInvalid("Email not is Unique"))
              case None    => ZIO.succeed(())
            }
      } yield ()
    }
    def validatedCreateUser(userC: UserCommand): ZIO[UserEnvironment, UserFailure, Unit] = {
      for {
        _ <- validateField(userC.fullName)
        _ <- validateField(userC.email)
        _ <- validateField(userC.password)
        _ <- validateIsUniqueEmail(userC.email)
      } yield ()
    }

    for {
      _ <- validatedCreateUser(userC)
      _ <- env.userRep.createUser(userC).mapError(e => UserFailure.UserRepositoryInvalid(e))
    } yield ()
  }
}
