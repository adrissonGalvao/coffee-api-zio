package endpoint

import cats.data.Kleisli
import cats.effect.ExitCode
import environment.Environments.AppEnvironment
import org.http4s.{Request, Response}
import org.http4s.server.Router
import org.http4s.server.blaze.BlazeServerBuilder
import zio.{RIO, ZIO}
import zio.interop.catz._
import org.http4s.implicits._
import cats.syntax.all._
import typedconfig.HttpServerConfig

object Server {

  type ServerRIO[A] = RIO[AppEnvironment, A]

  def createRoutes(basePath: String): Kleisli[ServerRIO, Request[ServerRIO], Response[ServerRIO]] = {

    val userEndpoint    = new UserEndpoint[AppEnvironment]("user").endpoints
    val productEndpoint = new ProductEndpoint[AppEnvironment]("product").endpoints
    val routes          = userEndpoint <+> productEndpoint
    Router[ServerRIO](basePath -> routes).orNotFound
  }

  def createServer(cfg: HttpServerConfig): ZIO[AppEnvironment, Throwable, Unit] = {

    ZIO
      .runtime[AppEnvironment]
      .flatMap { implicit rts =>
        BlazeServerBuilder[ServerRIO]
          .bindHttp(cfg.port, "0.0.0.0")
          .withHttpApp(createRoutes(cfg.context))
          .serve
          .compile[ServerRIO, ServerRIO, ExitCode]
          .drain
      }
  }

}
