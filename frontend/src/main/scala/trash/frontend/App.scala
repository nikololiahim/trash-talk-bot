package trash.frontend

import org.scalajs.dom
import slinky.core.FunctionalComponent
import slinky.core.facade.Hooks._
import slinky.web.html._
import org.scalajs.dom.console

import scala.scalajs.js
import scala.scalajs.js.Object.create

object App {
  private val css = Css.App

  val App: FunctionalComponent[Unit] = FunctionalComponent[Unit] { props =>
    val (chatID, setChatID) = useState(0)

    def kostil(aboba: js.Dynamic): Unit = {
      console.log(aboba)
    }

    div(
      div(span("Yes")),
      div(TelegramLoginButton(botName="debil_inno_bot", dataOnauth = kostil)),
      div("Of course")
    )
  }
}