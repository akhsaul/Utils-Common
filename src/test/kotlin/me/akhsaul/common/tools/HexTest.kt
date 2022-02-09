package me.akhsaul.common.tools

import me.akhsaul.common.byteBuffer
import me.akhsaul.common.debugLogger
import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class HexTest {
    companion object{
        private val charUp: CharArray = "68456C4C6F20576F526C4421".toCharArray()
        private val charLow: CharArray = "68456c4c6f20576f526c4421".toCharArray()
        private val byteHex: ByteArray = byteArrayOf(
            104, 69, 108, 76, 111, 32, 87, 111, 82, 108, 68, 33
        )
        private const val strHex = "68456c4c6f20576f526c4421"
        private const val str = "hElLo WoRlD!" // Real Str
    }

    @Test
    fun byteArrayLower() {
        val result = Hex.encodeHex(str.toByteArray(), true)
        assertArrayEquals(charLow, result)
    }

    @Test
    fun byteArrayUpper() {
        val result = Hex.encodeHex(str.toByteArray(), false)
        assertArrayEquals(charUp, result)
    }

    @Test
    fun byteBufferLower() {
        val result = Hex.encodeHex(str.byteBuffer(), true)
        assertArrayEquals(charLow, result)
    }

    @Test
    fun byteBufferUpper() {
        val result = Hex.encodeHex(str.byteBuffer(), false)
        assertArrayEquals(charUp, result)
    }

    @Test
    fun hexStrToBytes() {
        val result = Hex.decodeHex(strHex)
        assertArrayEquals(byteHex, result)
    }

    @Test
    fun hexCharsToBytes() {
        var result = Hex.decodeHex(charUp)
        assertArrayEquals(byteHex, result)
        // using charUp or charLow
        // SHOULD return same result
        result = Hex.decodeHex(charLow)
        assertArrayEquals(byteHex, result)
    }

    @Test
    fun hexStrToRealStr() {
        val result = Hex.decodeHex(strHex).decodeToString()
        assertEquals(str, result)
    }

    @Test
    fun hexCharsToRealStr() {
        var result = Hex.decodeHex(charUp).decodeToString()
        assertEquals(str, result)
        // using charUp or charLow
        // SHOULD return same result
        result = Hex.decodeHex(charLow).decodeToString()
        assertEquals(str, result)
    }
}