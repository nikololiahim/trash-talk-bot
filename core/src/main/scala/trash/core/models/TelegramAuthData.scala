package trash.core.models

sealed case class TelegramAuthData(
  id: Int,
  auth_date: Int,
  first_name: String,
  hash: String,
  last_name: String,
  photo_url: String,
  username: String,
)
