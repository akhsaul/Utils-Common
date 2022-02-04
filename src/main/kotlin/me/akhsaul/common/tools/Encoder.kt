package me.akhsaul.common.tools

import me.akhsaul.common.notNull
import me.akhsaul.common.withLock
import java.net.URLEncoder
import java.nio.ByteBuffer
import java.nio.charset.Charset
import java.util.*

object Encoder {
    private var base64: Base64.Encoder? = null

    private fun getBase64(): Base64.Encoder {
        if (base64 == null){
            base64 = withLock {
                base64 ?: Base64.getEncoder()
            }
        }
        return notNull(base64)
    }

    @JvmStatic
    fun base64(data: String, charset: Charset = Charsets.UTF_8): String {
        return String(getBase64().encode(data.toByteArray(charset)), charset)
    }

    @JvmStatic
    fun base64(data: ByteArray): ByteArray {
        return getBase64().encode(data)
    }

    @JvmStatic
    fun base64(data: ByteBuffer): ByteBuffer {
        return getBase64().encode(data)
    }

    @JvmStatic
    @JvmOverloads
    fun url(data: String, charset: Charset = Charsets.UTF_8): String {
        return URLEncoder.encode(data, charset)
    }
}