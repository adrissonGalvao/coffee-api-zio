package service

import domain.ProductCommand
import environment.Environments.ProductEnvironment
import failures.ProductFailure
import zio.ZIO
import domain.Product
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
}
