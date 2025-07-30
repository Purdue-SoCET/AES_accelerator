package aes

import chisel3._

/*
 * Round unit for aes cipher
 */

class RoundUnit() extends Module {
    val io = IO(new Bundle {
        val in  = Input(Vec(4, Vec(4, Bits(8.W))))
        val key = Input(Vec(4, Vec(4, Bits(8.W))))
        val out = Output(Vec(4, Vec(4, Bits(8.W))))
    })
    
    val subByte         = Module(new SubByte())
    val shiftRows       = Module(new ShiftRows())
    val mixColumns      = Module(new mixColumns())
    val addRoundKey     = Module(new AddRoundKey())

    subByte.io.in       := io.in
    shiftRows.io.in     := subByte.io.out
    mixColumns.io.in    := shiftRows.io.out
    addRoundKey.io.in   := mixColumns.io.out
    addRoundKey.io.key  := io.key
    io.out              := addRoundKey.io.out
}