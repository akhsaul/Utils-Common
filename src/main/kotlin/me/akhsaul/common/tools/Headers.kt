package me.akhsaul.common.tools

import okhttp3.Headers.Companion.toHeaders
import me.akhsaul.common.notNull

/**
 * Enhanced Class from [okhttp3.Headers].
 *
 * You have to make sure this class does not conflict with [okhttp3.Headers]
 * */
class Headers {
    private val data = mutableMapOf<String, String>()

    companion object {
        const val ACCEPT = "Accept"
        const val ACCEPT_ENCODING = "Accept-Encoding"
        const val ACCEPT_RANGES = "Accept-Ranges"
        const val COOKIE = "Cookie"
        const val CONNECTION = "Connection"
        const val CONTENT_DISPOSITION = "Content-Disposition"
        const val CONTENT_LENGTH = "Content-Length"
        const val CONTENT_TYPE = "Content-Type"
        const val USER_AGENT = "User-Agent"
        const val HOST = "Host"
        const val RANGE = "Range"
    }

    /**
     * add default headers
     * */
    fun addDefault() = apply {
        add(USER_AGENT, "Postman")
        accept("*/*")
        acceptEncoding("gzip, deflate, br")
        add(CONNECTION, "keep-alive")
    }

    /**
     * convert [okhttp3.Headers] into [Headers]
     * @param headers an instance of [okhttp3.Headers]
     * */
    fun add(headers: okhttp3.Headers) {
        for (i in 0 until headers.size) {
            add(headers.name(i), headers.value(i))
        }
    }

    /**
     * if you call this function twice then previous [value] will be discarded
     * @param name header name
     * @param value header value
     * */
    fun add(name: String, value: String) = apply {
        data[name] = value
    }

    fun add(map: Map<String, String>) = apply {
        map.forEach { (name, value) ->
            data[name] = value
        }
    }

    /**
     * remove header with specific [names]
     * @param names header name, can be zero or more
     * */
    fun remove(vararg names: String) = apply {
        names.forEach {
            remove(it)
        }
    }

    /**
     * remove header with specific [name]
     * @param name header name
     * */
    fun remove(name: String) = apply {
        data.remove(name)
    }

    /**
     * if you call this function twice then previous [value] will be discarded
     * @param value for [ACCEPT] header, can be NULL
     * @param add add or remove [ACCEPT] header
     * @throws IllegalStateException if [value] is NULL when [add] is TRUE
     * @see notNull
     * */
    @JvmOverloads
    fun accept(value: String? = null, add: Boolean = true) = if (add) add(ACCEPT, notNull(value)) else remove(ACCEPT)

    /**
     * if you call this function twice then previous [value] will be discarded
     * @param value for [ACCEPT_ENCODING] header, can be NULL
     * @param add add or remove [ACCEPT_ENCODING] header
     * @throws IllegalStateException if [value] is NULL when [add] is TRUE
     * @see notNull
     * */
    @JvmOverloads
    fun acceptEncoding(value: String? = null, add: Boolean = true) =
        if (add) add(ACCEPT_ENCODING, notNull(value)) else remove(ACCEPT)

    /**
     * if you call this function twice then previous [value] will be discarded
     * @param value for [ACCEPT_RANGES] header, can be NULL
     * @param add add or remove [ACCEPT_RANGES] header
     * @throws IllegalStateException if [value] is NULL when [add] is TRUE
     * @see notNull
     * */
    @JvmOverloads
    fun acceptRanges(value: String? = null, add: Boolean = true) =
        if (add) add(ACCEPT_RANGES, notNull(value)) else remove(ACCEPT)

    /**
     * this function only called in internal module
     * */
    internal fun build(): okhttp3.Headers {
        // always make a new headers
        return data.toHeaders()
    }
}