package trash.bot

import com.bot4s.telegram.models.Message
import cats.effect.kernel.MonadCancelThrow
import doobie.util.transactor.Transactor
import doobie._
import doobie.implicits._
import doobie.postgres.implicits._
import cats.syntax.all._
import cats._
import trash.repository.models.{DBMessage, Queries}
import trash.repository.models.Queries._
import trash.repository.models.Queries

class DBQueries[F[_]: MonadCancelThrow](
  xa: Transactor[F]
) extends Queries[F](xa) {

  def getRandomMessage(chatId: Long): F[List[DBMessage]] =
    sql"""
      SELECT chat_id, message_id, type, content FROM message
      WHERE chat_id = $chatId
      ORDER BY RANDOM()
      LIMIT 1
       """.query[DBMessage].to[List].transact(xa)

  def saveMessage(msg: Message): F[Unit] =
    DBMessage
      .from(msg)
      .map { m =>
        val updateChat = sql"""
          INSERT INTO chat (chat_id) values (${m.chatId}) ON CONFLICT DO NOTHING;
        """.update.run

        val updateMessages = sql"""
          INSERT INTO message (message_id, chat_id, content, type) 
          VALUES (${m.messageId}, ${m.chatId}, ${m.content}, ${m.msgType.toString}::msg_type);
        """.update.run.void

        (updateChat *> updateMessages)
          .transact(xa)
          .void
      }
      .getOrElse(
        Applicative[F].unit
      )

}
