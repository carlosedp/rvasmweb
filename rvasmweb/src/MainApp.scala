package com.carlosedp
package rvasmweb

import com.carlosedp.riscvassembler.RISCVAssembler
import com.raquo.laminar.api.L._
import org.scalajs.dom
import org.scalajs.dom.{Event, KeyCode, document, window}

object MainApp:

  def setupUI() =
    val inASMAPI =
      if window.location.pathname == "/api" then dom.URLSearchParams(window.location.search).get("asm") else ""
    val initialAsm = """|addi x0, x0, 0
                        |addi x1, x1, 1
                        |addi x2, x2, 2
                        |""".stripMargin

    val input = if inASMAPI == "" then initialAsm else inASMAPI
    val inputASM = textArea(
      input,
      idAttr     := "asminput",
      spellCheck := false,
      title      := "Hitting enter or backspace also assembles to output",
      onMountFocus,
    )

    val outputHex = textArea(
      idAttr     := "hexoutput",
      spellCheck := false,
      readOnly   := true,
      title      := "Clicking the output area copies data to clipboard",
    )

    // Trigger assemble on Enter or Backspace keys
    inputASM.amendThis { t =>
      onKeyDown --> (e => if Seq(KeyCode.Backspace, KeyCode.Enter).contains(e.keyCode) then assemble(t, outputHex))
    }
    // Copy contents to clipboard on output textarea click
    outputHex.amendThis { t =>
      onClick --> (_ => {
        t.ref.select()
        document.execCommand("copy")
        window.alert("Content copied to clipboard")
      })
    }

    // BuildInfo section
    val BuildInfo =
      div(
        idAttr := "buildinfo",
        a(
          "RISC-V Assembler library",
          href   := "https://github.com/carlosedp/riscvassembler",
          target := "_blank",
        ),
        p(s"Version: ${RISCVAssembler.AppInfo.appVersion}"),
        p(s"Build date: ${RISCVAssembler.AppInfo.buildDate}"),
      )

    // Root element
    val rootElement = div(
      h2("Scala.js RISC-V Assembler!"),
      label("Assembly Input:"),
      inputASM,
      label("Hex Output:"),
      outputHex,
      br(),
      br(),
      div(
        idAttr := "centered",
        button(
          "Assemble",
          onClick --> (_ => assemble(inputASM, outputHex)),
        ),
      ),
      br(),
      BuildInfo,
    )

    // Initialize with default textarea input and render
    assemble(inputASM, outputHex)
    val containerNode = document.getElementById("app")
    render(containerNode, rootElement)

  def assemble(in: TextArea, out: TextArea) =
    out.amend(value := RISCVAssembler.fromString(in.ref.value).trim)

  def main(args: Array[String]): Unit =
    document.addEventListener("DOMContentLoaded", (e: Event) => setupUI())
