package endpoint

import domain.UserCommand
import endpoint.r.ErrorResponse
import environment.Environments.UserEnvironment
import failures.UserFailure
import failures.UserFailure.{FieldInvalid, UserParserInvalid}
import org.http4s.dsl.Http4sDsl
import org.http4s.{HttpRoutes, Response}
import support.JsonHttp4s
import zio.RIO
import zio.interop.catz._

class UserEndpoint[R <: UserEnvironment](rootUri: String) extends JsonHttp4s[R] {

  type UserEndpointTask[A] = RIO[R, A]

  import service.UserService._

  val dsl: Http4sDsl[UserEndpointTask] = Http4sDsl[UserEndpointTask]

  import dsl._

  def endpoints: HttpRoutes[UserEndpointTask] = HttpRoutes.of[UserEndpointTask] {

    case req @ POST -> Root / `rootUri` =>
      val pipe = for {
        uc <- req.as[UserCommand].mapError(_ => UserFailure.UserParserInvalid("Error read json"))
        _  <- createUser(uc)
      } yield ()

      pipe
        .foldM(e => handleError(e), Created(_))

  }

  private def handleError(f: UserFailure): UserEndpointTask[Response[UserEndpointTask]] = f match {
    case _: UserParserInvalid             => UnprocessableEntity(ErrorResponse("Payload is invalid"))
    case e: FieldInvalid                  => BadRequest(ErrorResponse(e.msg))
    case e: UserFailure.RepositoryInvalid => BadRequest(ErrorResponse(e.er.toString))

    case _ => InternalServerError()
  }
}
