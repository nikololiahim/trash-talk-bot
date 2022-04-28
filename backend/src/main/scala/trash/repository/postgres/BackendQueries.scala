package trash.utils.postgres

import com.bot4s.telegram.models.Message
import cats.effect.kernel.MonadCancelThrow
import doobie.util.transactor.Transactor
import doobie._
import doobie.implicits._
import doobie.postgres.implicits._
import cats.syntax.all._
import trash.repository.models.{DBMessage, Queries}
import trash.repository.models.Queries._

class BackendQueries[F[_]: MonadCancelThrow](
                                           xa: Transactor[F]
                                         ) extends Queries[F](xa){
  def availableChatIds(): F[List[Long]] =
    sql"""
      SELECT DISTINCT chat_id FROM message
       """.query[Long].to[List].transact(xa)

  def getChatMessages(chatId: Long): F[List[DBMessage]] =
    sql"""
      SELECT chat_id, message_id, type, content FROM message
      WHERE chat_id = $chatId
       """.query[DBMessage].to[List].transact(xa)
}