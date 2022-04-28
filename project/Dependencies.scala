import sbt._
import org.portablescala.sbtplatformdeps.PlatformDepsPlugin.autoImport._

object Dependencies {
  object V {
    val cats           = "3.3.11"
    val slinky         = "0.7.2"
    val http4s         = "0.23.11"
    val doobie         = "1.0.0-RC1"
    val bot4s          = "5.4.1"
    val circe          = "0.14.1"
    val log4cats       = "2.2.0"
    val catsEffectSttp = "3.4.2"
    val munit          = "1.0.0-M3"
  }

  val core = Seq(
    "org.typelevel" %% "cats-effect" % V.cats
  )

  val test = Seq(
    "org.scalameta" %% "munit" % V.munit % Test
  )

  val frontend = Def.setting(
    Seq(
      "org.scalameta" %%% "munit"       % V.munit % Test,
      "me.shadaj"     %%% "slinky-core" % V.slinky,
      "me.shadaj"     %%% "slinky-web"  % V.slinky,
      "me.shadaj"     %%% "slinky-hot"  % V.slinky,
      "org.typelevel" %%% "cats-effect" % V.cats,
    )
  )

  val bot = test ++ Seq(
    "org.typelevel" %% "log4cats-slf4j" % V.log4cats,
    "com.softwaremill.sttp.client3" %% "async-http-client-backend-cats" % V.catsEffectSttp,
  )

  val backend = test ++ Seq(
    "com.bot4s"    %% "telegram-core"       % V.bot4s,
    "org.tpolecat" %% "doobie-core"         % V.doobie,
    "org.tpolecat" %% "doobie-postgres"     % V.doobie,
    "org.tpolecat" %% "doobie-hikari"       % V.doobie,
    "org.http4s"   %% "http4s-dsl"          % V.http4s,
    "org.http4s"   %% "http4s-blaze-server" % V.http4s,
    "org.http4s"   %% "http4s-circe"        % V.http4s,
    "io.circe"     %% "circe-generic"       % V.circe,
  )
}
