package me.akhsaul.common.network

import okhttp3.MediaType
import okhttp3.ResponseBody
import okio.BufferedSource
import okio.Throttler
import okio.buffer

class ResponsesBody(private val body: ResponseBody) : ResponseBody() {
    private val throttler = Throttler()
    override fun contentLength(): Long {
        TODO("Not yet implemented")
    }

    override fun contentType(): MediaType {
        TODO("Not yet implemented")
    }

    override fun source(): BufferedSource {
        throttler.bytesPerSecond(100)

        return throttler.source(body.source()).buffer()
    }
}