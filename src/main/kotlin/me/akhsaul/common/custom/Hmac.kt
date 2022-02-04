package me.akhsaul.common.custom

import me.akhsaul.common.tools.Hex
import java.nio.ByteBuffer
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

class Hmac(algorithm: String): Digest {
    private val digest: Mac = Mac.getInstance(algorithm)

    constructor(algorithm: String, key: ByteArray): this(algorithm){
        digest.init(SecretKeySpec(key, algorithm))
    }

    override fun update(input: ByteArray, offset: Int, len: Int) {
        digest.update(input, offset, len)
    }

    override fun update(input: ByteArray) {
        digest.update(input)
    }

    override fun update(input: ByteBuffer) {
        digest.update(input)
    }

    override fun getResult(toLowerCase: Boolean): CharArray {
        return Hex.encodeHex(digest.doFinal(), toLowerCase)
    }

    fun reset(){
        digest.reset()
    }

}