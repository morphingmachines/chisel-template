// See README.md for license details.

package gcd

import _root_.svsim
import chisel3.RawModule
import chisel3.simulator.{PeekPokeAPI, SingleBackendSimulator}
import org.scalatest.freespec.AnyFreeSpec
import java.nio.file.Files

/** This is a trivial example of how to run this Specification: From a terminal shell use:
  * {{{
  * mill gcd.test.testOnly gcd.GCDSpec
  * }}}
  */
class GCDSpec extends AnyFreeSpec with PeekPokeAPI {

  private lazy val verilatorWithTiming: svsim.verilator.Backend = {
    val verilatorPath = scala.sys.process.Process(Seq("which", "verilator")).!!.trim
    val wrapper = Files.createTempFile("verilator-timing-", ".sh")
    wrapper.toFile.setExecutable(true)
    Files.write(wrapper, s"#!/bin/sh\nexec $verilatorPath --timing \"$$@\"\n".getBytes)
    new svsim.verilator.Backend(wrapper.toFile.getAbsolutePath)
  }

  private def simulate[T <: RawModule](module: => T)(body: T => Unit): Unit = {
    val workDir = s"test_run_dir/${getClass.getSimpleName}"
    new SingleBackendSimulator[svsim.verilator.Backend] {
      val backend = verilatorWithTiming
      val tag = "verilator"
      val workspacePath = workDir
      val commonCompilationSettings = svsim.CommonCompilationSettings()
      val backendSpecificCompilationSettings = svsim.verilator.Backend.CompilationSettings(
        traceStyle = Some(svsim.verilator.Backend.CompilationSettings.TraceStyle.Vcd(traceUnderscore = true))
      )
    }.simulate(module) { m => body(m.wrapped) }.result
  }

  "Gcd should calculate proper greatest common denominator" in {
    simulate(new DecoupledGcd(16)) { dut =>
      val inputs  = List((48, 32), (7, 3), (100, 10))
      val outputs = List(16, 1, 10)

      dut.reset.poke(1)
      dut.input.valid.poke(false)
      dut.input.bits.value1.poke(0)
      dut.input.bits.value2.poke(0)
      dut.output.ready.poke(false)
      dut.clock.step(1)
      dut.reset.poke(0)
      dut.output.ready.poke(true)

      var i = 0
      do {
        dut.input.valid.poke(1)
        dut.input.bits.value1.poke(inputs(i)._1)
        dut.input.bits.value2.poke(inputs(i)._2)

        dut.clock.step(1)
        dut.input.valid.poke(0)

        dut.clock.stepUntil(dut.output.valid, 1, 100)
        dut.output.bits.gcd.expect(outputs(i))
        dut.clock.step(1)

        i = i + 1
      } while (i < 3)

      dut.clock.step(1)
    }
  }
}
