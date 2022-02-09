package me.akhsaul.common.core

import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

interface StreamHandler {
    @Throws(IOException::class)
    fun setProcessInputStream(outputStream: OutputStream?)

    @Throws(IOException::class)
    fun setProcessErrorStream(inputStream: InputStream?)

    @Throws(IOException::class)
    fun setProcessOutputStream(inputStream: InputStream?)

    @Throws(IOException::class)
    fun start()

    @Throws(IOException::class)
    fun stop()
}