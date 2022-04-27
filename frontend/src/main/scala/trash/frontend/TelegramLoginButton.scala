package trash.frontend

import scalajs.js
import slinky.core.ExternalComponent
import slinky.core.annotations.react

import scala.scalajs.js.annotation.JSImport

sealed case class TelegramAuthData(
                                    id: Int,
                                    auth_date: Int,
                                    first_name: String,
                                    hash: String,
                                    last_name: String,
                                    photo_url: String,
                                    username: String,
                                  )

@JSImport("react-telegram-login", JSImport.Default)
@js.native
private object TelegramLoginButtonImport extends js.Object


@react object TelegramLoginButton extends ExternalComponent {
  case class Props(botName: String, dataOnauth: TelegramAuthData => Unit)
  override val component = TelegramLoginButtonImport
}
