package aes

import chisel3._
import chisel3.util._

class MixColumns(val NumCols: Int = 4) extends Module {
  val io = IO(new Bundle {
    val in  = Input(Vec(NumCols, UInt(32.W)))  // each column = 4 bytes
    val out = Output(Vec(NumCols, UInt(32.W))) // transformed columns
  })

  // GF(2^8) multiplication by 2
  def gm2(x: UInt): UInt = (x << 1)(7, 0) ^ (0x1b.U & Fill(8, x(7)))

  // GF(2^8) multiplication by 3
  def gm3(x: UInt): UInt = gm2(x) ^ x

  def mixOneColumn(col: UInt): UInt = {
    val b = Wire(Vec(4, UInt(8.W)))
    for (i <- 0 until 4) {
      b(i) := col((i + 1) * 8 - 1, i * 8)
    }

    val mb = Wire(Vec(4, UInt(8.W)))
    mb(0) := gm2(b(0)) ^ gm3(b(1)) ^ b(2) ^ b(3)
    mb(1) := b(0) ^ gm2(b(1)) ^ gm3(b(2)) ^ b(3)
    mb(2) := b(0) ^ b(1) ^ gm2(b(2)) ^ gm3(b(3))
    mb(3) := gm3(b(0)) ^ b(1) ^ b(2) ^ gm2(b(3))

    Cat(mb.reverse) // Reassemble to 32-bit column
  }

  // Apply MixColumns to each column independently
  for (i <- 0 until NumCols) {
    io.out(i) := mixOneColumn(io.in(i))
  }
  //print(out)
}

/*package aes

import chisel3._
import chisel3.util._

class mixColumns extends Module {
  val io = IO(new Bundle {
    val w_i    = Input(UInt(32.W))  // one AES state column
    val mixw_o = Output(UInt(32.W)) // transformed column
  })

  // GF(2^8) multiplication by 2
  def gm2(x: UInt): UInt = (x << 1)(7, 0) ^ (0x1b.U & Fill(8, x(7)))

  // GF(2^8) multiplication by 3
  def gm3(x: UInt): UInt = gm2(x) ^ x

  // Break 32-bit input into Vec of 4 bytes: b0 is LSB, b3 is MSB
  val bytes = Wire(Vec(4, UInt(8.W)))
  for (i <- 0 until 4) {
    bytes(i) := io.w_i((i + 1) * 8 - 1, i * 8)
  }

  // MixColumns transformation
  val mb = Wire(Vec(4, UInt(8.W)))
  mb(0) := gm2(bytes(0)) ^ gm3(bytes(1)) ^ bytes(2) ^ bytes(3)
  mb(1) := bytes(0) ^ gm2(bytes(1)) ^ gm3(bytes(2)) ^ bytes(3)
  mb(2) := bytes(0) ^ bytes(1) ^ gm2(bytes(2)) ^ gm3(bytes(3))
  mb(3) := gm3(bytes(0)) ^ bytes(1) ^ bytes(2) ^ gm2(bytes(3))

  // Reassemble into 32-bit output (b3 is MSB)
  io.mixw_o := Cat(mb.reverse)
}*/
