package aes

import chisel3._
import chisel3.util._

// Galois Field multiply by 2
object GF {
  def gm2(x: UInt): UInt = (x << 1)(7, 0) ^ (0x1b.U & Fill(8, x(7)))
  def gm4(x: UInt): UInt = gm2(gm2(x))
  def gm8(x: UInt): UInt = gm2(gm4(x))
  def gm09(x: UInt): UInt = gm8(x) ^ x
  def gm11(x: UInt): UInt = gm8(x) ^ gm2(x) ^ x
  def gm13(x: UInt): UInt = gm8(x) ^ gm4(x) ^ x
  def gm14(x: UInt): UInt = gm8(x) ^ gm4(x) ^ gm2(x)
}

class InvMixColumns extends Module {
  val io = IO(new Bundle {
    val in    = Input(UInt(32.W))
    val out = Output(UInt(32.W))
  })

  val bytes = Wire(Vec(4, UInt(8.W)))
  for (i <- 0 until 4) {
    bytes(i) := io.in((i + 1) * 8 - 1, i * 8)
  }

  val mb = Wire(Vec(4, UInt(8.W)))
  mb(0) := GF.gm14(bytes(0)) ^ GF.gm11(bytes(1)) ^ GF.gm13(bytes(2)) ^ GF.gm09(bytes(3))
  mb(1) := GF.gm09(bytes(0)) ^ GF.gm14(bytes(1)) ^ GF.gm11(bytes(2)) ^ GF.gm13(bytes(3))
  mb(2) := GF.gm13(bytes(0)) ^ GF.gm09(bytes(1)) ^ GF.gm14(bytes(2)) ^ GF.gm11(bytes(3))
  mb(3) := GF.gm11(bytes(0)) ^ GF.gm13(bytes(1)) ^ GF.gm09(bytes(2)) ^ GF.gm14(bytes(3))

  io.out := Cat(mb.reverse) // MSB first
}
