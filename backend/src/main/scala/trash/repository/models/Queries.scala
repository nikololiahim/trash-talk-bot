package trash.repository.models

import doobie._
import doobie.postgres.implicits._
import doobie.util.transactor.Transactor

abstract class Queries[F[_]](xa: Transactor[F])

object Queries {

  def toEnum(t: MsgType): String           = t.toString
  def fromEnum(s: String): Option[MsgType] = MsgType.mapping.get(s)

  implicit val msgTypeMeta: Meta[MsgType] =
    pgEnumStringOpt("msg_type", fromEnum, toEnum)
}
