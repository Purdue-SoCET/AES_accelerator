package aes

import chisel3._
import chisel3.util._

/** AES S-Box implemented as a ROM lookup table */
class SBox extends Module {
  val io = IO(new Bundle {
    val in  = Input(UInt(8.W))   // 8-bit input
    val out = Output(UInt(8.W))  // 8-bit substituted output
  })

  // AES S-box values as UInts (substitute with real values if needed)
  val sbox = VecInit(Seq(
    "h63".U, "h7C".U, "h77".U, "h7B".U, "hF2".U, "h6B".U, "h6F".U, "hC5".U,
    "h30".U, "h01".U, "h67".U, "h2B".U, "hFE".U, "hD7".U, "hAB".U, "h76".U,
    "hCA".U, "h82".U, "hC9".U, "h7D".U, "hFA".U, "h59".U, "h47".U, "hF0".U,
    "hAD".U, "hD4".U, "hA2".U, "hAF".U, "h9C".U, "hA4".U, "h72".U, "hC0".U,
    "hB7".U, "hFD".U, "h93".U, "h26".U, "h36".U, "h3F".U, "hF7".U, "hCC".U,
    "h34".U, "hA5".U, "hE5".U, "hF1".U, "h71".U, "hD8".U, "h31".U, "h15".U,
    "h04".U, "hC7".U, "h23".U, "hC3".U, "h18".U, "h96".U, "h05".U, "h9A".U,
    "h07".U, "h12".U, "h80".U, "hE2".U, "hEB".U, "h27".U, "hB2".U, "h75".U,
    "h09".U, "h83".U, "h2C".U, "h1A".U, "h1B".U, "h6E".U, "h5A".U, "hA0".U,
    "h52".U, "h3B".U, "hD6".U, "hB3".U, "h29".U, "hE3".U, "h2F".U, "h84".U,
    "h53".U, "hD1".U, "h00".U, "hED".U, "h20".U, "hFC".U, "hB1".U, "h5B".U,
    "h6A".U, "hCB".U, "hBE".U, "h39".U, "h4A".U, "h4C".U, "h58".U, "hCF".U,
    "hD0".U, "hEF".U, "hAA".U, "hFB".U, "h43".U, "h4D".U, "h33".U, "h85".U,
    "h45".U, "hF9".U, "h02".U, "h7F".U, "h50".U, "h3C".U, "h9F".U, "hA8".U,
    "h51".U, "hA3".U, "h40".U, "h8F".U, "h92".U, "h9D".U, "h38".U, "hF5".U,
    "hBC".U, "hB6".U, "hDA".U, "h21".U, "h10".U, "hFF".U, "hF3".U, "hD2".U,
    "hCD".U, "h0C".U, "h13".U, "hEC".U, "h5F".U, "h97".U, "h44".U, "h17".U,
    "hC4".U, "hA7".U, "h7E".U, "h3D".U, "h64".U, "h5D".U, "h19".U, "h73".U,
    "h60".U, "h81".U, "h4F".U, "hDC".U, "h22".U, "h2A".U, "h90".U, "h88".U,
    "h46".U, "hEE".U, "hB8".U, "h14".U, "hDE".U, "h5E".U, "h0B".U, "hDB".U,
    "hE0".U, "h32".U, "h3A".U, "h0A".U, "h49".U, "h06".U, "h24".U, "h5C".U,
    "hC2".U, "hD3".U, "hAC".U, "h62".U, "h91".U, "h95".U, "hE4".U, "h79".U,
    "hE7".U, "hC8".U, "h37".U, "h6D".U, "h8D".U, "hD5".U, "h4E".U, "hA9".U,
    "h6C".U, "h56".U, "hF4".U, "hEA".U, "h65".U, "h7A".U, "hAE".U, "h08".U,
    "hBA".U, "h78".U, "h25".U, "h2E".U, "h1C".U, "hA6".U, "hB4".U, "hC6".U,
    "hE8".U, "hDD".U, "h74".U, "h1F".U, "h4B".U, "hBD".U, "h8B".U, "h8A".U,
    "h70".U, "h3E".U, "hB5".U, "h66".U, "h48".U, "h03".U, "hF6".U, "h0E".U,
    "h61".U, "h35".U, "h57".U, "hB9".U, "h86".U, "hC1".U, "h1D".U, "h9E".U,
    "hE1".U, "hF8".U, "h98".U, "h11".U, "h69".U, "hD9".U, "h8E".U, "h94".U,
    "h9B".U, "h1E".U, "h87".U, "hE9".U, "hCE".U, "h55".U, "h28".U, "hDF".U,
    "h8C".U, "hA1".U, "h89".U, "h0D".U, "hBF".U, "hE6".U, "h42".U, "h68".U,
    "h41".U, "h99".U, "h2D".U, "h0F".U, "hB0".U, "h54".U, "hBB".U, "h16".U
  ))

  // Connect lookup
  io.out := sbox(io.in)
}