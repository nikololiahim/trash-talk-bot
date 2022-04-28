package trash

import cats.effect._
import sttp.client3.asynchttpclient.cats.AsyncHttpClientCatsBackend
import doobie.util.transactor.Transactor
import trash.bot.Bot
import trash.bot.DBQueries
import core.Settings._

object Main extends IOApp {

  override def run(args: List[String]): IO[ExitCode] = {
    for {
      token <- botToken
      backend <- AsyncHttpClientCatsBackend[IO]()
      xa <- IO.delay(
        Transactor.fromDriverManager[IO](
          driver = "org.postgresql.Driver",
          url = f"$dbURL/$dbName",
          user = dbUsername,
          pass = dbPassword,
        )
      )
      queries = new DBQueries(xa)
      bot <- IO.pure(new Bot(token, backend, queries))
      botResource = Resource.make(bot.run().start)(_ =>
        IO.blocking(bot.shutdown())
      )
      _ <- botResource.use(_ => IO.never)
    } yield ExitCode.Success
  }

}
