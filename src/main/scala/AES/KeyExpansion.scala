package aes

import chisel3._
import chisel3.util._

/** KeyExpansion module for AES that handles 128/192/256-bit keys */

class KeyExpansion(keySize: Int) extends Module {
  require(Seq(128, 192, 256).contains(keySize), "Invalid key size")
  val io = IO(new Bundle {

  val keyIn       = Flipped(Decoupled(UInt(keySize.W)))             // Input key: 128, 192, or 256 bits
  val roundKeyOut = Decoupled(UInt(128.W))                          // Stream of round keys (128-bit chunks)
  val done        = Output(Bool())                                  // Optional: high when last round key is done

})
    // =-=-= Get Vals =-=-= 

      val Nr = keySize match {                                       // Match the keysize to the round
        case 128 => 10
        case 192 => 12
        case 256 => 14
        case _   => throw new IllegalArgumentException("Invalid key size")
      }
      val wordCount = keySize / 32                                     // number of 32-bit words in the input key
      val totalStages = Nr + 1
      // val totalRoundKeys = 4 * (Nr + 1)                                // Total amount of round keys
      val totalWords = 4 * (totalStages)
      

    // =-=-= Init Regs =-=-= 
      val stageRegs = Reg(Vec(Nr + 1, UInt(keySize max 128.W)))        //  Holds each round key as it flows through pipeline
      val validRegs = RegInit(VecInit(Seq.fill(Nr + 1)(false.B)))      //  Tracks which stages hold valid data
      // val roundCounter = RegInit(0.U(log2Ceil(Nr + 2).W))           //  Holds initial input key
      // val keyInReg = Reg(UInt(keySize.W))                           //  Tracks how many keys emitted (optional)
      // val wPrev = Reg(Vec(4, UInt(32.W)))                           //  Tracks last 4 words for key generation
    
    /** =-=-= Load Register =-=-= 
      keyIn.valid : Bool        // asserts when input data is valid
      keyIn.ready : Bool        // asserts when receiver is ready
      keyIn.bits  : UInt(128.W)  //the actual payload (your key)
    */

      when(io.keyIn.fire) {
        stageRegs(0) := io.keyIn.bits
        validRegs(0) := true.B
      }

      io.keyIn.ready := !validRegs(0)                                 // Accept new input only if pipeline is empty

    // === Key Generator Pipeline ===
      for (i <- 1 until totalStages) {
        val keyGen = Module(new KeyGenerator(keySize))
        keyGen.io.prevKey := stageRegs(i - 1)           // Intial or prev key
        keyGen.io.round   := i.U                        // round number input

        stageRegs(i) := keyGen.io.nextKey               // Output connected to stage reg (KEYGEN FUNCT)
        validRegs(i) := validRegs(i - 1)                // New reg gets loaded (valid)

        // io.roundKeyOut := stageRegs(i)                  // Output RoundKey  (KEYEXPANSION FUCNT)
        // valid(i-1) := false.B                           // Empty the Register
      }

    // === Output Logic: Emit any available key ===
      // val outputVec = VecInit(stageRegs)
      // val validVec  = VecInit(validRegs)
      // val outputVec = VecInit(stageRegs.toSeq)
      // val validVec  = VecInit(validRegs.toSeq)
      val outputVec = VecInit(stageRegs.toSeq.map(x => x))
      val validVec  = VecInit(validRegs.toSeq.map(x => x))

      io.roundKeyOut.valid := validVec.reduce(_ || _)
      io.roundKeyOut.bits  := Mux1H(validVec, outputVec)

    // === Clear stage after emission ===
      when(io.roundKeyOut.fire) {
        for (i <- 0 until totalStages) {
          when(validRegs(i) && outputVec(i) === io.roundKeyOut.bits) {
            validRegs(i) := false.B
          }
        }
      }

  io.done := validRegs(Nr)
}

  // Key Generator Helper function
  class KeyGenerator(val keySize: Int) extends Module {
  require(Seq(128, 192, 256).contains(keySize), "Invalid AES key size")
  val io = IO(new Bundle {
    val prevKey = Input(UInt((keySize max 128).W))
    val round   = Input(UInt(4.W))
    val nextKey = Output(UInt(128.W)) // Only output one 128-bit round key per stage
  })

  val Nk = keySize / 32
  val prevWords = Wire(Vec(Nk, UInt(32.W)))
  for (i <- 0 until Nk) {
    val hi = keySize - 1 - (i * 32)
    val lo = keySize - 32 - (i * 32)
    prevWords(i) := io.prevKey(hi, lo)
  }

  // =-=-= Modules Instantiations =-=-= 
  val gFunc = Module(new GFunction)               // for calculating w[] 
  gFunc.io.in := prevWords(Nk - 1)                // input w3 to the input of Gfunction
  gFunc.io.round := io.round                      // input round to specify the round constant 

  // =-=-=  New Word Vector =-=-= 
  val newWords = Wire(Vec(4, UInt(32.W)))

  // =-=-=  Filling New Word Vector =-=-=
  if (keySize == 128) {
    newWords(0) := prevWords(0) ^ gFunc.io.out
    newWords(1) := prevWords(1) ^ newWords(0)
    newWords(2) := prevWords(2) ^ newWords(1)
    newWords(3) := prevWords(3) ^ newWords(2)
  } else if (keySize == 192) {
    val temp = Wire(Vec(6, UInt(32.W)))
    for (i <- 0 until 6) temp(i) := prevWords(i)
    newWords(0) := temp(0) ^ gFunc.io.out
    newWords(1) := temp(1) ^ newWords(0)
    newWords(2) := temp(2) ^ newWords(1)
    newWords(3) := temp(3) ^ newWords(2)
  } else if (keySize == 256) {

  val useGFunc = (io.round % 8.U === 1.U)

  val temp = Wire(Vec(8, UInt(32.W)))
  for (i <- 0 until 8) temp(i) := prevWords(i)

  val subWord = Module(new SBox)
  subWord.io.in := temp(7)

  val gFunc = Module(new GFunction)
  gFunc.io.round := io.round
  gFunc.io.in := 0.U  // prevent garbage
  when (useGFunc) {
    gFunc.io.in := temp(7)
  }
  
  val word4 = Mux(useGFunc, gFunc.io.out, subWord.io.out)

  newWords(0) := temp(0) ^ word4
  newWords(1) := temp(1) ^ newWords(0)
  newWords(2) := temp(2) ^ newWords(1)
  newWords(3) := temp(3) ^ newWords(2)
}

  io.nextKey := Cat(newWords(0), newWords(1), newWords(2), newWords(3))
}


