package aes

import chisel3._
import chisel3.util._

class InvSubByte extends Module {
  val io = IO(new Bundle {
    val in  = Input(UInt(32.W))   // 8-bit input
    val out = Output(UInt(32.W))  // 8-bit substituted output
  })
  
val invSBox = VecInit(Seq(
    "h52".U, "h09".U, "h6a".U, "hd5".U, "h30".U, "h36".U, "ha5".U, "h38".U,
    "hbf".U, "h40".U, "ha3".U, "h9e".U, "h81".U, "hf3".U, "hd7".U, "hfb".U,
    "h7c".U, "he3".U, "h39".U, "h82".U, "h9b".U, "h2f".U, "hff".U, "h87".U,
    "h34".U, "h8e".U, "h43".U, "h44".U, "hc4".U, "hde".U, "he9".U, "hcb".U,
    "h54".U, "h7b".U, "h94".U, "h32".U, "ha6".U, "hc2".U, "h23".U, "h3d".U,
    "hee".U, "h4c".U, "h95".U, "h0b".U, "h42".U, "hfa".U, "hc3".U, "h4e".U,
    "h08".U, "h2e".U, "ha1".U, "h66".U, "h28".U, "hd9".U, "h24".U, "hb2".U,
    "h76".U, "h5b".U, "ha2".U, "h49".U, "h6d".U, "h8b".U, "hd1".U, "h25".U,
    "h72".U, "hf8".U, "hf6".U, "h64".U, "h86".U, "h68".U, "h98".U, "h16".U,
    "hd4".U, "ha4".U, "h5c".U, "hcc".U, "h5d".U, "h65".U, "hb6".U, "h92".U,
    "h6c".U, "h70".U, "h48".U, "h50".U, "hfd".U, "hed".U, "hb9".U, "hda".U,
    "h5e".U, "h15".U, "h46".U, "h57".U, "ha7".U, "h8d".U, "h9d".U, "h84".U,
    "h90".U, "hd8".U, "hab".U, "h00".U, "h8c".U, "hbc".U, "hd3".U, "h0a".U,
    "hf7".U, "he4".U, "h58".U, "h05".U, "hb8".U, "hb3".U, "h45".U, "h06".U,
    "hd0".U, "h2c".U, "h1e".U, "h8f".U, "hca".U, "h3f".U, "h0f".U, "h02".U,
    "hc1".U, "haf".U, "hbd".U, "h03".U, "h01".U, "h13".U, "h8a".U, "h6b".U,
    "h3a".U, "h91".U, "h11".U, "h41".U, "h4f".U, "h67".U, "hdc".U, "hea".U,
    "h97".U, "hf2".U, "hcf".U, "hce".U, "hf0".U, "hb4".U, "he6".U, "h73".U,
    "h96".U, "hac".U, "h74".U, "h22".U, "he7".U, "had".U, "h35".U, "h85".U,
    "he2".U, "hf9".U, "h37".U, "he8".U, "h1c".U, "h75".U, "hdf".U, "h6e".U,
    "h47".U, "hf1".U, "h1a".U, "h71".U, "h1d".U, "h29".U, "hc5".U, "h89".U,
    "h6f".U, "hb7".U, "h62".U, "h0e".U, "haa".U, "h18".U, "hbe".U, "h1b".U,
    "hfc".U, "h56".U, "h3e".U, "h4b".U, "hc6".U, "hd2".U, "h79".U, "h20".U,
    "h9a".U, "hdb".U, "hc0".U, "hfe".U, "h78".U, "hcd".U, "h5a".U, "hf4".U,
    "h1f".U, "hdd".U, "ha8".U, "h33".U, "h88".U, "h07".U, "hc7".U, "h31".U,
    "hb1".U, "h12".U, "h10".U, "h59".U, "h27".U, "h80".U, "hec".U, "h5f".U,
    "h60".U, "h51".U, "h7f".U, "ha9".U, "h19".U, "hb5".U, "h4a".U, "h0d".U,
    "h2d".U, "he5".U, "h7a".U, "h9f".U, "h93".U, "hc9".U, "h9c".U, "hef".U,
    "ha0".U, "he0".U, "h3b".U, "h4d".U, "hae".U, "h2a".U, "hf5".U, "hb0".U,
    "hc8".U, "heb".U, "hbb".U, "h3c".U, "h83".U, "h53".U, "h99".U, "h61".U,
    "h17".U, "h2b".U, "h04".U, "h7e".U, "hba".U, "h77".U, "hd6".U, "h26".U,
    "he1".U, "h69".U, "h14".U, "h63".U, "h55".U, "h21".U, "h0c".U, "h7d".U
  ))

  io.out := Cat(invSBox(io.in(31,24)), invSBox(io.in(23, 16)), invSBox(io.in(15,8)), invSBox(io.in(7,0)))
}