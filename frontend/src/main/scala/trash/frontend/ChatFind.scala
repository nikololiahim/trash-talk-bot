package trash.frontend

import org.scalajs.dom.console
import org.scalajs.dom.html.Input
import slinky.core.annotations.react
import slinky.core.{FunctionalComponent, SyntheticEvent, TagElement}
import slinky.core.facade.Hooks.useState
import slinky.core.facade.SetStateHookCallback
import slinky.web.html._
import trash.frontend.ConsoleAmogus.console2amogus

import scala.util.{Failure, Success, Try}

@react object ChatFind {
  Css.App

  case class Props(setChatId: SetStateHookCallback[Int])

  val component: FunctionalComponent[Props] =
    FunctionalComponent[Props] { props =>
      val (inputVal, setInputVal)     = useState(0)
      val (errorState, setErrorState) = useState(false)

      def handleChange(inp: Input): Unit =
        inp.value.toIntOption match {
          case None =>
            setErrorState(true)
          case Some(value) =>
            setInputVal(value)
            setErrorState(false)
        }

      def handleClick(): Unit = props.setChatId(inputVal)

      div(
        div(
          className := "form__group field",
          input(
            className := (if (errorState) "form__field error_input"
                          else "form__field"),
            onChange := (event =>
              handleChange(event.target.asInstanceOf[Input])
            ),
            placeholder := "ChatID",
            name        := "chatID",
            id          := "chatID",
            required,
          ),
          label(
            htmlFor   := "chatID",
            className := "form__label",
            "ChatID",
          ),
        ),
        button(
          className := "button-6",
          onClick   := (_ => handleClick()),
        )("Перейти к сообщениям"),
      )
    }

}
//<div class="form__group field">
//<input type="input" class="form__field" placeholder="Name" name="name" id='name' required />
//<label for="name" class="form__label">Name</label>
//</div>
