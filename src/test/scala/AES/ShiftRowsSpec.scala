package aes

import chisel3._
import chisel3.simulator.scalatest.ChiselSim
import org.scalatest.freespec.AnyFreeSpec

class ShiftRowsSpec extends AnyFreeSpec with ChiselSim {
    
    "Shift Row should shift rows" in {
        simulate(new ShiftRows(4)) { dut =>
            // Key and text needs to be 128 bits, 16 words
            val input = Seq(Seq(1,2,3,4), Seq(5,6,7,8), Seq(9,10,11,12), Seq(13,14,15,16))
            val expected = Seq(Seq(1,6,11,16), Seq(2,7,12,13), Seq(3,8,9,14), Seq(4,5,10,15))


            // Feed input
            for (col <- 0 until 4; row <- 0 until 4) {
                dut.io.in(col)(row).poke(input(col)(row).U)
            }


            // Expected output
            for (col <- 0 until 4; row <- 0 until 4) {
                dut.io.out(col)(row).expect(expected(col)(row).U)
            }

            print(dut.io.out)
            
        }
    }
}