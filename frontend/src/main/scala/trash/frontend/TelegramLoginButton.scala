package trash.frontend

import scalajs.js
import slinky.core.ExternalComponent
import slinky.core.annotations.react

import scala.scalajs.js.annotation.JSImport


@JSImport("react-telegram-login", JSImport.Default)
@js.native
private object TelegramLoginButtonImport extends js.Object


@react object TelegramLoginButton extends ExternalComponent {
  case class Props(botName: String, dataOnauth: js.Dynamic => Unit)
  override val component = TelegramLoginButtonImport
}