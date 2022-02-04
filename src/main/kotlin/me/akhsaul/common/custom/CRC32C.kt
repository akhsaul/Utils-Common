package me.akhsaul.common.custom

import java.nio.ByteBuffer
import java.util.zip.Checksum

class CRC32C : Digest, Checksum {
    private val state: Checksum = java.util.zip.CRC32C()

    override fun update(b: Int) {
        state.update(b)
    }

    override fun update(input: ByteArray, offset: Int, len: Int) {
        state.update(input, offset, len)
    }

    override fun update(input: ByteArray){
        state.update(input)
    }
    override fun update(input: ByteBuffer){
        state.update(input)
    }

    override fun getResult(toLowerCase: Boolean): CharArray {
        return java.lang.Long.toHexString(state.value).let {
            if (!toLowerCase) it.uppercase() else it
        }.toCharArray()
    }

    @Deprecated(
        message = "error when transfer into long",
        replaceWith = ReplaceWith("CRC32C().getResult()"),
        level = DeprecationLevel.ERROR,
    )
    override fun getValue(): Long {
        return -1
    }

    override fun reset() {
        state.reset()
    }
}