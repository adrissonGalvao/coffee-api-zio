package environment

import doobie.util.transactor.Transactor
import environment.config.DBConfig
import environment.effect.repository.{ProductRepository, UserRepository}
import typedconfig.AppConfig
import zio.{Task, ZIO}
import zio.blocking.Blocking
import zio.clock.Clock
import zio.console.Console
import zio.random.Random
import zio.system.System

object Environments {
  type AppEnvironment = Clock
    with Console
    with System
    with Random
    with Blocking
    with DBConfig
    with UserRepository
    with ProductRepository
  type UserEnvironment    = Clock with Blocking with DBConfig with UserRepository
  type ProductEnvironment = Clock with Blocking with DBConfig with ProductRepository
//  type BuyEnvironment     = Clock with Blocking with DBConfig with BuyRepository

  def createEnvironment(cfg: AppConfig, transactor: Transactor[Task]): ZIO[Any, Nothing, AppEnvironment] =
    ZIO.effect {
      new Clock with Console with System with Random with Blocking with DBConfig with UserRepository
      with ProductRepository {
        override val clock: Clock.Service[Any]            = Clock.Live.clock
        override val console: Console.Service[Any]        = Console.Live.console
        override val system: System.Service[Any]          = System.Live.system
        override val random: Random.Service[Any]          = Random.Live.random
        override val blocking: Blocking.Service[Any]      = Blocking.Live.blocking
        override val dbconfig: DBConfig.Config            = DBConfig.Live.dbconfig(cfg.database)
        override val userRep: UserRepository.Effect       = UserRepository.Live.userRep(transactor)
        override val productRep: ProductRepository.Effect = ProductRepository.Live.productRep(transactor)
//        override val buyRep: BuyRepository.Effect         = BuyRepository.Live.buyRep(transactor)
      }
    }.orDie
}
