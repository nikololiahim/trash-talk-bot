package trash.markov
import cats.effect.std.Random
import cats.data.NonEmptyVector
import cats.Applicative
import cats.kernel.Order
import cats.kernel.Comparison._
import cats.syntax.functor._
import cats.syntax.foldable._
import cats.syntax.traverse._
import cats.effect.IOApp
import cats.effect.{ExitCode, IO}

object WeightedSelection extends IOApp {

  override def run(args: List[String]): IO[ExitCode] = {

    val nums = Map('A' -> 2, 'B' -> 1, 'C' -> 16, 'D' -> 1).toList
    val a = Random
      .scalaUtilRandom[IO]
      .flatMap(implicit rng =>
        List.from(1 to 1e6.toInt).traverse(_ => select(nums))
      )
      .map(_.flatten.groupBy(identity).fmap(_.length))

    a.flatMap(IO.println) *> IO(ExitCode.Success)
  }

  def select[T, F[_]](
    seq: Seq[(T, Int)]
  )(implicit rng: Random[F], F: Applicative[F]): F[Option[T]] = {
    val cumulativeWeights = seq.foldLeft(Vector.empty[(T, Int)]) {
      case (acc, (t, w)) =>
        acc.lastOption match {
          case None          => acc :+ (t, w)
          case Some((_, pw)) => acc :+ (t, pw + w)
        }
    }

    def bisect[T](seq: NonEmptyVector[(T, Int)], pivot: Int): T =
      seq.find { case (_, w) => w >= pivot }.map(_._1).get

    NonEmptyVector.fromVector(cumulativeWeights) match {
      case None => F.pure(None)
      case Some(weights) =>
        rng
          .betweenInt(1, weights.last._2 + 1)
          .map(seed => Some(bisect(weights, seed)))

    }

  }

}
