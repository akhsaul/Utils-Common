package me.akhsaul.common.network

import me.akhsaul.common.custom.MySSL
import me.akhsaul.common.makeTempDir
import okhttp3.*
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.dnsoverhttps.DnsOverHttps
import okhttp3.logging.HttpLoggingInterceptor
import java.net.CookieManager
import java.net.CookiePolicy
import java.net.InetAddress
import java.util.concurrent.TimeUnit

@Suppress("unused")
class Clients private constructor(
    private val client: OkHttpClient,
) {
    enum class Setting {
        IGNORE_SSL,
        ENABLE_COOKIE,
        USE_DNS_GOOGLE,
        USE_DNS_CLOUDFLARE,
        USE_DEFAULT_CACHE,
        USE_DEFAULT_LOGGER,
    }

    class Builder() {
        private var client: OkHttpClient
        private var cacheMaxSize = 100L * 1024L * 1024L
        private var cacheDir = "Cache/Connection"
        private var logger = HttpLoggingInterceptor()
            .setLevel(HttpLoggingInterceptor.Level.HEADERS)
        private var interceptor: Interceptor = logger
        private var dns: okhttp3.Dns = okhttp3.Dns.SYSTEM
        private var settings = mutableSetOf<Setting>()

        init {
            client = lazy {
                OkHttpClient.Builder()
                    .connectionPool(
                        ConnectionPool(8, 5, TimeUnit.MINUTES)
                    )
                    .pingInterval(3, TimeUnit.SECONDS)
                    .build()
            }.value

            val patch = Dispatcher()
            patch.maxRequestsPerHost = 1
            patch.maxRequests = 8

            client.newBuilder()
                .dispatcher(patch)
        }

        internal constructor(clients: Clients) : this() {
            this.client = clients.client
        }

        /**
         * set DNS manually
         * @param dns, DnsOverHttps
         * */
        fun setDns(dns: DnsOverHttps) = apply {
            this.dns = dns
        }

        /**
         * set cache dir
         * */
        fun setCacheDirectory(path: String): Builder = apply {
            cacheDir = path
        }

        /**
         * Change cache size if size more than default max size
         * @param size
         **/
        fun setCacheSize(size: Long): Builder = apply {
            if (size > cacheMaxSize) {
                cacheMaxSize = size
            }
        }

        /**
         * set level of logger, using default logger
         * */
        fun setLogLevel(level: HttpLoggingInterceptor.Level): Builder = apply {
            logger.setLevel(level)
            interceptor = logger
        }

        /**
         * set a logger to use your own logger
         * */
        fun setLogger(logger: Interceptor) = apply {
            interceptor = logger
        }

        /**
         * set client using setting defined
         * */
        fun setClient(vararg setting: Setting): Builder = apply {
            settings.addAll(setting)
        }

        /**
         * final build
         * */
        fun build(): OkHttpClient {
            var dnsSetting: Setting? = null
            settings.forEach {
                client = when (it) {
                    Setting.ENABLE_COOKIE -> {
                        client.newBuilder()
                            .cookieJar(
                                JavaNetCookieJar(CookieManager().apply {
                                    setCookiePolicy(CookiePolicy.ACCEPT_ORIGINAL_SERVER)
                                })
                            ).build()
                    }
                    /**
                     * TRUST SSL-Self-Signed
                     * */
                    Setting.IGNORE_SSL -> {
                        with(MySSL()) {
                            client.newBuilder()
                                .sslSocketFactory(getSSLSocket(), getTrustManager())
                                .hostnameVerifier { _, _ -> true }
                                .build()
                        }
                    }
                    Setting.USE_DEFAULT_CACHE -> {
                        client.newBuilder()
                            .cache(
                                Cache(makeTempDir(cacheDir).toFile(), cacheMaxSize)
                            ).build()
                    }
                    Setting.USE_DEFAULT_LOGGER -> {
                        client.newBuilder()
                            .addNetworkInterceptor(interceptor).build()
                    }
                    Setting.USE_DNS_GOOGLE -> {
                        dnsSetting = it
                        client
                    }
                    Setting.USE_DNS_CLOUDFLARE -> {
                        dnsSetting = it
                        client
                    }
                }
            }

            dnsSetting?.let {
                dns = if (it == Setting.USE_DNS_GOOGLE) {
                    DnsOverHttps.Builder()
                        .client(client)
                        .url("https://dns.google/dns-query".toHttpUrl())
                        .bootstrapDnsHosts(
                            InetAddress.getByName("8.8.4.4"),
                            InetAddress.getByName("8.8.8.8")
                        )
                        .includeIPv6(false).post(true)
                        .build()
                } else {
                    DnsOverHttps.Builder()
                        .client(client)
                        .url("https://dns-unfiltered.adguard.com/dns-query".toHttpUrl())
                        .includeIPv6(false).post(true)
                        .build()
                }
            }

            return client.newBuilder().dns(dns).build()
        }
    }

    /**
     * Copy this Builder into new Builder
     * */
    fun newBuilder(): Builder = Builder(this)
}