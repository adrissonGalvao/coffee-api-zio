package environment.effect.repository

import domain.ProductCommand
import doobie.util.transactor.Transactor
import environment.Environments.ProductEnvironment
import failures.environmental.{RepositoryFailure}
import zio.{Task, ZIO}

trait ProductRepository {
  val productRep: ProductRepository.Effect
}

object ProductRepository {
  trait Effect {
    def createProduct(productC: ProductCommand): ZIO[ProductEnvironment, RepositoryFailure, Unit]
  }

  trait Live {

    def productRep(xa: Transactor[Task]) = new Effect {

      import doobie.implicits._
      import zio.interop.catz._

      override def createProduct(productC: ProductCommand): ZIO[ProductEnvironment, RepositoryFailure, Unit] =
        ZIO.accessM { _ =>
          def query =
            sql"""
                 |INSERT INTO product
                 | (name,user_id)
                 | values (${productC.name},${productC.idUser})
      """.stripMargin

          query.update.run
            .transact(xa)
            .map(_ => ())
            .mapError(_ => RepositoryFailure.RepositoryDefect("not create product "))
        }
    }

  }
  object Live extends Live
}
