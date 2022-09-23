package com.carlosedp
package rvasmweb

import scala.scalajs.js.annotation.JSExportTopLevel

import com.carlosedp.riscvassembler.RISCVAssembler
import org.scalajs.dom.*
import org.scalajs.dom.html.*

object MainApp:

  def main(args: Array[String]): Unit =
    document.addEventListener("DOMContentLoaded", (e: Event) => setupUI())

  def setupUI() =
    val appnode = document.getElementById("app")
    addNode(appnode, "Scala.js RISC-V Assembler!", "h2")

    // Create input textarea
    addNode(appnode, "Assembly Input:", "p")
    val asminput = document.createElement("textarea").asInstanceOf[TextArea]
    asminput.id = "asminput"
    asminput.className = "pre .code"
    asminput.spellcheck = false
    appnode.appendChild(asminput)

    // Set initial value
    asminput.textContent = """|addi x0, x0, 0
                              |addi x1, x1, 1
                              |addi x2, x2, 2
                              |""".stripMargin

    // Create output textarea
    addNode(appnode, "Hex Output:", "p")
    val hexout = addNode(appnode, "", "textarea", "hexoutput").asInstanceOf[TextArea]
    hexout.readOnly = true
    addNode(appnode, nodeType = "br")

    // Create Button
    val assembleButton = document.createElement("button")
    assembleButton.textContent = "Assemble"
    assembleButton.addEventListener(
      "click",
      (e: MouseEvent) => {
        // println("Clicked")
        val out = RISCVAssembler.fromString(asminput.value).trim
        // println(asminput.value)
        // println(out)
        updateNode("hexoutput", out)
      },
    )
    appnode.appendChild(assembleButton)

    // Initialize
    // println("Input: ")
    // println(asminput.asInstanceOf[TextArea].value)
    val out = RISCVAssembler.fromString(asminput.textContent).trim
    hexout.textContent = out
    // println("Output:")
    // println(out)

  def addNode(
    targetNode: Node,
    text:       String = "",
    nodeType:   String = "p",
    id:         String = "",
    nodeClass:  String = "",
  ): Node =
    val n = document.createElement(nodeType)
    n.textContent = text
    n.id = id
    targetNode.appendChild(n)

  def updateNode(id: String, text: String) =
    val node = document.getElementById(id)
    node.textContent = text
