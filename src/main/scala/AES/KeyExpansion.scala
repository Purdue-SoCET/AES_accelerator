// package aes

// import chisel3._
// import chisel3.util._

// /** KeyExpansion module for AES that handles 128/192/256-bit keys */
// class KeyExpansion(keySize: Int) extends Module {
//   require(Seq(128, 192, 256).contains(keySize), "Invalid key size")
//   val io = IO(new Bundle {
//   val keyIn = Flipped(Decoupled(UInt(keySize.W)))   // Input key: 128, 192, or 256 bits
//   val roundKeyOut = Decoupled(UInt(128.W))          // Stream of round keys (128-bit chunks)
//   val done        = Output(Bool())                  // Optional: high when last round key is done
// })
//   //Instances of GFunction and Sbox
//   val gFunc = Module(new GFunction)
//   val sbox0 = Module(new SBox)
//   val sbox1 = Module(new SBox)
//   val sbox2 = Module(new SBox)
//   val sbox3 = Module(new SBox)

//   def subWordOnly(word: UInt): UInt = {
//     sbox0.io.in := word(31, 24)
//     sbox1.io.in := word(23, 16)
//     sbox2.io.in := word(15, 8)
//     sbox3.io.in := word(7, 0)
//     Cat(sbox0.io.out, sbox1.io.out, sbox2.io.out, sbox3.io.out)
//   }

//   val wordCount = keySize / 32 // number of 32-bit words in the input key

//   val Nr = keySize match {      //match the keysize to the round
//     case 128 => 10
//     case 192 => 12
//     case 256 => 14
//     case _   => throw new IllegalArgumentException("Invalid key size")
//   }

//   val totalRoundKeys = 4 * (Nr + 1) // Total amount of round keys

//   val words = Reg(Vec(totalRoundKeys, UInt(32.W))) // splits intial key into vector (32 bit per element) 
//   val loaded = RegInit(false.B)                // Flag: have we accepted the input key?   

//   when(io.keyIn.fire && !loaded) {
//   for (j <- 0 until wordCount) {
//     words(j) := io.keyIn.bits((keySize - 1 - j * 32), keySize - (j + 1) * 32)
//   }
//     loaded := true.B
//   }

//   val i = RegInit(wordCount.U(log2Ceil(totalRoundKeys).W)) // starts after initial key
//   val roundIndex = RegInit(0.U(log2Ceil(Nr + 2).W)) // to count 0 to Nr
//   val outputValid = RegInit(false.B)  

//   when(loaded && i < totalRoundKeys.U) {
//     val prev   = words(i - 1.U)   //w3-128,w5-192,w7-256
//     val prevNk = words(i - wordCount.U)//w0-128,w0-192,w0-256

//     val temp = Wire(UInt(32.W))

//     when(i % wordCount.U === 0.U) {
//       gFunc.io.in := prev
//       gFunc.io.round := (i / wordCount.U)(3,0)  //Gfunction case
//       temp := gFunc.io.out
//     }.elsewhen(wordCount.U > 6.U && i % wordCount.U === 4.U) {
//       temp := subWordOnly(prev)  //Sbox case
//     }.otherwise {
//       temp := prev
//     }

//     words(i) := prevNk ^ temp // ACTUAL OPERATION
//     i := i + 1.U

//     when(loaded && i >= ((roundIndex + 1.U) * 4.U)) {
//       io.roundKeyOut.valid := true.B
//       io.roundKeyOut.bits := Cat(
//         words(roundIndex * 4.U + 0.U),
//         words(roundIndex * 4.U + 1.U),
//         words(roundIndex * 4.U + 2.U),
//         words(roundIndex * 4.U + 3.U)
//       )

//       when(io.roundKeyOut.ready) {
//         roundIndex := roundIndex + 1.U
//       }
//     }.otherwise {
//       io.roundKeyOut.valid := false.B
//       io.roundKeyOut.bits := 0.U
//     }
//   }
  
//   io.done := roundIndex === (Nr + 1).U
// }
