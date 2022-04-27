package trash.bot

import cats.effect._
import cats.syntax.all._
import com.bot4s.telegram.api.declarative.Commands
import com.bot4s.telegram.cats.{Polling, TelegramBot}
import com.bot4s.telegram.methods._
import com.bot4s.telegram.models._
import doobie.implicits._
import sttp.client3.SttpBackend
import trash.core.models.MsgType
import trash.core.repository.TelegramMessageRepository

class Bot[F[_]: Async](
  token: String,
  backend: SttpBackend[F, Any],
  repo: TelegramMessageRepository[F],
  telegramApi: String = "api.telegram.org",
) extends TelegramBot[F](token, backend, telegramApi)
  with Polling[F]
  with Commands[F] {

  val botUser: F[User] = request(GetMe)

  def generateMessage(chatId: Long): F[Unit] =
    repo
      .getRandomMessage(chatId)
      .flatMap { list =>
        list.headOption match {
          case Some(msg) =>
            val content = msg.msgType match {
              case MsgType.TEXT  => SendMessage(chatId, msg.content)
              case MsgType.IMAGE => SendPhoto(chatId, InputFile(msg.content))
              case MsgType.STICKER =>
                SendSticker(chatId, InputFile(msg.content))
              case MsgType.VIDEO => SendVideo(chatId, InputFile(msg.content))
              case MsgType.DOC   => SendDocument(chatId, InputFile(msg.content))
            }
            request(content).void
          case None =>
            ().pure[F]
        }
      }

  override def receiveMessage(msg: Message): F[Unit] =
    (msg.newChatMembers, msg.leftChatMember) match {
      case (Some(_), None) =>
            Async[F].delay(())
//        botUser.flatMap { me =>
//          if (addedUsers.contains(me)) {
//            val req = SendMessage(msg.chat.chatId, f"Я ЩА ВСЁ ПРО ВАС УЗНАЮ")
//            val debilReq = request(GetChatAdministrators(msg.chat.id)).flatMap {
//              debils =>
//                request(
//                  SendMessage(msg.chat.chatId, f"Список пидарасов:\n$debils")
//                )
//            }
//
//
//          } else {
//            val req = SendMessage(msg.chat.chatId, f"ПРИВЕТ ДИБИЛАМ")
//            request(req).void
//          }
//        }

      case (None, Some(removedUser)) =>
        botUser.flatMap { me =>
          if (removedUser == me) {
            logger.info("SAD")
          } else {
//            val req = SendMessage(msg.chat.chatId, f"ПОКА ДИБИЛУ $removedUser")
//            request(req).void
          }
          Async[F].delay(())

        }

      case _ =>
        for {
          _ <- Async[F].delay(logger.info(msg.toString))
          _ <- repo.insertMessage(msg)
          _ <- generateMessage(msg.chat.id)
        } yield ()
    }

}
