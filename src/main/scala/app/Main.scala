package app

import endpoint.Server
import environment.Environments
import environment.effect.repository.TransactorBuilder
import typedconfig.ConfigLoader
import zio.{App, ZIO}

object Main extends App {
  override def run(args: List[String]): ZIO[zio.ZEnv, Nothing, Int] = {
    val program = for {
      cfg        <- ZIO.fromEither(ConfigLoader.load)
      transactor <- TransactorBuilder.createTransactor(cfg.database)
      env        <- Environments.createEnvironment(cfg, transactor)
      _          <- Server.createServer(cfg.httpServer).provide(env)

    } yield ()

    program
      .foldM(e => ZIO.effectTotal(println(e)) *> ZIO.succeed(1), _ => ZIO.succeed(0))
  }
}
