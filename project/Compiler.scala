import sbt.Keys._
import sbt._

object Compiler {

  val options = Seq(
    "-deprecation", // Emit warning and location for usages of deprecated APIs.
    "-encoding",
    "utf-8",         // Specify character encoding used by source files.
    "-explaintypes", // Explain type errors in more detail.
    "-feature",
    /* Emit warning and location for usages of features that should be imported
     * explicitly. */
    "-unchecked", // Enable additional warnings where generated code depends on assumptions.
    "-Wunused",                     // Enable compiler warnings.
    "-Xlint:-byname-implicit",      // Enable compiler linter warnings.
    "-Ymacro-annotations",          // To support the @react macro
    "-language:implicitConversions",// Enable implicit conversions
  )

  val settings = Seq(
    scalacOptions ++= options,
    Test / console / scalacOptions := (Compile / console / scalacOptions).value,
  )

}
