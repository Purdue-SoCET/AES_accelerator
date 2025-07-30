package aes

import chisel3._
import chisel3.util._

class GFunction extends Module {
  val io = IO(new Bundle {
    val in    = Input(UInt(32.W))   // Word to be transformed
    val round = Input(UInt(4.W))    // Round number (1 to 10)
    val out   = Output(UInt(32.W))  // Result of the G function
  })

  val sbox0 = Module(new SBox)
  val sbox1 = Module(new SBox)
  val sbox2 = Module(new SBox)
  val sbox3 = Module(new SBox)

  def rotWord(w: UInt): UInt = Cat(w(23,16), w(15,8), w(7,0), w(31,24))

  def subWord(w: UInt): UInt = {
    sbox0.io.in := w(31,24)
    sbox1.io.in := w(23,16)
    sbox2.io.in := w(15,8)
    sbox3.io.in := w(7,0)
    Cat(sbox0.io.out, sbox1.io.out, sbox2.io.out, sbox3.io.out)
  }

  def rcon(i: UInt): UInt = {
    val rconTable = VecInit(Seq(
      "h01".U, "h02".U, "h04".U, "h08".U,
      "h10".U, "h20".U, "h40".U, "h80".U,
      "h1B".U, "h36".U, "h6C".U, "hD8".U,
      "hAB".U, "h4D".U, "h9A".U
    ))
    rconTable(i - 1.U)  // i starts at 1
  }

  val rotated = rotWord(io.in)
  val substituted = subWord(rotated)
  val rconWord = Cat(rcon(io.round), 0.U(24.W))

  io.out := substituted ^ rconWord
}
