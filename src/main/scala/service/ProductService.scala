package service

import domain.ProductCommand
import environment.Environments.ProductEnvironment
import failures.ProductFailure
import zio.ZIO
import domain.Product
import failures.environmental.RepositoryFailure
object ProductService {
  import pure.Validate._

  def createProduct(pc: ProductCommand): ZIO[ProductEnvironment, ProductFailure, Unit] = ZIO.accessM { env =>
    for {
      _ <- validateField(pc.name).mapError(e => ProductFailure.FieldInvalid(e.getMessage))
      _ <- if (pc.userId == 0) ZIO.fail(ProductFailure.ProductParserInvalid("userId error")) else ZIO.succeed(())
      _ <- env.productRep.createProduct(pc).mapError(e => ProductFailure.RepositoryInvalid(e))
    } yield ()
  }

  def listAllProduct(): ZIO[ProductEnvironment, ProductFailure, List[Product]] = ZIO.accessM { env =>
    env.productRep.listAllProduct().mapError(e => ProductFailure.RepositoryInvalid(e))
  }

  def findProduct(id: Long): ZIO[ProductEnvironment, ProductFailure, Product] = ZIO.accessM { env =>
    for {
      _ <- if (id == 0) ZIO.fail(ProductFailure.ProductParserInvalid("userId error")) else ZIO.succeed(())
      p <- env.productRep
            .findProduct(id)
            .flatMap {
              case Some(p) => ZIO.succeed(p)
              case None    => ZIO.fail(ProductFailure.NotFoundProduct("Not found product"))
            }
            .mapError {
              case e: RepositoryFailure              => ProductFailure.RepositoryInvalid(e)
              case e: ProductFailure.NotFoundProduct => e
            }
    } yield p

  }

}
