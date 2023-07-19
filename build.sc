import mill._, mill.scalalib._
import mill.scalajslib._, mill.scalajslib.api._
import scalafmt._
import coursier.maven.MavenRepository

import $ivy.`com.goyeau::mill-scalafix::0.3.1`
import com.goyeau.mill.scalafix.ScalafixModule
import $ivy.`io.github.davidgregory084::mill-tpolecat::0.3.5`
import io.github.davidgregory084.TpolecatModule
import $ivy.`com.carlosedp::mill-aliases::0.3.0`
import com.carlosedp.aliases._

object libVersion {
  val scala          = "3.3.0"
  val scalajs        = "1.13.2"
  val scalajsdom     = "2.6.0"
  val scalatest      = "3.2.16"
  val riscvassembler = "1.9.0"
  val laminar        = "15.0.1"
}

// -----------------------------------------------------------------------------
// Projects
// -----------------------------------------------------------------------------

object rvasmweb extends ScalaJSModule with TpolecatModule with ScalafmtModule with ScalafixModule {
  def scalaVersion   = libVersion.scala
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

  object test extends ScalaJSTests with TestModule.ScalaTest {
    def ivyDeps = Agg(
      ivy"org.scalatest::scalatest::${libVersion.scalatest}"
    )
  }
}

object MyAliases extends Aliases {
  def fmt      = alias("mill.scalalib.scalafmt.ScalafmtModule/reformatAll __.sources")
  def checkfmt = alias("mill.scalalib.scalafmt.ScalafmtModule/checkFormatAll __.sources")
  def lint     = alias("mill.scalalib.scalafmt.ScalafmtModule/reformatAll __.sources", "__.fix")
  def deps     = alias("mill.scalalib.Dependency/showUpdates")
  def testall  = alias("__.test")

}
