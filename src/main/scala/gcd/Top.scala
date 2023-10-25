// See README.md for license details.

package gcd

import circt.stage.ChiselStage
import java.io._
import java.nio.file._


trait Toplevel {
  def topclass_name = this.getClass().getName().split("\\$").last

  def generated_sv_dir = s"generated_sv_dir/${topclass_name}"

  /** For firtoolOpts run `firtool --help` There is an overlap between ChiselStage args and firtoolOpts.
    *
    * TODO: Passing "--Split-verilog" "--output-annotation-file" to firtool is not working.
    */

  val chiselArgs   = Array("--full-stacktrace", "--target-dir", generated_sv_dir, "--split-verilog")
  val firtroolArgs = Array("-dedup")
}

/** To run from a terminal shell
  * {{{
  * mill gcd.runMain gcd.gcd8
  * }}}
  */
object gcd8 extends App with Toplevel {

  val str_firrtl = ChiselStage.emitCHIRRTL(new DecoupledGcd(8), args=Array("--full-stacktrace"))
  Files.createDirectories(Paths.get("generated_sv_dir"))
  val pw         = new PrintWriter(new File(s"${generated_sv_dir}.fir"))
  pw.write(str_firrtl)
  pw.close()

  os.proc(
    "firtool",
    s"${generated_sv_dir}.fir",
    "--disable-annotation-unknown",
    "--split-verilog",
    s"-o=${generated_sv_dir}",
    s"--output-annotation-file=${generated_sv_dir}/DecoupledGcd.anno.json",
  ).call() // check additional options with "firtool --help"

}

/** To run from a terminal shell
  * {{{
  * mill gcd.runMain gcd.gcd16
  * }}}
  */
object gcd16 extends App with Toplevel {

  println(
    ChiselStage.emitSystemVerilogFile(
      new DecoupledGcd(16),
      args = chiselArgs,
      firtoolOpts = firtroolArgs ++ Array("--strip-debug-info", "--disable-all-randomization"),
    ),
  )
}
