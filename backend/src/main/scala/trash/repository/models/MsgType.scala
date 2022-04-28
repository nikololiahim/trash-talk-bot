package trash.repository.models

import scala.collection.immutable.SortedMap
import doobie.postgres.implicits.pgEnumStringOpt
import doobie.Meta

sealed trait MsgType

object MsgType {
  final case object TEXT extends MsgType

  final case object IMAGE extends MsgType

  final case object STICKER extends MsgType

  final case object VIDEO extends MsgType

  final case object DOC extends MsgType

  private[repository] val mapping = SortedMap(
    "TEXT"    -> TEXT,
    "IMAGE"   -> IMAGE,
    "STICKER" -> STICKER,
    "VIDEO"   -> VIDEO,
    "DOC"     -> DOC,
  )

  val msgTypes: List[MsgType] = mapping.values.toList

  def toEnum(t: MsgType): String           = t.toString
  def fromEnum(s: String): Option[MsgType] = MsgType.mapping.get(s)

  implicit val msgTypeMeta: Meta[MsgType] =
    pgEnumStringOpt("msg_type", fromEnum, toEnum)

}
