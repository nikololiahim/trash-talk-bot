package trash.repository

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

class Server[F[_]: Async](xa: Transactor[F]) {

  def routes: HttpRoutes[F] = {
    val dsl = Http4sDsl[F]
    val sql = new BackendQueries(xa)
    import dsl._
    HttpRoutes.of[F] {
      case GET -> Root / "ids" =>
        for {
          messages <- sql.availableChatIds()
          response <- Ok(messages.asJson)
        } yield response

      case GET -> Root / chatId =>
        chatId.toLongOption
          .map { id =>
            for {
              messages <- sql.getChatMessages(id)
              response <- Ok(messages.asJson)
            } yield response
          }
          .getOrElse(BadRequest(s"Chat with id \"$chatId\" does not exist"))
    }
  }

  def app: HttpApp[F] =
    Router(
      "/" -> routes
    ).orNotFound

}