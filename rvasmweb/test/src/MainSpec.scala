package com.carlosedp
package rvasmweb

import com.raquo.laminar.api.L.render
import org.scalajs.dom.*
import org.scalajs.dom.html.*
import org.scalatest.*
import org.scalatest.flatspec.*
import org.scalatest.matchers.*

class MainSpec extends AnyFlatSpec with should.Matchers {
  // Initialize Test for elements we don't create in Scala.js (exists in index.html)
  val appDiv = document.createElement("div")
  appDiv.id = "app"
  document.body.appendChild(appDiv)
  // Initialize App
  render(appDiv, MainApp.setupUI())

  behavior of "Main App"

  it should "contain a button in its body" in {
    document.querySelectorAll("button").count(_.textContent.contains("Assemble")) should be(1)
  }

  it should "contain an input textarea in its body" in {
    document.getElementById("asminput") should not be null
  }

  it should "contain an output textarea in its body" in {
    document.getElementById("hexoutput") should not be null
  }

  it should "assemble one RISC-V instruction" in {
    val in     = document.getElementById("asminput").asInstanceOf[TextArea]
    val out    = document.getElementById("hexoutput").asInstanceOf[TextArea]
    val button = document.querySelector("button").asInstanceOf[html.Button]
    in.value = "addi x0, x1, 100"
    button.click()
    out.value should be("06408013")
  }

  it should "assemble multiple RISC-V instructions" in {
    val in     = document.getElementById("asminput").asInstanceOf[TextArea]
    val out    = document.getElementById("hexoutput").asInstanceOf[TextArea]
    val button = document.querySelector("button").asInstanceOf[html.Button]
    in.value = """|    addi x0, x1, 100
                  |    beq x1, x2, l1
                  |l1: and x1, x2, x3
      """.stripMargin
    button.click()
    out.value should be("""|06408013
                           |00208263
                           |003170B3
      """.stripMargin.trim)
  }

  it should "assemble multiple RISC-V instructions with some errors" in {
    val in     = document.getElementById("asminput").asInstanceOf[TextArea]
    val out    = document.getElementById("hexoutput").asInstanceOf[TextArea]
    val button = document.querySelector("button").asInstanceOf[html.Button]
    in.value = """|
                  |addi x0, x1, 100
                  |bogus
                  |j 128
    """.stripMargin
    button.click()
    out.value should be("""|06408013
                           |00000000
                           |0800006F
    """.stripMargin.trim)
  }

}
