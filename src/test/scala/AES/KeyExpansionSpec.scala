package aes

import chisel3._
import chisel3.experimental.BundleLiterals._
import chisel3.simulator.scalatest.ChiselSim
import org.scalatest.freespec.AnyFreeSpec

class KeyExpansionSpec extends AnyFreeSpec with ChiselSim {

  "KeyExpansion should generate all round keys for AES-128" in {
    simulate(new KeyExpansion(128)) { dut =>
      // Step 1: Set inputs
      val testKey = BigInt("2b7e151628aed2a6abf7158809cf4f3c", 16)
      dut.io.keyIn.bits.poke(testKey.U)
      dut.io.keyIn.valid.poke(true.B)
      dut.io.roundKeyOut.ready.poke(true.B)

      // Step 2: Step until the key is latched
      dut.clock.step()
      dut.io.keyIn.valid.poke(false.B)

      // Step 3: Wait and print all round keys
      var seenKeys = 0
      while (seenKeys < 11) { // 10 rounds + initial key = 11
        if (dut.io.roundKeyOut.valid.peek().litToBoolean) {
          val out = dut.io.roundKeyOut.bits.peek().litValue
          println(f"[Round $seenKeys%2d] RoundKey = 0x$out%032x")
          seenKeys += 1
        }
        dut.clock.step()
      }

      // Step 4: Check `done` signal
      dut.io.done.expect(true.B)
    }
  }
}
