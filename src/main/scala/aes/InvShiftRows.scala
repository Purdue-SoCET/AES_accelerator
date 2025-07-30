package aes

import chisel3._
import chisel3.util._

class ShiftRowsInv(val N: Int = 4) extends Module {
  val io = IO(new Bundle {
    val in  = Input(Vec(N, Vec(N, UInt(8.W)))) // State matrix: [col][row]
    val out = Output(Vec(N, Vec(N, UInt(8.W))))
  })

  // Perform cyclic right shift on each row by its row index
  for (row <- 0 until N) {
    for (col <- 0 until N) {
      io.out(col)(row) := io.in(col)((N + row - col) % N)
    }
  }
}
