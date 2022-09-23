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
        assemble(asminput, hexout)
      },
    )
    appnode.appendChild(assembleButton)

    // Initialize
    assemble(asminput, hexout)
    // Attach event handler to input TextArea
    asminput.onkeydown = { (e: KeyboardEvent) =>
      if (Seq(KeyCode.Backspace, KeyCode.Enter).contains(e.keyCode)) assemble(asminput, hexout)
    }

  def addNode(
    targetNode: Node,
    text:       String = "",
    nodeType:   String = "p",
    id:         String = "",
  ): Node =
    val n = document.createElement(nodeType)
    n.textContent = text
    n.id = id
    targetNode.appendChild(n)

  def assemble(in: TextArea, out: TextArea) =
    out.textContent = RISCVAssembler.fromString(in.value).trim
