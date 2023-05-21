import mill._, mill.scalalib._
import mill.scalajslib._, mill.scalajslib.api._
import scalafmt._
import coursier.maven.MavenRepository

import $ivy.`com.goyeau::mill-scalafix::0.2.11`
import com.goyeau.mill.scalafix.ScalafixModule
import $ivy.`io.github.davidgregory084::mill-tpolecat::0.3.1`
import io.github.davidgregory084.TpolecatModule

object libVersion {
  val scala           = "3.2.2"
  val scalajs         = "1.13.1"
  val organizeimports = "0.6.0"
  val scalajsdom      = "2.6.0"
  val scalatest       = "3.2.16"
  val riscvassembler  = "1.7.1"
  val laminar         = "15.0.1"
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
      ivy"org.scalatest::scalatest::${libVersion.scalatest}"
    )
    def jsEnvConfig = T(JsEnvConfig.JsDom())
  }
}

// -----------------------------------------------------------------------------
// Command Aliases
// -----------------------------------------------------------------------------
// Alias commands are run like `./mill run [alias]`
// Define the alias as a map element containing the alias name and a Seq with the tasks to be executed
val aliases: Map[String, Seq[String]] = Map(
  "fmt"      -> Seq("mill.scalalib.scalafmt.ScalafmtModule/reformatAll __.sources"),
  "checkfmt" -> Seq("mill.scalalib.scalafmt.ScalafmtModule/checkFormatAll __.sources"),
  "deps"     -> Seq("mill.scalalib.Dependency/showUpdates"),
  "testall"  -> Seq("__.test"),
)

def run(ev: eval.Evaluator, alias: String = "") = T.command {
  aliases.get(alias) match {
    case Some(t) =>
      mill.main.MainModule.evaluateTasks(ev, t.flatMap(x => Seq(x, "+")).flatMap(_.split("\\s+")).init, false)(identity)
    case None =>
      Console.err.println("Use './mill run [alias]'."); Console.out.println("Available aliases:")
      aliases.foreach(x => Console.out.println(s"${x._1.padTo(15, ' ')} - Commands: (${x._2.mkString(", ")})"));
      sys.exit(1)
  }
}
