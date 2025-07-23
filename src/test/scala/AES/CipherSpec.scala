package aes

import chisel3._
import chisel3.simulator.scalatest.ChiselSim

import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec
import java.util.Base64

/*
 * Pipelined AES cipher accelerator testbench
 */

class CipherSpec extends AnyFreeSpec with ChiselSim {
    def encrypt(plainText: String, secretKey: String): String = {
        val key: SecretKey = new SecretKeySpec(secretKey.getBytes("UTF-8"), "AES")
        val cipher: Cipher = Cipher.getInstance("AES")
        cipher.init(Cipher.ENCRYPT_MODE, key)
        val encryptedBytes = cipher.doFinal(plainText.getBytes("UTF-8"))
        Base64.getEncoder.encodeToString(encryptedBytes)
    }

    def decrypt(encryptedText: String, secretKey: String): String = {
        val key = new SecretKeySpec(secretKey.getBytes("UTF-8"), "AES")
        val cipher: Cipher = Cipher.getInstance("AES")
        cipher.init(Cipher.DECRYPT_MODE, key)
        val decryptedBytes = cipher.doFinal(Base64.getDecoder.decode(encryptedText))
        new String(decryptedBytes, "UTF-8")
    }

    "Cipher should return AES-128 ciphered text" in {
        simulate(new Cipher(128)) { dut =>
            // Key and text needs to be 128 bits, 16 words
            val text    = "SOCET2025SUMMER!"
            val key     = "1234567891011123"

            dut.reset.poke(true.B)
            dut.clock.step()
            dut.reset.poke(false.B)
            dut.clock.step()

            // Feed input
            dut.input.in.poke(text.U(128.W))
            dut.input.key.poke(key.U(128.W))
            dut.input.load.poke(true.B)
            
            // Take a step
            dut.clock.step()

            // Stop loading
            dut.input.load.poke(false.B)

            // Take 7 steps
            dut.clock.step(7)

            // Expected result
            // TODO - fill this field
            val expectedText = encrypt(text, key)
            dut.io.done.peek(true.B)
            dut.io.out.peek(expectedText)
        }
    }
    
    "Decipher should return AES-128 deciphered text" in {
        simulate(new Decipher(128)) { dut =>
            // Key and text needs to be 128 bits, 16 words
            val expectedText    = "SOCET2025SUMMER!"
            val key             = "1234567891011123"
            val text            = encrypt(text, key)

            dut.reset.poke(true.B)
            dut.clock.step()
            dut.reset.poke(false.B)
            dut.clock.step()

            // Feed input
            dut.input.in.poke(text.U(128.W))
            dut.input.key.poke(key.U(128.W))
            dut.input.load.poke(true.B)
            
            // Take a step
            dut.clock.step()

            // Stop loading
            dut.input.load.poke(false.B)

            // Take 7 steps
            dut.clock.step(7)

            // Expected result
            // TODO - fill this field
            dut.io.done.peek(true.B)
            dut.io.out.peek(expectedText)
        }
    }
}
