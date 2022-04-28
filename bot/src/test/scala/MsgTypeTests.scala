import trash.repository.models.MsgType
import MsgType._

class MsgTypeTests extends munit.FunSuite {
  val msgTypeTestCases: Map[MsgType, String] = Map(
    TEXT    -> "TEXT",
    VIDEO   -> "VIDEO",
    DOC     -> "DOC",
    STICKER -> "STICKER",
    IMAGE   -> "IMAGE",
  )

  msgTypeTestCases.foreach { case (msgType, str) =>
    test(s"converts $msgType to string") {
      assertEquals(toEnum(msgType), str)
    }

    test(s"converts string to $msgType") {
      assertEquals(fromEnum(str), Some(msgType))
    }
  }

  test("MsgType.toEnum returns None when given an invalid string") {
    assertEquals(fromEnum("ABOBA"), None)
  }

}
