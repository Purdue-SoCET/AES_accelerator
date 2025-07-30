package aes

import chisel3._

/*
 * Round unit for aes cipher
 */

class AddRoundKey() extends Module {
    val io = IO(new Bundle {
        val in  = Input(Vec(4, Vec(4, Bits(8.W))))
        val roundKey = Input(Vec(4, Vec(4, Bits(8.W))))
        val out = Output(Vec(4, Vec(4, Bits(8.W))))
    })

    io.out := io.in + io.roundKey
}