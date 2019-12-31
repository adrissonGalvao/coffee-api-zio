package environment.effect.repository

import doobie.util.transactor.Transactor
import doobie.util.transactor.Transactor.Aux
import typedconfig.DatabaseConfig
import zio.{Task, ZIO}
import zio.interop.catz._

object TransactorBuilder {
  def createTransactor(cfg: DatabaseConfig): Task[Aux[Task, Unit]] =
    ZIO effect
      Transactor
        .fromDriverManager[Task](cfg.driver, cfg.url, cfg.user, cfg.password)
}
