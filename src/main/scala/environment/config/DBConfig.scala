package environment.config

trait DBConfig {
  val dbconfig: DBConfig.Config
}

object DBConfig {
  trait Config {
    val url: String
    val user: String
    val password: String
    val driver: String
  }

  trait Live {
    val dbconfig: typedconfig.DatabaseConfig => Config = cfg =>
      new Config {
        override val url: String      = cfg.url
        override val user: String     = cfg.user
        override val password: String = cfg.password
        override val driver: String   = cfg.driver
    }
  }
  object Live extends Live
}
