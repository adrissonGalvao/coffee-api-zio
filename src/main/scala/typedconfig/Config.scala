package typedconfig

final case class HttpServerConfig(host: String, context: String, port: Int)

final case class DatabaseConfig(driver: String, url: String, user: String, password: String)

final case class AppConfig(
    httpServer: HttpServerConfig,
    database: DatabaseConfig,
)

object ConfigLoader {

  import pureconfig.error.ConfigReaderFailures
  import pureconfig.generic.auto._

  def load: Either[ConfigReaderFailures, AppConfig] = {

    import pureconfig._

    ConfigSource.default.at("coffee-api").load[AppConfig]
  }

}
