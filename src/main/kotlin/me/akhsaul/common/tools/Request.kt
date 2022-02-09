package me.akhsaul.common.tools

import okhttp3.*
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.RequestBody.Companion.toRequestBody
import me.akhsaul.common.enums.HTTPMethod
import me.akhsaul.common.enums.Priority
import me.akhsaul.common.notNull
import java.util.concurrent.TimeUnit

class Request private constructor(
    val client: Client,
    val method: HTTPMethod,
    val headers: Headers,
    val priority: Priority,
    val cache: CacheControl,
    val body: RequestBody? = null
) {
    companion object {
        @JvmField
        val DEFAULT_CACHE_CONTROL: CacheControl = CacheControl.Builder()
            .maxAge(9600, TimeUnit.SECONDS)
            .maxStale(3600, TimeUnit.SECONDS).build()
    }

    abstract class Builder(url: HttpUrl) {
        protected val url: HttpUrl.Builder = url.newBuilder()
        protected val headers = Headers().addDefault()
        protected var priority = Priority.LOW
        private var cacheControl: CacheControl? = null
        private var maxAgeSec: Int? = null
        private var maxStaleSec: Int? = null

        // take global client, if NULL then use DEFAULT client
        protected var currentClient: Client = Client.GLOBAL ?: Client.DEFAULT

        constructor(url: String) : this(url.toHttpUrl())

        fun setClient(client: OkHttpClient) {
            currentClient = Client.convert(client)
        }

        fun setClient(client: Client) {
            currentClient = client
        }

        fun addHeader(name: String, value: String) = apply {
            headers.add(name, value)
        }

        fun addHeader(map: Map<String, String>) = apply {
            headers.add(map)
        }

        fun setPriority(priority: Priority) = apply {
            this.priority = priority
        }

        fun addQueryParameter(name: String, value: String?) = apply {
            url.addQueryParameter(name, value)
        }

        fun addEncodedQueryParameter(encodedName: String, encodedValue: String?) = apply {
            url.addEncodedQueryParameter(encodedName, encodedValue)
        }

        fun addPathSegment(pathSegment: String) = apply {
            url.addPathSegment(pathSegment)
        }

        fun addPathSegments(pathSegments: String) = apply {
            url.addPathSegments(pathSegments)
        }

        fun addEncodedPathSegment(encodedPathSegment: String) = apply {
            url.addEncodedPathSegment(encodedPathSegment)
        }

        fun addEncodedPathSegments(encodedPathSegments: String) = apply {
            url.addEncodedPathSegments(encodedPathSegments)
        }

        /**
         * @see [CacheControl.noStore]
         * */
        fun cacheResponse(value: Boolean) = apply {
            if (!value) {
                cacheControl = CacheControl.Builder().noStore().build()
            }
        }

        /**
         * @see [CacheControl.FORCE_CACHE]
         * */
        fun onlyIfCached() = apply {
            cacheControl = CacheControl.FORCE_CACHE
        }

        /**
         * @see [CacheControl.FORCE_NETWORK]
         * */
        fun onlyFromNetwork() = apply {
            cacheControl = CacheControl.FORCE_NETWORK
        }

        fun setMaxAgeCacheControl(maxAge: Int, timeUnit: TimeUnit) = apply {
            require(maxAge >= 0) { IllegalArgumentException("maxAge < 0: $maxAge") }
            maxAgeSec = timeUnit.toSeconds(maxAge.toLong()).let {
                if (it > Int.MAX_VALUE) Int.MAX_VALUE else it.toInt()
            }
        }

        fun setMaxStaleCacheControl(maxStale: Int, timeUnit: TimeUnit) = apply {
            require(maxStale >= 0) { IllegalArgumentException("maxStale < 0: $maxStale") }
            maxStaleSec = timeUnit.toSeconds(maxStale.toLong()).let {
                if (it > Int.MAX_VALUE) Int.MAX_VALUE else it.toInt()
            }
        }

        fun getCacheController(): CacheControl {
            return if (cacheControl != null) {
                // use existing cache controller
                notNull(cacheControl)
            } else if (maxAgeSec != null || maxStaleSec != null) {
                // if cache control is NULL, maxAge or maxStale not NULL
                // then build a new cache controller
                CacheControl.Builder().run {
                    maxAgeSec?.let { maxAge(it, TimeUnit.SECONDS) }
                    maxStaleSec?.let { maxStale(it, TimeUnit.SECONDS) }
                    build()
                }
            } else {
                // if cache control and maxAge and maxStale is NULL
                // then use default cache control
                DEFAULT_CACHE_CONTROL
            }
        }

        abstract fun build(): Request
    }

    class GetBuilder(url: HttpUrl) : Builder(url) {
        private val method = HTTPMethod.GET

        constructor(url: String) : this(url.toHttpUrl())

        override fun build(): Request {
            return RequestQueue.add(Request(currentClient, method, headers, priority, getCacheController()))
        }
    }

    class PostBuilder(url: HttpUrl) : Builder(url) {
        private val method = HTTPMethod.POST

        constructor(url: String) : this(url.toHttpUrl())

        override fun build(): Request {
            RequestBody.create(null, "")
            "".toRequestBody()
            FormBody.Builder().add("", "").addEncoded("", "").build()
            MultipartBody.Builder().build()
            //MultipartBody
            return RequestQueue.add(Request(currentClient, method, headers, priority, getCacheController()))
        }
    }
}

fun main() {
    Request.GetBuilder("")
        .addHeader("", "")
        .build()
}