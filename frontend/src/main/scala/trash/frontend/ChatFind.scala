package trash.frontend

import org.scalajs.dom.console
import slinky.core.annotations.react
import slinky.core.{FunctionalComponent, SyntheticEvent, TagElement}
import slinky.core.facade.Hooks.useState
import slinky.core.facade.SetStateHookCallback
import slinky.web.html._

import scala.util.{Failure, Success, Try}

// TODO: make it actually work
@react object ChatFind {
  case class Props(setChatId: SetStateHookCallback[Int])

  val component: FunctionalComponent[Props] =
    FunctionalComponent[Props] { props =>
      val (inputVal, setInputVal)     = useState(0)
      val (errorState, setErrorState) = useState(false)

//      def handleChange(e: Input): Unit = Try(e.value.toInt) match {
//        case Failure(_) => setErrorState(true)
//        case Success(value) =>
//          setInputVal(value)
//          setErrorState(false)
//      }

      def handleClick(): Unit = props.setChatId(inputVal)

      div(
        div(
          className := "form__group field",
          input(
            className := (if (errorState) "form__field error_input"
                          else "form__field"),
            onChange    := (event => console.log(event)),
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
