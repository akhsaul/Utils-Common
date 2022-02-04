package me.akhsaul.common.tools

import okhttp3.Dns
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.dnsoverhttps.DnsOverHttps
import java.net.InetAddress

object DNS {
    fun system(): Dns {
        return Dns.SYSTEM
    }

    @JvmStatic
    fun google(client: OkHttpClient): Dns {
        return DnsOverHttps.Builder()
            .client(client)
            .url("https://dns.google/dns-query".toHttpUrl())
            .bootstrapDnsHosts(address("8.8.8.8", "8.8.4.4"))
            .includeIPv6(false).post(true)
            .build()
    }

    @JvmOverloads
    fun adGuard(client: OkHttpClient, type: String? = null): Dns {
        val builder = DnsOverHttps.Builder().client(client).includeIPv6(false).post(true)
        if (type?.contentEquals("family") == true) {
            builder.url("https://dns-family.adguard.com/dns-query".toHttpUrl())
                .bootstrapDnsHosts(address("94.140.14.15", "94.140.15.16"))
        } else if (type?.contentEquals("non_filter") == true) {
            builder.url("https://dns-unfiltered.adguard.com/dns-query".toHttpUrl())
                .bootstrapDnsHosts(address("94.140.14.140", "94.140.14.141"))
        } else {
            builder.url("https://dns.adguard.com/dns-query".toHttpUrl())
                .bootstrapDnsHosts(address("94.140.14.14", "94.140.15.15"))
        }
        return builder.build()
    }

    private fun address(vararg host: String): List<InetAddress> = buildList {
        host.forEach {
            InetAddress.getByName(it)
        }
    }
}