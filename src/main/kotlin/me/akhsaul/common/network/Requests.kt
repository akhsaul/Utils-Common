package me.akhsaul.common.network

import okhttp3.*
import okhttp3.HttpUrl.Companion.toHttpUrl
import me.akhsaul.common.*
import me.akhsaul.common.enums.HTTPMethod
import me.akhsaul.common.enums.ResponseStatus
import me.akhsaul.common.exception.IllegalParameterException
import me.akhsaul.common.exception.ResponseException
import me.akhsaul.common.exception.UnsupportedMethodException
import java.util.concurrent.TimeUnit
import me.akhsaul.common.tools.Headers as Key

@Suppress("unused", "")
class Requests private constructor(
    private val client: OkHttpClient,
    private val request: Request
) {

    companion object {
        val HEADERS = lazy {
            Headers.Builder()
                .add(Key.USER_AGENT, "")
                .add(Key.ACCEPT, "*/*")
                .add(Key.ACCEPT_ENCODING, "gzip, deflate, br")
                .add(Key.CONNECTION, "keep-alive")
                .build()
        }.value
    }

    @Suppress("unused")
    class Builder(private val client: OkHttpClient) {
        private var request: Request? = null

        internal constructor(requests: Requests) : this(requests.client) {
            this.request = requests.request
        }

        fun buildRequest(url: String): Builder {
            return buildRequest(url, HTTPMethod.GET)
        }

        fun buildRequest(url: HttpUrl): Builder {
            return buildRequest(url, HTTPMethod.GET)
        }

        fun buildRequest(url: String, method: HTTPMethod): Builder {
            return buildRequest(url, method, HEADERS)
        }

        fun buildRequest(url: HttpUrl, method: HTTPMethod): Builder {
            return buildRequest(url, method, HEADERS)
        }

        fun buildRequest(url: String, method: HTTPMethod, headers: Headers): Builder = apply {
            request = finalRequest(url, method, headers, null)
        }

        fun buildRequest(url: HttpUrl, method: HTTPMethod, headers: Headers): Builder = apply {
            request = finalRequest(url, method, headers, null)
        }

        fun buildRequest(url: String, method: HTTPMethod, headers: Headers, body: RequestBody): Builder = apply {
            request = finalRequest(url, method, headers, body)
        }

        fun buildRequest(url: HttpUrl, method: HTTPMethod, headers: Headers, body: RequestBody): Builder = apply {
            request = finalRequest(url, method, headers, body)
        }

        private fun finalRequest(url: String, method: HTTPMethod, headers: Headers, body: RequestBody?): Request {
            return finalRequest(url.toHttpUrl(), method, headers, body)
        }

        private fun finalRequest(url: HttpUrl, method: HTTPMethod, headers: Headers, body: RequestBody?): Request {
            val request = Request.Builder()
                .url(url)
                .headers(headers.newBuilder().build())
                .build()

            return runCatching {
                when (method) {
                    HTTPMethod.GET -> {
                        request.newBuilder()
                            .get().build()
                    }
                    HTTPMethod.HEAD -> {
                        request.newBuilder()
                            .head().build()
                    }
                    HTTPMethod.POST -> {
                        request.newBuilder()
                            .post(body!!).build()
                    }
                    HTTPMethod.PUT -> {
                        request.newBuilder()
                            .put(body!!).build()
                    }
                    HTTPMethod.DELETE -> {
                        request.newBuilder()
                            .delete(body).build()
                    }
                    else -> {
                        throw UnsupportedMethodException(method)
                    }
                }
            }.getOrElse {
                throw IllegalParameterException("Using method ${method.name} but body is $body", it)
            }
        }

        fun build(): Requests {
            request = request?.newBuilder()
                ?.cacheControl(
                    CacheControl.Builder()
                        .maxAge(9600, TimeUnit.SECONDS)
                        .maxStale(3600, TimeUnit.SECONDS).build()
                )?.build() ?: request

            return runCatching {
                Requests(
                    client,
                    request!!
                )
            }.getOrElse {
                throw IllegalCallerException("Call buildRequest() first!", it)
            }
        }
    }

    @Suppress("all")
    open class PostBuilder(private val client: OkHttpClient) {
        //private var mapHeader = mutableMapOf<String, String>()
        //val mapper = jacksonObjectMapper()
        //private val tHeader = object : TypeToken<Map<String, String>>(){}.type
        //private val gson = Gson()
    }

    class PutBuilder(client: OkHttpClient) : PostBuilder(client)

    fun newBuilder(): Builder = Builder(this)

    fun connect(): Response {
        return client.newCall(request).execute().validate()
    }

    fun peekSource(byteCount: Long): ResponseBody = let {
        require(byteCount >= 1 && byteCount <= (1 * 1024 * 1024)){
            IllegalParameterException("Min 1 Bytes, Max 1 MegaBytes ('${(1 * 1024 * 1024)}').")
        }
        connect().peekBody(byteCount)
    }

    private fun Response.validate(): Response {
        if (AcceptRanges.isNotBlank()) {
            println("Network supports ResumeAble Content. Accept-Ranges: $AcceptRanges")
        } else {
            println("Network does NOT support ResumeAble Content.")
        }
        // TODO: need to handle all HTTP code
        return when (this.code) {
            ResponseStatus.HTTP_OK.code -> this
            ResponseStatus.HTTP_PARTIAL_CONTENT.code -> this
            ResponseStatus.HTTP_UNAUTHORIZED.code -> {
                //this.close()
                throw ResponseException("Authentication failed.")
                //throw ApiException("Authentication failed.", "$javaClass", this.message)
            }
            ResponseStatus.HTTP_NOT_FOUND.code -> {
                //this.close()
                throw ResponseException("The resource you requested could not be found.")
                /*
                throw ApiException(
                    "The resource you requested could not be found.",
                    "$javaClass",
                    "Please check your request",
                    "${this.headers}",
                    this.message
                )*/
            }
            else -> {
                this
                //throw ApiException("Unknown Response Status.", "$javaClass", "ResponseCode : ${this.code}")
            }
        }
    }
}