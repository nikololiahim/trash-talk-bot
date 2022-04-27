package trash.frontend

import org.scalajs.dom
import slinky.core.FunctionalComponent
import slinky.core.facade.Hooks._
import slinky.web.html._
import org.scalajs.dom.console
import trash.core.models.TelegramAuthData
import trash.frontend.MessageList.MessageListProps

import scala.scalajs.js
import scala.scalajs.js.Object.create

object App {
  private val css = Css.App

  val App: FunctionalComponent[String] = FunctionalComponent[String] { botName =>
    val (chatID, setChatID) = useState(0)

    def callBack(aboba: TelegramAuthData): Unit = {
      console.log(aboba)
    }

    div(
      className := "App",
      div(
        className := "center",
        ChatFind(setChatID),
        MessageList.MessageList(MessageListProps(chatID)),
      ),
      div(TelegramLoginButton(botName=botName, dataOnauth = callBack))
    )
  }
}