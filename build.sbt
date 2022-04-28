import org.scalajs.jsenv.selenium.SeleniumJSEnv
import org.openqa.selenium.chrome.ChromeOptions

ThisBuild / scalaVersion := "2.13.8"
//ThisBuild / scapegoatVersion           := "1.4.12"
ThisBuild / version                    := "0.1.0-SNAPSHOT"
ThisBuild / semanticdbEnabled          := true
ThisBuild / semanticdbVersion          := scalafixSemanticdb.revision
ThisBuild / scalafixScalaBinaryVersion := "2.13"
ThisBuild / licenses                   := Seq(License.MIT)
//scapegoatReports                       := Seq("xml")

def seleniumConfig(
  port: Int
): SeleniumJSEnv.Config = {
  import _root_.io.github.bonigarcia.wdm.WebDriverManager
  val contentDir =
    file("frontend/.js/target/frontend-test-fastopt").getAbsolutePath()
  val webRoot = s"http://localhost:$port/.js/target/frontend-test-fastopt/"
  println("contentDir: " + contentDir)
  println("webRoot: " + webRoot)
  WebDriverManager.chromedriver().setup()
  SeleniumJSEnv
    .Config()
    .withMaterializeInServer(
      contentDir = contentDir,
      webRoot = webRoot,
    )
}

val githubWorkflowScalas = List("2.13.8")

val checkoutSetupJava = List(WorkflowStep.Checkout) ++
  WorkflowStep.SetupJava(List(JavaSpec.temurin("11")))

ThisBuild / githubWorkflowPublishTargetBranches := Seq()

ThisBuild / githubWorkflowBuildPreamble ++= Seq(
  WorkflowStep.Use(
    UseRef.Public("nanasess", "setup-chromedriver", "v1.0.1")
  ),
  WorkflowStep.Use(
    UseRef.Public("actions", "setup-node", "v3.0.0"),
    params = Map(
      "node-version"          -> "lts/gallium",
      "cache"                 -> "npm",
      "cache-dependency-path" -> "frontend/package-lock.json",
    ),
  ),
)

ThisBuild / githubWorkflowBuild := Seq(
  WorkflowStep.Run(
    List(
      """cd frontend && \
         npm install && \
         { npx vite -l silent --clearScreen false --port 3000 & } && \
         cd ..  && \
         sbt ++${{ matrix.scala }} test && \
         kill $(jobs -p)"""
    )
  )
)

ThisBuild / githubWorkflowAddedJobs ++= Seq(
  WorkflowJob(
    id = "scalafmt",
    name = "Format code with scalafmt",
    scalas = githubWorkflowScalas,
    steps = checkoutSetupJava ++
      githubWorkflowGeneratedCacheSteps.value ++
      List(
        WorkflowStep.Sbt(List("scalafmtCheckAll")),
        WorkflowStep.Sbt(List("scalafmtSbtCheck")),
      ),
  ),
  WorkflowJob(
    id = "scalafix",
    name = "Check code with scalafix",
    scalas = githubWorkflowScalas,
    steps = checkoutSetupJava ++
      githubWorkflowGeneratedCacheSteps.value ++
      List(WorkflowStep.Sbt(List("scalafixAll --check"))),
  ),
  WorkflowJob(
    id = "coverage",
    name = "Upload coverage report to Codecov",
    scalas = githubWorkflowScalas,
    steps = checkoutSetupJava ++
      githubWorkflowGeneratedCacheSteps.value ++
      List(
        // TODO: For now, coverage is collected only from 'bot' subproject.
        //       Figure out how to setup sbt-scoverage with scala-js (probably unreal)
        WorkflowStep.Sbt(
          List("project bot", "coverage", "test", "coverageReport")
        ),
        WorkflowStep.Run(
          List(
            "curl -Os https://uploader.codecov.io/latest/linux/codecov",
            "chmod +x codecov",
            "./codecov",
          )
        ),
      ),
  ),
)

lazy val core = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Pure)
  .in(file("core"))
  .enablePlugins(BuildInfoPlugin)
  .settings(Compiler.settings)
  .settings(
    name := "Core",
    buildInfoKeys := Seq[BuildInfoKey](
      name,
      version,
      scalaVersion,
      sbtVersion,
      "BACKEND_URL" -> sys.env.get("BACKEND_URL"),
    ),
    buildInfoPackage := "trash",
    libraryDependencies ++= Dependencies.core,
  )
  .settings(
    buildInfoKeys := Seq[BuildInfoKey](
      "BACKEND_URL" -> sys.env.get("BACKEND_URL")
    )
  )

lazy val backend = crossProject(JVMPlatform)
  .crossType(CrossType.Pure)
  .in(file("backend"))
  .dependsOn(core)
  .settings(Compiler.settings)
  .settings(
    name := "Trash Talk Backend",
    libraryDependencies ++= Dependencies.backend,
  )

lazy val bot = crossProject(JVMPlatform)
  .crossType(CrossType.Pure)
  .in(file("bot"))
  .dependsOn(core, backend)
  .aggregate(core, backend)
  .settings(Compiler.settings)
  .settings(
    name := "Trash Talk Telegram Bot",
    libraryDependencies ++= Dependencies.bot,
  )

lazy val frontend = crossProject(JSPlatform)
  .crossType(CrossType.Pure)
  .in(file("frontend"))
  .dependsOn(core)
  .enablePlugins(ScalaJSPlugin)
  .settings(Compiler.settings)
  .settings(
    scalaJSUseMainModuleInitializer := true,
    scalaJSLinkerConfig ~= {
      _.withModuleKind(ModuleKind.ESModule)
        .withSourceMap(false)
    },
    Test / jsEnv := {
      new SeleniumJSEnv(
        new ChromeOptions().setHeadless(true),
        seleniumConfig(
          port = if (githubIsWorkflowBuild.value) 3000 else 80
        ),
      )
    },
    libraryDependencies ++= Dependencies.frontend.value,
  )
