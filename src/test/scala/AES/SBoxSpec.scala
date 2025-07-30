// See README.md for license details.
package aes

import chisel3._
import chisel3.experimental.BundleLiterals._
import chisel3.simulator.scalatest.ChiselSim
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers

/**

  */
class SboxSpec extends AnyFreeSpec with ChiselSim {

  "Sbox should substitute the byte" in {
    simulate(new SBox()) { dut =>
      val input = (0) // 0th index 
      val expectVal = "h63".U // 0th value subbed
      dut.io.in.poke(input)
      dut.clock.step() //good practice to have to let the output settle
      dut.io.out.expect(expectVal)
      }
    }
  }
