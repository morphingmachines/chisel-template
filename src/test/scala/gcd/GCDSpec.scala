// See README.md for license details.

package gcd
import chisel3.simulator.VCDHackedEphemeralSimulator._
import org.scalatest.freespec.AnyFreeSpec

/** This is a trivial example of how to run this Specification: From a terminal shell use:
  * {{{
  * mill gcd.test.testOnly gcd.GCDSpec
  * }}}
  */
class GCDSpec extends AnyFreeSpec {
  "Gcd should calculate proper greatest common denominator" in {
    simulate(new DecoupledGcd(16)) { dut =>
      val inputs  = List((48, 32), (7, 3), (100, 10))
      val outputs = List(16, 1, 10)

      dut.reset.poke(1)
      dut.input.valid.poke(false)
      // dut.input.bits.poke((new GcdInputBundle(16)).Lit(_.value1 -> 0.U, _.value2 -> 0.U))
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

        dut.clock.stepUntil(dut.output.valid, 1, 10)
        dut.output.bits.gcd.expect(outputs(i))
        dut.clock.step(1)

        i = i + 1
      } while (i < 3)

      dut.clock.step(1)
    }
  }
}
