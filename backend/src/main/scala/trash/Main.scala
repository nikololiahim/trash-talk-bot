package trash

import cats.effect._
import doobie.util.transactor.Transactor
import org.http4s.blaze.server.BlazeServerBuilder
import trash.core.Settings.{backendPort, backendURL, botToken, dbName, dbPassword, dbURL, dbUsername}
import trash.repository.{Server, TelegramAPI}

object Main extends IOApp {

  override def run(args: List[String]): IO[ExitCode] = for {
    code <- IO(ExitCode.Success)
    port <- backendPort
    host = backendURL
    transactor <- IO.delay(
      Transactor.fromDriverManager[IO](
        driver = "org.postgresql.Driver",
        url = f"$dbURL/$dbName",
        user = dbUsername,
        pass = dbPassword,
      )
    )
    botToken <- botToken
    api = TelegramAPI.http4sAPI(tkn = botToken)
    _ <- BlazeServerBuilder[IO]
      .bindHttp(port = port, host)
      .withHttpApp(new Server(transactor, api).app)
      .resource
      .use(_ => IO.never)
  } yield code

}
