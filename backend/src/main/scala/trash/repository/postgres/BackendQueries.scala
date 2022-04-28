package trash.utils.postgres

import cats.effect.MonadCancelThrow
import doobie.util.transactor.Transactor
import doobie.implicits._
import trash.repository.models.DBMessage

class BackendQueries[F[_]](xa: Transactor[F])(implicit F: MonadCancelThrow[F]) {
  def availableChatIds: F[List[Long]] =
    sql"""
      SELECT DISTINCT chat_id FROM message
       """.query[Long].to[List].transact(xa)

  def getChatMessages(chatId: Long): F[List[DBMessage]] =
    sql"""
      SELECT chat_id, message_id, type, content FROM message
      WHERE chat_id = $chatId
       """.query[DBMessage].to[List].transact(xa)
}
