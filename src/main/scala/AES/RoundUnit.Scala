package aes

import chisel3._

/*
 * Round unit for aes
 */

class RoundUnit() extends Module {
    val io = IO(new Bundle {
        val in  = Input(Vec(4, Vec(4, Bits(8.W))))
        val key = Input(Bits(128.W))
        val out = Output(Bits(128.W))
    })
    
    val subByte         = Module(new SubByte())
    val shiftRows       = Module(new ShiftRows())
    val mixColumns      = Module(new mixColumns())
    val addRoundKey     = Module(new AddRoundKey())
}