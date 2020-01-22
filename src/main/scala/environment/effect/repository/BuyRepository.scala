package environment.effect.repository

import domain.{BuyCommand}
import doobie.util.transactor.Transactor
import environment.Environments.{BuyEnvironment, ProductEnvironment}
import failures.environmental.RepositoryFailure
import zio.{Task, ZIO}

trait BuyRepository {
  val buyRep: BuyRepository.Effect
}
object BuyRepository {

  trait Effect {
    def createBuy(buyC: BuyCommand): ZIO[BuyEnvironment, RepositoryFailure, Unit]
  }

  trait Live {

    def buyRep(xa: Transactor[Task]) = new Effect {

      import doobie.implicits._
      import zio.interop.catz._

      override def createBuy(buyC: BuyCommand): ZIO[BuyEnvironment, RepositoryFailure, Unit] = ZIO.accessM { _ =>
        def query =
          sql"""
               |INSERT INTO buy
               | (value_freight,quantity,user_buy,product_id)
               | values (${buyC.valueFreight},${buyC.quantity}, ${buyC.userBuy}, ${buyC.productId})
      """.stripMargin

        query.update.run
          .transact(xa)
          .map(_ => ())
          .mapError(_ => RepositoryFailure.RepositoryDefect("not create buy"))
      }
    }

  }
  object Live extends Live
}
