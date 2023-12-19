// See README.md for license details.

package gcd

import circt.stage.ChiselStage

import java.io._
import java.nio.file._

trait Toplevel {
  def topModule: chisel3.RawModule
  def topclass_name = topModule.getClass().getName().split("\\$").mkString(".")

  def generated_sv_dir = s"generated_sv_dir/${topclass_name}"

  /** For firtoolOpts run `firtool --help` There is an overlap between ChiselStage args and firtoolOpts.
    *
    * TODO: Passing "--Split-verilog" "--output-annotation-file" to firtool is not working.
    */

  lazy val chiselArgs   = Array("--full-stacktrace", "--target-dir", s"${generated_sv_dir}", "--split-verilog")
  lazy val firtroolArgs = Array("-dedup")

  def chisel2firrtl() = {
    val str_firrtl = ChiselStage.emitCHIRRTL(topModule, args = Array("--full-stacktrace"))
    Files.createDirectories(Paths.get("generated_sv_dir"))
    val pw = new PrintWriter(new File(s"${generated_sv_dir}.fir"))
    pw.write(str_firrtl)
    pw.close()
  }

  // Call this only after calling chisel2firrtl()
  def firrtl2sv() =
    os.proc(
      "firtool",
      s"${generated_sv_dir}.fir",
      "--disable-annotation-unknown",
      "--split-verilog",
      s"-o=${generated_sv_dir}",
      s"--output-annotation-file=${generated_sv_dir}/${topclass_name}.anno.json",
    ).call() // check additional options with "firtool --help"

  def chisel2sv() =
    ChiselStage.emitSystemVerilogFile(
      topModule,
      args = chiselArgs,
      firtoolOpts = firtroolArgs ++ Array("--strip-debug-info", "--disable-all-randomization"),
    )
}

/** To run from a terminal shell
  * {{{
  * ./mill gcd.runMain gcd.gcd8
  * }}}
  */
object gcd8 extends App with Toplevel {

  lazy val topModule = new DecoupledGcd(8)
  chisel2firrtl()
  firrtl2sv()

}

/** To run from a terminal shell
  * {{{
  * ./mill gcd.runMain gcd.gcd16
  * }}}
  */
object gcd16 extends App with Toplevel {
  override val topclass_name = gcd16.getClass().getName().split("\\$").mkString(".")

  lazy val topModule = new DecoupledGcd(16)
  println(chisel2sv())
}
