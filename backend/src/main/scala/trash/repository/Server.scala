package trash.repository

import cats.data.{Kleisli, OptionT}
import cats.effect.{Async, MonadCancelThrow}
import cats.syntax.flatMap._
import cats.syntax.functor._
import doobie.util.transactor.Transactor
import io.circe.jawn.decode
import io.circe.generic.auto._
import io.circe.syntax._
import org.http4s._
import org.http4s.dsl.io._
import org.http4s.circe._
import org.http4s.implicits._
import org.http4s.server._
import trash.utils.postgres.BackendQueries
import trash.core.models.TelegramAuthData
import org.http4s.headers.Authorization
import cats.syntax.either._
import org.http4s.dsl.Http4sDsl

class Server[F[_]](xa: Transactor[F], API: TelegramAPI[F])(implicit
  F: Async[F]
) {

  def validateAuthData(
    auth: TelegramAuthData
  ): Either[String, TelegramAuthData] = Right(auth)
//      .toRight("Auth data expired.")

  def authUser: Kleisli[F, Request[F], Either[String, TelegramAuthData]] =
    Kleisli { request: Request[F] =>
      F.delay {
        println(request.headers.headers.head.value)
        request.headers.headers.headOption match {
//        request.headers.get[Authorization] match {
          case Some(auth) =>
            for {
              authData <- decode[TelegramAuthData](auth.value).leftMap(
                _.toString
              )
              checkedData <- validateAuthData(authData)
            } yield checkedData
          case None => Left("Couldn't find an Authorization header")
        }
      }
    }

  def onAuthFailure: AuthedRoutes[String, F] =
    Kleisli { req: AuthedRequest[F, String] =>
      req.req match {
        case _ =>
          OptionT.pure[F](
            Response[F](
              status = Unauthorized
            )
          )
      }
    }

  def authMiddleware =
    AuthMiddleware[F, String, TelegramAuthData](authUser, onAuthFailure)

  def routes: AuthedRoutes[TelegramAuthData, F] = {
    val dsl = Http4sDsl[F]
    val sql = new BackendQueries(xa)
    import dsl._
    AuthedRoutes.of {
      case GET -> Root / "name" as _ =>
        for {
          name     <- API.getBotName()
          response <- Ok(name)
        } yield response

      case GET -> Root / "pidors" / chatID as _ =>
        chatID.toLongOption
          .map { id =>
            for {
              messages <- API.getChatAdministrators(id)
              response <- Ok(messages.asJson)
            } yield response
          }
          .getOrElse(BadRequest(s"Chat with id \"$chatID\" does not exist"))

      case GET -> Root / "ids" as _ =>
        for {
          messages <- sql.availableChatIds
          response <- Ok(messages.asJson)
        } yield response

      case GET -> Root / chatId as _ =>
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
    Router("/" -> authMiddleware.apply(routes)).orNotFound
}
