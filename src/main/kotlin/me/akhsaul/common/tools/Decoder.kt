package me.akhsaul.common.tools

import me.akhsaul.common.notNull
import me.akhsaul.common.withLock
import java.io.InputStream
import java.net.URLDecoder
import java.nio.ByteBuffer
import java.nio.charset.Charset
import java.util.*

object Decoder {
    private var base64: Base64.Decoder? = null

    private fun getBase64(): Base64.Decoder {
        if (base64 == null){
            base64 = withLock {
                base64 ?: Base64.getDecoder()
            }
        }
        return notNull(base64)
    }

    @JvmStatic
    fun base64(data: String): ByteArray {
        return getBase64().decode(data)
    }

    @JvmStatic
    fun base64(data: ByteArray): ByteArray {
        return getBase64().decode(data)
    }

    @JvmStatic
    fun base64(data: ByteBuffer): ByteBuffer {
        return getBase64().decode(data)
    }

    @JvmStatic
    fun base64(data: InputStream): InputStream {
        return getBase64().wrap(data)
    }

    @JvmOverloads
    @JvmStatic
    fun url(data: String, charset: Charset = Charset.defaultCharset()): String {
        return URLDecoder.decode(data, charset)
    }
    @JvmOverloads
    @JvmStatic
    fun url(data: InputStream, charset: Charset = Charset.defaultCharset()) {
    }
}