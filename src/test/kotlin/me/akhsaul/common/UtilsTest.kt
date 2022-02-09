package org.akhsaul.api.common

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import okhttp3.FormBody
import okhttp3.Request
import me.akhsaul.common.enums.HTTPMethod
import me.akhsaul.common.network.Clients
import me.akhsaul.common.network.Clients.Setting.*
import me.akhsaul.common.network.Requests
import org.jsoup.Jsoup
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory

private val LOG = LoggerFactory.getLogger("me.akhsaul.common.Util.kt")

class UtilsTest {
    private val client =
        Clients.Builder().setClient(ENABLE_COOKIE, USE_DNS_CLOUDFLARE, IGNORE_SSL).build()
    private val base = "www.largehdtube.com"
    private val data = mutableSetOf<Map<String, String>>()

    fun notNull() {
        var page = 1
        val job = CoroutineScope(Dispatchers.Default).async {
            while (page == 1) {
                try {
                    Requests.Builder(client.newBuilder().build())
                        .buildRequest("https://$base/en/search/jade_kush/$page.html")
                        .build()
                        .connect().body?.byteStream()?.buffered().use { stream ->
                            if (stream != null) {
                                Jsoup.parse(stream, null, "https://$base").select("#content a").forEach {
                                    val link = it.absUrl("href")
                                    if (link.contains("/hdvideo/", false)) {
                                        data.add(buildMap {
                                            put("link", link)
                                            put("sid", it.attr("data-sid"))
                                        })
                                    }
                                }
                            }
                        }
                    page++
                } catch (e: Throwable) {
                    break
                }
            }
        }

        runBlocking {
            job.await()
            println("total page $page")

            val pre = Requests.HEADERS.newBuilder().add("Referer", "http://www.largehdtube.com/en/search/jade_kush/1.html").build()
            var req: Request?
            data.first()["sid"]?.let { str ->
                req = Request.Builder().url("https://www.largehdtube.com/r_log.php").headers(pre).post(
                    FormBody.Builder()
                        .add("sid", str)
                        .add("sv", "[]")
                        .build()
                ).build()
                client.newCall(req!!).execute().headers.forEach {
                    println(it)
                }
            }
            val result = mutableSetOf<String>()
            data.forEach {
                val res = Requests.Builder(client).buildRequest(it["link"]!!, HTTPMethod.GET, pre).build().connect()
                result.add(res.request.url.toString())
            }
            result.forEach {
                println(it)
            }
        }
    }
}