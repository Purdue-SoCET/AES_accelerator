package aes

import chisel3._

/*
 * Pipelined AES cipher accelerator
 */

class Cipher(keySize: Int) extends Module {
    val io = IO(new Bundle {
        val nRST            = Input(Bool())
        val in              = Input(Bits(128.W))
        val key             = Input(Bits(keySize.W))
        val out             = Output(Bits(128.W))
        val outputValid     = Output(Bool())
    })

    def getNr(keySize: Int): Int = keySize match {
        case 128 => 10
        case 192 => 12
        case 256 => 14
        case _   => throw new IllegalArgumentException("Invalid key size")
    }
    val Nr = getNr(keySize)

    val keyExpansion = Module(new keyExpansion())
    val initialADD = Module(new AddRoundKey())

    val pipeline = withReset(!io.Input.nRST) {
        Seq.fill(1 + Nr)(Seq.fill(4)(Seq.fill(4)(RegInit(0.U(8.W)))))
    }

    val roundUnits = Seq.fill(Nr - 1)(new Module(RoundUnit()))
}