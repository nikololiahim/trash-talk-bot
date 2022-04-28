package trash.core
import cats.effect.IO

object Settings {
  val botEnvVarName = "TRASHTALK_TOKEN"
  val botToken: IO[String] = IO.fromOption(sys.env.get(botEnvVarName))(
    new Exception(
      f"Telegram Bot API token is not set. " +
        s"Please set the \"$botEnvVarName\" environment variable" +
        " with your token value."
    )
  )
//  val botName: IO[String] = botToken.flatMap(Aux.getBotName)

  val dbName: String = sys.env.getOrElse("TRASHDB_NAME", "postgres")
  val dbURL: String =
    sys.env.getOrElse("TRASHDB_URL", "jdbc:postgresql://localhost:5432")
  val dbUsername: String = sys.env.getOrElse("TRASHDB_UNAME", "postgres")
  val dbPassword: String = sys.env.getOrElse("TRASHDB_PASS", "changeme")

  val backendURL: String = sys.env.getOrElse("TRASHBACK_URL", "localhost")
  val backendPort: IO[Int] =
    IO.fromOption(sys.env.getOrElse("TRASHBACK_PORT", "8080").toIntOption)(
      new Exception(
        f"Could not retrieve the proper port value. " +
          s"The provided value could not be converted to int"
      )
    )

}

//object Aux {
//  case class Content(
//    username: String
//  )
//  case class User(
//    ok: Boolean,
//    result: Content,
//  )
//
//  def getBotName(token: String): IO[String] = {
//    implicit val circeDecoder2  = deriveDecoder[Content]
//    implicit val circeDecoder  = deriveDecoder[User]
//    implicit val http4sDecoder = jsonOf[IO, User]
//
//    val req = GET(uri"https://api.telegram.org/bot${token}/getMe")
//
//    EmberClientBuilder.default[IO].build.use { client =>
//      client
//        .expect(req)(jsonOf[IO, User])
//        .map(_.result.username)
//    }
//  }
//}
