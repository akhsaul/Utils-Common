package me.akhsaul.common.custom

import java.nio.ByteBuffer
import java.util.zip.Checksum

class CRC32 : Digest, Checksum {
    private val state: Checksum = java.util.zip.CRC32()

    override fun update(input: ByteArray, offset: Int, len: Int){
        state.update(input, offset, len)
    }
    override fun update(input: ByteArray){
        state.update(input)
    }
    override fun update(input: ByteBuffer){
        state.update(input)
    }
    override fun getResult(toLowerCase: Boolean): CharArray{
        return java.lang.Long.toHexString(state.value).let {
            if (!toLowerCase) it.uppercase() else it
        }.toCharArray()
    }

    override fun update(b: Int) {
        state.update(b)
    }

    @Deprecated(
        message = "error when transfer into long",
        replaceWith = ReplaceWith("CRC32().getResult()"),
        level = DeprecationLevel.ERROR,
    )
    override fun getValue(): Long {
        throw IllegalAccessException()
    }

    /**
     * Resets the checksum to its initial value.
     */
    override fun reset() {
        state.reset()
    }
}