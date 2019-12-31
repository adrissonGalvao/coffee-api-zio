package service

import domain.ProductCommand
import environment.Environments.ProductEnvironment
import failures.ProductFailure
import zio.ZIO

object ProductService {
  import pure.Validate._

  def createProduct(pc: ProductCommand): ZIO[ProductEnvironment, ProductFailure, Unit] = ZIO.accessM { env =>
    for {
      _ <- validateField(pc.name).mapError(e => ProductFailure.FieldInvalid(e.getMessage))
      _ <- env.productRep.createProduct(pc).mapError(e => ProductFailure.RepositoryInvalid(e))
    } yield ()
  }
}
