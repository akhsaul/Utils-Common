package me.akhsaul.common.tools

class Response private constructor(res: okhttp3.Response){
    private val headers = res.headers
    val accept = getHeader(Headers.ACCEPT)
    val acceptEncoding = getHeader(Headers.ACCEPT_ENCODING)
    val acceptRanges = getHeader(Headers.ACCEPT_RANGES)
    val cookie = getHeader(Headers.COOKIE)

    /**
     * get header value of specific [name]
     * @param name header name
     * @return value of [name] or empty string
     * */
    fun getHeader(name: String): String = headers[name] ?: ""

}