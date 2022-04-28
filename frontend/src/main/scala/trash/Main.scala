package trash

import scala.scalajs.js.annotation.JSExportTopLevel
import scala.scalajs.LinkingInfo
import slinky.core._
import slinky.web.ReactDOM
import slinky.hot
import trash.frontend.App._
import org.scalajs.dom
import trash.frontend.ConsoleAmogus._
import trash.frontend.{AmogusImport, Css}
import trash.BuildInfo

// TODO: import botName from settings
// TODO: import telegram auth data
object Main {
  Css.Index
  AmogusImport

  @JSExportTopLevel("main")
  def main(args: Array[String]): Unit = {
    if (LinkingInfo.developmentMode) {
      hot.initialize()
    }

    val container = Option(dom.document.getElementById("root")).getOrElse {
      val elem = dom.document.createElement("div")
      elem.id = "root"
      dom.document.body.appendChild(elem)
      elem
    }

    dom.console.amogus("@gosha2st", 0, true)
    ReactDOM.render(App("debil_inno_bot"), container)
  }
}
