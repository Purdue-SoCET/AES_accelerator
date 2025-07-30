package aes

import chisel3._
import chisel3.simulator.ChiselSim
import chisel3.experimental.BundleLiterals._
import org.scalatest.freespec.AnyFreeSpec

class InvMixColumnsSpec extends AnyFreeSpec with ChiselSim{
  "ParamInvMixColumns should transform column correctly for AES-128" in {
    simulate(new InvMixColumns()) { dut =>
      // Input column: 0x305dbfd4 (d4 bf 5d 30), LSB = top byte
      dut.io.in.poke("he5816604".U)

      dut.clock.step()

      // Expected output: 0xe5816604 (04 66 81 e5)
      dut.io.out.expect("h305dbfd4".U)
    }
  }

  }

