package trash.repository

import cats.effect.IO
import io.circe.generic.semiauto.deriveDecoder
import org.http4s.Uri
import org.http4s.circe.jsonOf
import org.http4s.dsl.io._
import org.http4s.ember.client.EmberClientBuilder
import org.http4s.implicits._
import org.http4s.client.dsl.io._

trait TelegramAPI[F[_]] {
  val token: String

  def getBotName(): F[String]
  def getChatAdministrators(chatId: Long): F[Seq[Long]]
}

object TelegramAPI {
  case class User(
    id: Long,
    username: String,
  )
  case class Cock(
    user: User
  )
  case class Wrapper[A](
    ok: Boolean,
    result: A,
  )

  def http4sAPI(tkn: String) = new TelegramAPI[IO] {
    override val token: String = tkn

    implicit val circeDecoder2  = deriveDecoder[User]
    implicit val circeDecoder4  = deriveDecoder[Cock]
    implicit val circeDecoder3  = deriveDecoder[Wrapper[User]]
    implicit val circeDecoder1  = deriveDecoder[Wrapper[Seq[Cock]]]
    implicit val http4sDecoder3 = jsonOf[IO, Cock]
    implicit val http4sDecoder2 = jsonOf[IO, Wrapper[User]]
    implicit val http4sDecoder1 = jsonOf[IO, Wrapper[Seq[Cock]]]

    override def getBotName(): IO[String] = {

      val req = GET(
        Uri.fromString(f"https://api.telegram.org/bot$token/getMe").toOption.get
      )

      EmberClientBuilder.default[IO].build.use { client =>
        client
          .expect(req)(jsonOf[IO, Wrapper[User]])
          .map(_.result.username)
      }
    }

    override def getChatAdministrators(
      chatId: Long
    ): IO[Seq[Long]] = {

      val req = GET(
        Uri
          .fromString(
            f"https://api.telegram.org/bot$token/getChatAdministrators?chat_id=$chatId"
          )
          .toOption
          .get
      )

      EmberClientBuilder.default[IO].build.use { client =>
        client
          .expect(req)(jsonOf[IO, Wrapper[Seq[Cock]]])
          .map(_.result.map(_.user.id))
      }
    }
  }

}
