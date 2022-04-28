package trash

import cats.effect._
import doobie.util.transactor.Transactor
import org.http4s.blaze.server.BlazeServerBuilder
import trash.core.Settings.backendPort
import trash.core.Settings.backendURL
import trash.core.Settings.dbName
import trash.core.Settings.dbPassword
import trash.core.Settings.dbURL
import trash.core.Settings.dbUsername
import trash.repository.Server

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
    _ <- BlazeServerBuilder[IO]
      .bindHttp(port = port, host)
      .withHttpApp(new Server(transactor).app)
      .resource
      .use(_ => IO.never)
  } yield code

}
