package me.akhsaul.common.custom

import me.akhsaul.common.tools.Hex
import java.nio.ByteBuffer
import java.security.MessageDigest

class Message(algorithm: String): Digest {
    private val digest = MessageDigest.getInstance(algorithm)

    override fun update(input: ByteArray, offset: Int, len: Int) {
        digest.update(input, offset, len)
    }
    override fun update(input: ByteArray){
        digest.update(input)
    }

    override fun update(input: ByteBuffer){
        digest.update(input)
    }

    override fun getResult(toLowerCase: Boolean): CharArray {
        return Hex.encodeHex(digest.digest(), toLowerCase)
    }

    fun reset(){
        digest.reset()
    }
}