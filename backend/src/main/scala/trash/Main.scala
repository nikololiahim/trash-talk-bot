package trash

import cats.effect._
import cats.implicits._
import doobie.util.transactor.Transactor
import io.circe.generic.auto._
import io.circe.syntax._
import org.http4s._
import org.http4s.blaze.server.BlazeServerBuilder
import org.http4s.circe._
import org.http4s.dsl._
import org.http4s.implicits._
import org.http4s.server._
import trash.core.Settings.{backendPort, backendURL, dbName, dbPassword, dbURL, dbUsername}
import trash.utils.postgres.BackendQueries
import trash.repository.Server

object Main extends IOApp {

  override def run(args: List[String]): IO[ExitCode] = for {
    code <- IO(ExitCode.Success)
    port <- backendPort
    host =  backendURL
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
