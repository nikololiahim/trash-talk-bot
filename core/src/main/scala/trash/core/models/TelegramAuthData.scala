package trash.core.models
import io.circe.generic.semiauto._

sealed case class TelegramAuthData(
  id: Int,
  auth_date: Int,
  first_name: String,
  hash: String,
  last_name: String,
  photo_url: String,
  username: String,
)

object TelegramAuthData{
  implicit val encoder = deriveEncoder[TelegramAuthData]
  implicit val decoder = deriveDecoder[TelegramAuthData]
}