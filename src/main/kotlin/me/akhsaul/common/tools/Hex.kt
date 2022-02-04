package me.akhsaul.common.tools

import org.apache.commons.codec.DecoderException
import java.nio.ByteBuffer

/**
 * Converts hexadecimal Strings
 * ONLY support UTF-8
 */
object Hex {
    /**
     * Used to build output as hex.
     */
    private val DIGITS_LOWER = charArrayOf(
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd',
        'e', 'f'
    )

    /**
     * Used to build output as hex.
     */
    private val DIGITS_UPPER = charArrayOf(
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D',
        'E', 'F'
    )

    fun toDecimal(hex: String){

    }

    /**
     * Converts a String representing hexadecimal values into an array of bytes of those same values. The returned array
     * will be half the length of the passed String, as it takes two characters to represent any given byte. An
     * exception is thrown if the passed String has an odd number of elements.
     *
     * @param data A String containing hexadecimal digits
     * @return A byte array containing binary data decoded from the supplied char array.
     * @throws DecoderException Thrown if an odd number of characters or illegal characters are supplied
     */
    @Throws(DecoderException::class)
    fun decodeHex(data: String): ByteArray {
        return decodeHex(data.toCharArray())
    }

    /**
     * Converts an array of characters representing hexadecimal values into an array of bytes of those same values. The
     * returned array will be half the length of the passed array, as it takes two characters to represent any given
     * byte. An exception is thrown if the passed char array has an odd number of elements.
     *
     * @param data An array of characters containing hexadecimal digits
     * @return A byte array containing binary data decoded from the supplied char array.
     * @throws DecoderException Thrown if an odd number of characters or illegal characters are supplied
     */
    @Throws(DecoderException::class)
    fun decodeHex(data: CharArray): ByteArray {
        val out = ByteArray(data.size shr 1)
        val len = data.size
        if (len and 0x01 != 0) {
            throw DecoderException("Odd number of characters.")
        }
        val outLen = len shr 1
        if (out.size < outLen) {
            throw DecoderException("Output array is not large enough to accommodate decoded data.")
        }
        // two characters form the hex value.
        var i = 0
        var j = 0
        while (j < len) {
            var f = toDigit(data[j], j) shl 4
            j++
            f = f or toDigit(data[j], j)
            j++
            out[i] = (f and 0xFF).toByte()
            i++
        }
        return out
    }

    /**
     * Converts an array of bytes into an array of characters representing the hexadecimal values of each byte in order.
     * The returned array will be double the length of the passed array, as it takes two characters to represent any
     * given byte.
     *
     * @param data a byte array to convert to Hex characters
     * @param toLowerCase `true` converts to lowercase, `false` to uppercase
     * @return A char array containing hexadecimal characters in the selected case
     */
    fun encodeHex(data: ByteArray, toLowerCase: Boolean = true): CharArray {
        return encodeHex(data, if (toLowerCase) DIGITS_LOWER else DIGITS_UPPER)
    }

    /**
     * Converts a byte buffer into an array of characters representing the hexadecimal values of each byte in order. The
     * returned array will be double the length of the passed array, as it takes two characters to represent any given
     * byte.
     *
     *
     * All bytes identified by [ByteBuffer.remaining] will be used; after this method
     * the value [remaining()][ByteBuffer.remaining] will be zero.
     *
     * @param data a byte buffer to convert to hex characters
     * @param toLowerCase `true` converts to lowercase, `false` to uppercase
     * @return A char array containing hexadecimal characters in the selected case
     */
    fun encodeHex(data: ByteBuffer, toLowerCase: Boolean = true): CharArray {
        return encodeHex(toByteArray(data), if (toLowerCase) DIGITS_LOWER else DIGITS_UPPER)
    }

    /**
     * Converts an array of bytes into an array of characters representing the hexadecimal values of each byte in order.
     * The returned array will be double the length of the passed array, as it takes two characters to represent any
     * given byte.
     *
     * @param data a byte array to convert to hex characters
     * @param toDigits the output alphabet (must contain at least 16 chars)
     * @return A char array containing the appropriate characters from the alphabet For best results, this should be either
     * upper- or lower-case hex.
     */
    private fun encodeHex(data: ByteArray, toDigits: CharArray): CharArray {
        val out = CharArray(data.size shl 1)
        // two characters form the hex value.
        var i = 0
        var j = 0
        while (i < data.size) {
            out[j++] = toDigits[0xF0 and data[i].toInt() ushr 4]
            out[j++] = toDigits[0x0F and data[i].toInt()]
            i++
        }
        return out
    }

    private fun toByteArray(byteBuffer: ByteBuffer): ByteArray {
        val remaining = byteBuffer.remaining()
        // Use the underlying buffer if possible
        if (byteBuffer.hasArray()) {
            val byteArray = byteBuffer.array()
            if (remaining == byteArray.size) {
                byteBuffer.position(remaining)
                return byteArray
            }
        }
        // Copy the bytes
        val byteArray = ByteArray(remaining)
        byteBuffer[byteArray]
        return byteArray
    }

    @Throws(DecoderException::class)
    private fun toDigit(ch: Char, index: Int): Int {
        val digit = ch.digitToIntOrNull(16) ?: -1
        if (digit == -1) {
            throw DecoderException("Illegal hexadecimal character $ch at index $index")
        }
        return digit
    }
}