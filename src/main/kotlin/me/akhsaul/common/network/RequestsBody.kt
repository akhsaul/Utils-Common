package me.akhsaul.common.network

import okhttp3.MediaType
import okhttp3.RequestBody
import okio.BufferedSink
import okio.Throttler
import okio.buffer

class RequestsBody(private val body: RequestBody) : RequestBody() {
    private val throttler = Throttler()
    override fun contentType(): MediaType? {
        return body.contentType()
    }

    override fun writeTo(sink: BufferedSink) {
        throttler.bytesPerSecond(100)
        val bufferedSink: BufferedSink = throttler.sink(sink).buffer()
        bufferedSink.let {
            body.writeTo(it)
        }
        bufferedSink.flush()
    }
}