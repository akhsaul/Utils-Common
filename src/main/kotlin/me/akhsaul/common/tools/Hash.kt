package me.akhsaul.common.tools

import me.akhsaul.common.EOF
import me.akhsaul.common.custom.*
import java.io.File
import java.io.InputStream
import java.nio.ByteBuffer

class Hash(algorithm: Algorithm) {
    enum class Algorithm(val value: String) {
        CRC32(""),
        CRC32C(""),
        CRC64(""),
        MD2("MD2"),
        MD4("MD4"),
        MD5("MD5"),
        SHA_1("SHA-1"),
        SHA_224("SHA-224"),
        SHA_256("SHA-256"),
        SHA_384("SHA-384"),
        SHA_512("SHA-512"),
        SHA_512_224("SHA-512/224"),
        SHA_512_256("SHA-512/256"),
        SHA3_224("SHA3-224"),
        SHA3_256("SHA3-256"),
        SHA3_384("SHA3-384"),
        SHA3_512("SHA3-512"),
        HMAC_MD5("HmacMD5"),
        HMAC_SHA_1("HmacSHA1"),
        HMAC_SHA_224("HmacSHA224"),
        HMAC_SHA_256("HmacSHA256"),
        HMAC_SHA_384("HmacSHA384"),
        HMAC_SHA_512("HmacSHA512"),
        HMAC_SHA_512_224("HmacSHA512/224"),
        HMAC_SHA_512_256("HmacSHA512/256"),
        HMAC_PBE_SHA_1("HmacPBESHA1"),
        HMAC_PBE_SHA_224("HmacPBESHA224"),
        HMAC_PBE_SHA_256("HmacPBESHA256"),
        HMAC_PBE_SHA_384("HmacPBESHA384"),
        HMAC_PBE_SHA_512("HmacPBESHA512"),
        HMAC_PBE_SHA_512_224("HmacPBESHA512/224"),
        HMAC_PBE_SHA_512_256("HmacPBESHA512/256"),
        HMAC_SHA3_224("HmacSHA3-224"),
        HMAC_SHA3_256("HmacSHA3-256"),
        HMAC_SHA3_384("HmacSHA3-384"),
        HMAC_SHA3_512("HmacSHA3-512"),
    }

    private val hash: Digest

    init {
        hash = when (algorithm) {
            Algorithm.CRC32 -> CRC32()
            Algorithm.CRC32C -> CRC32C()
            Algorithm.CRC64 -> CRC64()
            Algorithm.MD2, Algorithm.MD4, Algorithm.MD5, Algorithm.SHA_1, Algorithm.SHA_224, Algorithm.SHA_256,
            Algorithm.SHA_384, Algorithm.SHA_512, Algorithm.SHA_512_224, Algorithm.SHA_512_256,
            Algorithm.SHA3_224, Algorithm.SHA3_384, Algorithm.SHA3_256, Algorithm.SHA3_512 -> {
                Message(algorithm.value)
            }
            Algorithm.HMAC_MD5, Algorithm.HMAC_SHA_1, Algorithm.HMAC_SHA_224, Algorithm.HMAC_SHA_256,
            Algorithm.HMAC_SHA_384, Algorithm.HMAC_SHA_512, Algorithm.HMAC_SHA_512_224,
            Algorithm.HMAC_SHA_512_256, Algorithm.HMAC_PBE_SHA_1, Algorithm.HMAC_PBE_SHA_224,
            Algorithm.HMAC_PBE_SHA_256, Algorithm.HMAC_PBE_SHA_384, Algorithm.HMAC_PBE_SHA_512,
            Algorithm.HMAC_PBE_SHA_512_224, Algorithm.HMAC_PBE_SHA_512_256, Algorithm.HMAC_SHA3_224,
            Algorithm.HMAC_SHA3_256, Algorithm.HMAC_SHA3_384, Algorithm.HMAC_SHA3_512 -> {
                Hmac(algorithm.value)
            }
        }
    }


    fun generate(input: InputStream, toLowerCase: Boolean = false): CharArray {
        input.buffered().use { i ->
            val buf = ByteArray(DEFAULT_BUFFER_SIZE)
            var n: Int
            while (i.read(buf).also { n = it } != EOF) {
                hash.update(buf, 0, n)
            }
        }
        return hash.getResult(toLowerCase)
    }

    fun generate(input: ByteArray, toLowerCase: Boolean = false): CharArray {
        hash.update(input)
        return hash.getResult(toLowerCase)
    }

    fun generate(input: String, toLowerCase: Boolean = false): CharArray {
        return generate(input.toByteArray(), toLowerCase)
    }

    fun generate(input: File, toLowerCase: Boolean = false): CharArray {
        return generate(input.inputStream(), toLowerCase)
    }

    fun generate(input: ByteBuffer, toLowerCase: Boolean = false): CharArray {
        return generate(input.array(), toLowerCase)
    }
}