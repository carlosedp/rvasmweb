import mill._, mill.scalalib._
import mill.scalajslib._, mill.scalajslib.api._
import scalafmt._
import coursier.maven.MavenRepository

import $ivy.`com.lihaoyi::mill-contrib-docker:$MILL_VERSION`
import contrib.docker.DockerModule
import $ivy.`com.goyeau::mill-scalafix::0.2.10`
import com.goyeau.mill.scalafix.ScalafixModule
import $ivy.`io.github.davidgregory084::mill-tpolecat::0.3.1`
import io.github.davidgregory084.TpolecatModule

object libVersion {
  val scala           = "3.2.0"
  val scalajs         = "1.11.0"
  val organizeimports = "0.6.0"
  val scalajsdom      = "2.3.0"
  val scalatest       = "3.2.14"
  val riscvassembler  = "1.6.0"
  val laminar         = "0.14.5"
}

trait Base extends ScalaModule with TpolecatModule with ScalafmtModule with ScalafixModule {
  override def scalaVersion = libVersion.scala
  def scalafixIvyDeps       = Agg(ivy"com.github.liancheng::organize-imports:${libVersion.organizeimports}")
  def repositoriesTask = T.task {
    super.repositoriesTask() ++ Seq("oss", "s01.oss")
      .map(r => s"https://$r.sonatype.org/content/repositories/snapshots")
      .map(MavenRepository(_))
  }
}

// -----------------------------------------------------------------------------
// Projects
// -----------------------------------------------------------------------------

object rvasmweb extends ScalaJSModule with Base {
  def scalaJSVersion = libVersion.scalajs
  def ivyDeps = super.ivyDeps() ++ Agg(
    ivy"org.scala-js::scalajs-dom::${libVersion.scalajsdom}",
    ivy"com.raquo::laminar::${libVersion.laminar}",
    ivy"com.carlosedp::riscvassembler::${libVersion.riscvassembler}",
  )

  def scalaJSUseMainModuleInitializer = true
  def moduleKind                      = T(ModuleKind.ESModule)
  def moduleSplitStyle                = T(ModuleSplitStyle.SmallModulesFor(List("com.carlosedp.rvasmweb")))

  // These two tasks are used by Vite to get update path
  def fastLinkOut() = T.command {
    val target = fastLinkJS()
    println(target.dest.path)
  }
  def fullLinkOut() = T.command {
    val target = fullLinkJS()
    println(target.dest.path)
  }

  object test extends Tests with Base with TestModule.ScalaTest {
    // Test dependencies
    def ivyDeps = Agg(
      ivy"org.scalatest::scalatest::${libVersion.scalatest}",
    )
    def jsEnvConfig = T(JsEnvConfig.JsDom())
  }
}

// -----------------------------------------------------------------------------
// Global commands
// -----------------------------------------------------------------------------
def runTasks(t: Seq[String])(implicit ev: eval.Evaluator) = T.task {
  mill.main.MainModule.evaluateTasks(
    ev,
    t.flatMap(x => x +: Seq("+")).flatMap(x => x.split(" ")).dropRight(1),
    mill.define.SelectMode.Separated,
  )(identity)
}
def lint(implicit ev: eval.Evaluator) = T.command {
  runTasks(Seq("__.fix", "mill.scalalib.scalafmt.ScalafmtModule/reformatAll __.sources"))
}
def deps(ev: eval.Evaluator) = T.command {
  mill.scalalib.Dependency.showUpdates(ev)
}
