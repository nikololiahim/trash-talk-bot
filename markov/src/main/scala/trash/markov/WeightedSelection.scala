package trash.markov
import cats.data.NonEmptyVector
import cats.effect.ExitCode
import cats.effect.IO
import cats.effect.IOApp
import cats.effect.std.Random
import cats.syntax.functor._
import cats.syntax.traverse._

object WeightedSelection extends IOApp {

  override def run(args: List[String]): IO[ExitCode] = {

    val nums = Map('A' -> 2, 'B' -> 1, 'C' -> 16, 'D' -> 1).toList
    val a =
      List
        .from(1 to 1e6.toInt)
        .traverse(_ => select(Random.scalaUtilRandom[IO])(nums))
        .map(_.flatten.groupBy(identity).fmap(_.length))

    a.flatMap(IO.println) *> IO(ExitCode.Success)
  }

  def select[T](rng: IO[Random[IO]])(
    seq: Seq[(T, Int)]
  ): IO[Option[T]] = {
    val cumulativeWeights = seq.foldLeft(Vector.empty[(T, Int)]) {
      case (acc, (t, w)) =>
        acc.lastOption match {
          case None          => acc :+ (t, w)
          case Some((_, pw)) => acc :+ (t, pw + w)
        }
    }

    def findItem(seq: NonEmptyVector[(T, Int)], pivot: Int): T =
      seq.find { case (_, w) => w >= pivot }.map(_._1).get

    NonEmptyVector.fromVector(cumulativeWeights) match {
      case None => IO.pure(None)
      case Some(weights) =>
        rng
          .flatMap(rng =>
            rng
              .betweenInt(1, weights.last._2 + 1)
              .map(seed => Some(findItem(weights, seed)))
          )

    }

  }

}
