package trash.markov
import cats.{Eq, Hash, Show}
import cats.syntax.show._
import cats.syntax.foldable._
import cats.effect.std.Random

object Markov extends App {
  type Weight = Int

  final case class Chain[T](
    graph: Map[T, Map[T, Weight]],
    prev: Option[T],
  ) {

    def add(cur: T): Chain[T] =
      this.prev match {
        case None => this.copy(prev = Some(cur))
        case Some(prev) =>
          Chain(
            prev = Some(cur),
            graph = this.graph.updatedWith(prev) {
              case None => Some(Map(cur -> 1))
              case Some(counts) =>
                Some(counts.updatedWith(cur) {
                  case None    => Some(1)
                  case Some(c) => Some(c + 1)
                })
            },
          )
      }

    def addAll(s: Seq[T]): Chain[T] = s.foldLeft(this)(_.add(_))

    def generate[F[_]](implicit rng: Random[F]): F[Option[Seq[T]]] = ???

  }

  object Chain {
    def empty[T]: Chain[T] = Chain(graph = Map(), prev = None)

    implicit def showChain[T: Show]: Show[Chain[T]] = new Show[Chain[T]] {
      override def show(t: Chain[T]): String = t.graph.foldLeft("") {
        case (acc, (k, vs)) =>
          acc ++ k.show ++
            " -> " ++
            vs.map { case (v, w) =>
              show"($v, weight=$w)"
            }.toList
              .intercalate(", ") ++
            "\n"
      }
    }
  }

  val words = Seq(
    Seq("I", "have", "has", "cats"),
    Seq("I", "have", "apples"),
    Seq("I", "like", "music"),
  )
  val chain = words.foldLeft(Chain.empty[String])(_.addAll(_))

  println(chain.show)

}
