package environment.effect.repository

import domain.{Product, ProductCommand, User}
import doobie.util.transactor.Transactor
import environment.Environments.ProductEnvironment
import failures.environmental.RepositoryFailure
import failures.environmental.RepositoryFailure.RepositoryDefect
import zio.{Task, ZIO}

trait ProductRepository {
  val productRep: ProductRepository.Effect
}

object ProductRepository {
  trait Effect {
    def createProduct(productC: ProductCommand): ZIO[ProductEnvironment, RepositoryFailure, Unit]
    def listAllProduct(): ZIO[ProductEnvironment, RepositoryFailure, List[Product]]
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
                 | values (${productC.name},${productC.userId})
      """.stripMargin

          query.update.run
            .transact(xa)
            .map(_ => ())
            .mapError(_ => RepositoryFailure.RepositoryDefect("not create product "))
        }

      override def listAllProduct(): ZIO[ProductEnvironment, RepositoryFailure, List[Product]] =
        ZIO.accessM { _ =>
          def statement =
            sql"""
                 |SELECT 
                 |p.id,
                 |p.name,
                 |u.id,
                 |u.full_name,
                 |u.email
                 |FROM product p INNER JOIN user u ON u.id=p.user_id
                 """.stripMargin

          val q = statement.query[(Long, String, Long, String, String)]

          q.to[List]
            .transact(xa)
            .flatMap { els =>
              els.map(t => {
                val user = User(t._3, t._4, t._5)
                Product(t._1, t._2, user)
              }) match {
                case lp: List[Product] => ZIO.succeed(lp)
                case _                 => ZIO.fail(new Exception("error parser Data"))
              }
            }
            .mapError(e => RepositoryDefect(e.getMessage))

        }
    }

  }
  object Live extends Live
}
