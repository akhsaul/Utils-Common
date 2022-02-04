package me.akhsaul.common.custom

import me.akhsaul.common.and
import me.akhsaul.common.tools.Hex
import java.nio.ByteBuffer
import java.util.zip.Checksum

class CRC64 : Digest, Checksum {
    companion object{
        private val TABLE = Array(4) { LongArray(256) }
    }
    private val poly64 = -0x3693a86a2878f0beL
    private var crc: Long = -1

    init {
        for (s in 0..3) {
            for (b in 0..255) {
                var r = if (s == 0) b.toLong() else TABLE[s - 1][b]
                for (i in 0..7) {
                    r = if (r and 1 == 1L) {
                        r ushr 1 xor poly64
                    } else {
                        r ushr 1
                    }
                }
                TABLE[s][b] = r
            }
        }
    }

    override fun update(input: ByteArray) {
        update(input, 0, input.size)
    }

    override fun update(input: ByteArray, offset: Int, len: Int) {
        val end = offset + len
        var i = offset
        val end4 = end - 3
        while (i < end4) {
            val tmp = crc.toInt()
            crc = TABLE[3][tmp and 0xFF xor (input[i] and 0xFF)] xor
                    TABLE[2][tmp ushr 8 and 0xFF xor (input[i + 1] and 0xFF)] xor
                    (crc ushr 32) xor
                    TABLE[1][tmp ushr 16 and 0xFF xor (input[i + 2] and 0xFF)] xor
                    TABLE[0][tmp ushr 24 and 0xFF xor (input[i + 3] and 0xFF)]
            i += 4
        }
        while (i < end) crc = TABLE[0][input[i++] and 0xFF xor (crc.toInt() and 0xFF)] xor
                (crc ushr 8)
    }

    override fun getResult(toLowerCase: Boolean): CharArray {
        val value = crc.inv()
        crc = -1
        val buf = ByteArray(8)
        for (i in buf.indices) buf[i] = (value shr i * 8).toByte()

        return Hex.encodeHex(buf.reversedArray(), toLowerCase)
    }

    override fun update(input: ByteBuffer) {
        update(input.array())
    }

    @Deprecated(
        message = "error when transfer into long",
        replaceWith = ReplaceWith("CRC64().getResult()"),
        level = DeprecationLevel.ERROR,
    )
    override fun getValue(): Long {
        return -1
    }

    @Deprecated("does not effect", level = DeprecationLevel.ERROR)
    override fun reset() {
        // ignore
    }

    @Deprecated("does not effect", level = DeprecationLevel.ERROR)
    override fun update(b: Int) {
        // ignore
    }
}