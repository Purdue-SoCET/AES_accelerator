package aes

import chisel3._
import chisel3.simulator.scalatest.ChiselSim
import org.scalatest.freespec.AnyFreeSpec

class shiftRowsInvSpec extends AnyFreeSpec with ChiselSim {
    
    "Shift Rows Inverse Works" in {
        simulate(new ShiftRowsInv(4)) { dut =>
            // Key and text needs to be 128 bits, 16 words
            val expected = Seq(Seq(1,2,3,4), Seq(5,6,7,8), Seq(9,10,11,12), Seq(13,14,15,16))
            val input = Seq(Seq(1,2,3,4), Seq(6,7,8,5), Seq(11,12,9,10), Seq(16,13,14,15))


            // Feed input
            for (col <- 0 until 4; row <- 0 until 4) {
                dut.io.in(col)(row).poke(input(col)(row).U)
            }

            for (col <- 0 until 4) {
                for(row <- 0 until 4) {
                    print(input(col)(row))
                    print(" ")
                }
                print("\n")
            }

            print("\n")
            for (col <- 0 until 4) {
                for(row <- 0 until 4) {
                    print(expected(col)(row))
                    print(" ")
                }
                print("\n")
            }

            print("\n")
            for (col <- 0 until 4) {
                for(row <- 0 until 4) {
                    print(dut.io.out(col)(row).peek().litValue)
                    print(" ")
                }
                print("\n")
            }

            // Expected output
            for (col <- 0 until 4; row <- 0 until 4) {
                dut.io.out(col)(row).expect(expected(col)(row).U)
                // print("Col : " + col + " Row : " + row)
            }
        }
    }
}