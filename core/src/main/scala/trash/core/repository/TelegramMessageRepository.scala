package trash.core.repository

import com.bot4s.telegram.models.Message
import trash.core.models.DBMessage

trait TelegramMessageRepository[F[_]] {
  def getChatMessages(chatId: Long): F[List[DBMessage]]
  def getRandomMessage(chatId: Long): F[List[DBMessage]]
  def insertMessage(msg: Message): F[Unit]
}
