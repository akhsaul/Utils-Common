package me.akhsaul.common.tools

import java.io.InputStream
import java.nio.charset.Charset

class URLDecoderStream @JvmOverloads constructor(
    private val data: InputStream,
    private val charset: Charset = Charset.defaultCharset()
) : InputStream() {

    override fun read(): Int {
        data.read()
        data.readAllBytes()
        TODO("Not yet implemented")
    }

    override fun read(b: ByteArray, off: Int, len: Int): Int {
        return super.read(b, off, len)
    }
}