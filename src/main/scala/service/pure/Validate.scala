package service.pure

import zio.ZIO

object Validate {
  def validateField(value: String): ZIO[Any, Throwable, Unit] = {
    if (value.isEmpty) {
      ZIO.fail(new Exception("Field is Empty"))
    } else {
      ZIO.succeed(())
    }
  }

}
