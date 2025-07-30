package aes

import chisel3._

/*
 * Pipelined AES cipher accelerator
 */

class Cipher(keySize: Int) extends Module {
    val io = IO(new Bundle {
        val nRST            = Input(Bool())
        val in              = Input(Bits(128.W))
        val load            = Input(Bool())
        val key             = Input(Bits(keySize.W))
        val out             = Output(Bits(128.W))
        val done            = Output(Bool())
    })

    def getNr(keySize: Int): Int = keySize match {
        case 128 => 10
        case 192 => 12
        case 256 => 14
        case _   => throw new IllegalArgumentException("Invalid key size")
    }
    val Nr = getNr(keySize)

    // Initialize modules
    val keyExpansion = Module(new KeyExpansion())
    val initialAddRoundKey = Module(new AddRoundKey())
    val roundUnitVec = VecInit(Seq.fill(Nr - 1)(new RoundUnit()))
    val lastSubByte = Module(new SubByte())
    val lastShiftRows = Module(new ShiftRows())
    val lastAddRoundKey = Module(new AddRoundKey())

    // Initialize pipeline reigsters
    val pipeline = withReset(~io.nRST) {
        RegInit(VecInit(Seq.fill(Nr + 1)(VecInit(Seq.fill(4)(VecInit(Seq.fill(4)(0.U(8.W))))))))
    }
    // Valid register is to track whether output is valid
    val valid = withReset(~io.nRST) {
        RegInit(VecInit(Seq.fill(Nr + 1)(0.U(1.W))))
    }

    // Convert in to 4x4 vec to feed to initialAddRoundKey module
    val inVec = Wire(Vec(4, Vec(4, Bits(8.W))))
    for(row <- 0 until 4) {
        for(col <- 0 until 4) {
            inVec(row)(col) := io.in(127 - (row + col * 4) * 8, 120 - (row + col * 4) * 8)
        }
    }
    
    // Connect wires
    // TODO - Input roundKey to initialAddRoundKey module
    initialAddRoundKey.io.in := inVec

    pipeline(0) := initialAddRoundKey.io.out
    valid(0)    := io.load

    for(i <- 0 until Nr - 1) {
        roundUnitVec(i).io.in   := pipeline(i)
        pipeline(i + 1)         := roundUnitVec(i).io.out
        // TODO - Input roundKey to RoundUnit
    }

    for(i <- 1 to Nr) {
        valid(i) := valid(i - 1)
    }

    lastSubByte.io.in       := pipeline(Nr - 1)
    lastShiftRows.io.in     := lastSubByte.io.out
    lastAddRoundKey.io.in   := lastShiftRows.io.out
    // TODO - Input roundKey to lastAddRoundKey module

    // Convert 4x4 state into 128 bit wire for output
    val outWire = Wire(Bits(128.W))
    for(row <- 0 until 4) {
        for(col <- 0 until 4) {
            outWire(127 - (row + col * 4) * 8, 120 - (row + col * 4) * 8) := lastAddRoundKey.io.out(row)(col)
        }
    }

    pipeline(Nr) := outWire

    io.out  := pipeline(Nr)
    io.done := valid(Nr)
}