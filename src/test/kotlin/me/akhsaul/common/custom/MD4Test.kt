package me.akhsaul.common.custom

import me.akhsaul.common.EOF
import me.akhsaul.common.tools.Hex
import org.junit.jupiter.api.Test
import java.io.File
/*
internal class MD4Test {
    private lateinit var file: File
    private var md4 = MD4()

    @Test
    fun updateByByte() {
        "HeLlO WoRlD!".byteInputStream().use { i ->
            var n: Int
            while (i.read().also { n = it } != EOF) {
                md4.update(n.toByte())
            }
        }
        assertEquals("723decdfc7833489e4a37b2d67220140", Hex.encodeHex(md4.digest(), true).concatToString())
        md4.reset()
    }

    @Test
    fun updateByArray() {
        println(file.absolutePath)
        file.inputStream().use { i ->
            val buf = ByteArray(DEFAULT_BUFFER_SIZE)
            var n: Int
            while (i.read(buf).also { n = it } != EOF) {
                md4.update(buf, 0, n)
            }
        }
        assertEquals("4e8a3c704b976359f2c40b6e880a9a72", Hex.encodeHex(md4.digest(), true).concatToString())
        md4.reset()
    }
}
 */