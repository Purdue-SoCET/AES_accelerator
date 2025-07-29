package aes

import chisel3._
import chisel3.simulator.ChiselSim
import chisel3.experimental.BundleLiterals._
import org.scalatest.freespec.AnyFreeSpec

class ParamMixColumnsSpec extends AnyFreeSpec with ChiselSim{
  "ParamMixColumns should transform column correctly for AES-128" in {
    simulate(new ParamMixColumns(1)) { dut =>
      // Input column: 0x305dbfd4 (d4 bf 5d 30), LSB = top byte
      dut.io.in(0).poke("h305dbfd4".U)

      dut.clock.step()

      // Expected output: 0xe5816604 (04 66 81 e5)
      dut.io.out(0).expect("he5816604".U)
    }
  }

  "ParamMixColumns should transform 4 independent columns" in {
    simulate(new ParamMixColumns(4)) { dut =>
      // Set 4 columns: test only first, rest zeros
      dut.io.in(0).poke("h305dbfd4".U)
      dut.io.in(1).poke(0.U)
      dut.io.in(2).poke(0.U)
      dut.io.in(3).poke(0.U)

      dut.clock.step()

      dut.io.out(0).expect("he5816604".U)
      dut.io.out(1).expect(0.U)
      dut.io.out(2).expect(0.U)
      dut.io.out(3).expect(0.U)
    }
  }
}
