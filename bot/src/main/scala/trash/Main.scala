package trash

import cats.effect._
import cats.syntax.all._
import sttp.client3.asynchttpclient.cats.AsyncHttpClientCatsBackend
import doobie.util.transactor.Transactor
import trash.bot.Bot
import trash.core.repository.postgres
import core.Settings._

object Main extends IOApp {

  override def run(args: List[String]): IO[ExitCode] =
    for {
      token <- IO.fromOption(botToken)(
        new Exception(
          f"Telegram Bot API token is not set. " +
            s"Please set the \"$botEnvVarName\" environment variable" +
            " with your token value."
        )
      )
      backend <- AsyncHttpClientCatsBackend[IO]()
      xa <- IO.delay(
        Transactor.fromDriverManager[IO](
          driver = "org.postgresql.Driver",
          url = f"$dbURL/$dbName",
          user = dbUsername,
          pass = dbPassword,
        )
      )
      postgresRepo = postgres.PostgresTelegramMessageRepository(xa)
      bot <- IO.pure(new Bot(token, backend, postgresRepo))
      botResource = Resource.make(bot.run().start)(_ =>
        IO.blocking(bot.shutdown())
      )
      _ <- botResource.use(_ => IO.never)
    } yield ExitCode.Success

}
