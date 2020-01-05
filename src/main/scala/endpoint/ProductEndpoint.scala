package endpoint

import domain.ProductCommand
import endpoint.r.ErrorResponse
import environment.Environments.ProductEnvironment
import failures.ProductFailure.{FieldInvalid, ProductParserInvalid, RepositoryInvalid}
import failures.ProductFailure
import org.http4s.{HttpRoutes, Response}
import org.http4s.dsl.Http4sDsl
import support.JsonHttp4s
import zio.{RIO}
import zio.interop.catz._

class ProductEndpoint[R <: ProductEnvironment](rootUri: String) extends JsonHttp4s[R] {
  type ProductEndpointTask[A] = RIO[R, A]

  import service.ProductService._

  val dsl: Http4sDsl[ProductEndpointTask] = Http4sDsl[ProductEndpointTask]

  import dsl._

  private def handleError(f: ProductFailure): ProductEndpointTask[Response[ProductEndpointTask]] = f match {
    case e: FieldInvalid         => BadRequest(ErrorResponse(e.msg))
    case _: ProductParserInvalid => UnprocessableEntity("Json not is valid")
    case e: RepositoryInvalid    => BadRequest(ErrorResponse(e.er.toString))
    case _                       => InternalServerError()
  }

  def endpoints: HttpRoutes[ProductEndpointTask] = HttpRoutes.of[ProductEndpointTask] {

    case req @ POST -> Root / `rootUri` =>
      val pipe = for {
        uc <- req.as[ProductCommand].mapError(_ => ProductFailure.FieldInvalid("Error read json"))
        _  <- createProduct(uc)
      } yield ()

      pipe
        .foldM(e => handleError(e), Created(_))

    case _ @GET -> Root / `rootUri` =>
      listAllProduct().foldM(e => handleError(e), Ok(_))

  }
}
