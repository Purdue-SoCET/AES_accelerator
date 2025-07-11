package aes

import chisel3._

/*
 * Inv round unit for aes decipher
 */

class InvRoundUnit() extends Module {
    val io = IO(new Bundle {
        val in  = Input(Vec(4, Vec(4, Bits(8.W))))
        val key = Input(Vec(4, Vec(4, Bits(8.W))))
        val out = Output(Vec(4, Vec(4, Bits(8.W))))
    })
    
    val invSubByte         = Module(new InvSubByte())
    val invShiftRows       = Module(new InvShiftRows())
    val invMixColumns      = Module(new InvMixColumns())
    val addRoundKey        = Module(new AddRoundKey())

    invShiftRows.io.in      := io.in
    invSubByte.io.in        := invShiftRows.io.out
    addRoundKey.io.in       := invSubByte.io.out
    addRoundKey.io.key      := io.key
    invMixColumns.io.in     := addRoundKey.io.out
    io.out                  := invMixColumns.io.out
}