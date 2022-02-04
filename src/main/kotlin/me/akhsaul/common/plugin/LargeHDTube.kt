package me.akhsaul.common.plugin

import kotlinx.coroutines.*
import okhttp3.FormBody
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.Request
import me.akhsaul.common.enum.HTTPMethod
import me.akhsaul.common.network.Requests
import me.akhsaul.common.notNull
import org.jsoup.Jsoup
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class LargeHDTube(override val url: HttpUrl, sid: String? = null) : SearchPlugin {
    private var sid: String? = null
    init {
        this.sid = sid
    }
    @JvmOverloads
    constructor(url: String, sid: String? = null) : this(url.toHttpUrl(), sid)

    override val LOG: Logger = LoggerFactory.getLogger(this::class.java)
    override val baseUrl: String = "http://www.largehdtube.com"
    private var sites: MutableList<String> = mutableListOf()

    override fun isAccepted(url: String): Boolean {
        TODO("Not yet implemented")
    }

    override fun <T : HostPlugin> T.doJob(): T = apply {
        val path = url.encodedPath
        if (path.contains("/search/", true)) {
            val data = getRawSearch()
            val jobs = mutableListOf<Deferred<*>>()
            data.forEach {
                val job = CoroutineScope(Dispatchers.IO).async {
                    it["link"]?.let { link ->
                        sites.add(getVideoSite(link, notNull(it["sid"])))
                    }
                }
                jobs.add(job)
            }
            runBlocking {
                jobs.awaitAll()
                println(sites)
            }
        } else if (path.contains("/hdvideo/", true)) {
            getVideoSite()
        } else {
            LOG.error("Url $url not valid for plugin ${this::class.simpleName}")
        }
    }

    override fun getSearchResult(): List<Map<String, String>> {
        throw IllegalStateException("does not implement")
    }

    private fun getVideoSite(): String {
        return getVideoSite(url, notNull(sid))
    }

    private fun getVideoSite(link: String, sid: String): String {
        return getVideoSite(link.toHttpUrl(), sid)
    }

    /**
     * this will not return video source, it only returns site of video
     * */
    private fun getVideoSite(link: HttpUrl, sid: String): String {
        return runCatching {
            LOG.info("link = $link, sid = $sid")
            val request = Request.Builder().url("https://www.largehdtube.com/r_log.php").post(
                FormBody.Builder()
                    .add("sid", sid)
                    .add("sv", "[]")
                    .build()
            ).build()
            client.newCall(request).execute().close()

            val header = Requests.HEADERS.newBuilder().add("Referer", "http://www.largehdtube.com/").build()
            Requests.Builder(client).buildRequest(link, HTTPMethod.GET, header).build().connect().use {
                val videoSrc = it.request.url.toString()
                LOG.info("video source = $videoSrc")
                return@runCatching videoSrc
            }
        }.getOrDefault("")
    }

    private fun getRawSearch(): Set<Map<String, String>> {
        val path = url.encodedPath.substring(0, url.encodedPath.lastIndexOf('/'))
        var page = 1
        val data = mutableSetOf<Map<String, String>>()
        val jobs = mutableListOf<Deferred<*>>()
        while (true) {
            try {
                LOG.info("$path/$page.html")
                Requests.Builder(client).buildRequest(url.newBuilder().encodedPath("$path/$page.html").build())
                    .build().connect().use { r ->
                        r.body?.let { b ->
                            val html = Jsoup.parse(b.string()).select("#content a")
                            html.forEach {
                                val job = CoroutineScope(Dispatchers.IO).async {
                                    val link = it.absUrl("href")
                                    if (link.contains("/hdvideo/", false)) {
                                        val imgTag = it.children().attr("data-tn")
                                        data.add(buildMap {
                                            put("img", "http://cdn.webclicks24.com/t/$imgTag.jpg")
                                            put("link", link)
                                            put("sid", it.attr("data-sid"))
                                        })
                                    }
                                }
                                jobs.add(job)
                            }
                        }
                    }
                page++
            } catch (_: Throwable) {
                break
            }
        }

        return runBlocking {
            jobs.awaitAll()
            data
        }
    }
}