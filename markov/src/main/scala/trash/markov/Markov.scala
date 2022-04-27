package trash.markov
import cats.Show
import cats.effect.IO
import cats.effect.std.Random
import cats.syntax.foldable._
import cats.syntax.show._
import cats.effect.IOApp
import cats.effect.ExitCode

object Markov extends IOApp {

  override def run(args: List[String]): IO[ExitCode] = {

    val words = Seq(
      Seq("I", "have", "has", "cats"),
      Seq("John", "has", "apples"),
      Seq("Garfield", "likes", "lasagna"),
    )
    val chain = words.foldLeft(Chain.empty[String])(_.addAll(_))

    for {
      _         <- IO.println(chain.show)
      generated <- chain.generate(Random.scalaUtilRandom[IO])(maxSize = 10)
      _         <- IO.println(generated)
    } yield ExitCode.Success
  }

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

    def generate(
      rng: IO[Random[IO]]
    )(maxSize: Int, maxAttempts: Int = 10): IO[Seq[T]] = {

      def walk(start: T, acc: Seq[T]): IO[Seq[T]] =
        this.graph.get(start) match {
          case None => IO.pure(acc)
          case Some(nextStates) =>
            WeightedSelection.select(rng)(nextStates.toList).flatMap {
              case None => IO.pure(acc)
              case Some(nextState) =>
                if (acc.length < maxSize)
                  walk(nextState, acc :+ nextState)
                else
                  IO.pure(acc)
            }
        }

      def recurse(attempt: Int, lastAttempt: Seq[T]): IO[Seq[T]] = {
        val keys = this.graph.keySet.toVector
        if (keys.isEmpty) {
          IO.pure(Vector())
        } else if (attempt == maxAttempts) {
          IO.pure(lastAttempt)
        } else {
          val startRandom =
            rng.flatMap(rng => rng.betweenInt(0, graph.size).map(keys(_)))
          for {
            start          <- startRandom
            initialAttempt <- walk(start, Vector())
            nextAttempt <-
              if (initialAttempt.length == maxSize) {
                IO.pure(initialAttempt)
              } else {
                recurse(attempt + 1, initialAttempt)
              }
          } yield nextAttempt
        }
      }

      recurse(1, lastAttempt = Vector())
    }

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

}
