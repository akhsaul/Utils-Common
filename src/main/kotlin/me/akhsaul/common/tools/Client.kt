package me.akhsaul.common.tools

import okhttp3.ConnectionPool
import okhttp3.Dns
import okhttp3.OkHttpClient
import java.util.*
import java.util.concurrent.TimeUnit

class Client private constructor(internal val okhttp: OkHttpClient) {
    class Builder() {
        var connectTimeout = 0L
        var readTimeout = 0L
        var writeTimeout = 0L
        private var DOH: (client: OkHttpClient) -> Dns = { DNS.system() }

        constructor(client: Client) : this() {
            connectTimeout = client.okhttp.connectTimeoutMillis.toLong()
            readTimeout = client.okhttp.readTimeoutMillis.toLong()
            writeTimeout = client.okhttp.writeTimeoutMillis.toLong()
        }

        fun connectTimeout(timeout: Long, unit: TimeUnit = TimeUnit.SECONDS) = apply {
            connectTimeout = unit.toMillis(timeout)
        }

        fun readTimeout(timeout: Long, unit: TimeUnit = TimeUnit.SECONDS) = apply {
            readTimeout = unit.toMillis(timeout)
        }

        fun writeTimeout(timeout: Long, unit: TimeUnit = TimeUnit.SECONDS) = apply {
            writeTimeout = unit.toMillis(timeout)
        }

        fun setDns(dns: (client: OkHttpClient) -> Dns) {
            DOH = dns
        }

        fun dnsGoogle() {
            DOH = { client ->
                DNS.google(client)
            }
        }

        fun dnsCloudflare() {
        }

        fun dnsADGuard() {
        }

        fun build(): Client {
            val client = OkHttpClient.Builder()
                .connectTimeout(connectTimeout, TimeUnit.MILLISECONDS)
                .readTimeout(readTimeout, TimeUnit.MILLISECONDS)
                .writeTimeout(writeTimeout, TimeUnit.MILLISECONDS)
                .connectionPool(ConnectionPool(8, 5, TimeUnit.MINUTES))
                .pingInterval(3, TimeUnit.SECONDS)
                .build().let {
                    it.newBuilder().dns(DOH.invoke(it)).build()
                }

            return Client(client)
        }
    }

    companion object {
        @JvmField
        val DEFAULT: Client = Builder()
            .connectTimeout(60)
            .build()

        /**
         * convert [OkHttpClient] into [Client]
         * */
        @JvmStatic
        fun convert(client: OkHttpClient): Client {
            return Client(client)
        }

        /**
         * GLOBAL [Client], if not NULL then every [Request] will be made by this client
         * */
        internal var GLOBAL: Client? = null
    }

    /**
     * set current client to be global, so you don't have to make a new client
     * */
    @Synchronized
    fun setAsGlobal() {
        GLOBAL = this
    }

    fun new(): Builder {
        return Builder(this)
    }

    internal fun get(): OkHttpClient {
        return okhttp
    }
}

fun main() {
    mutableMapOf<String, String>()
    val s = Client.Builder()
}