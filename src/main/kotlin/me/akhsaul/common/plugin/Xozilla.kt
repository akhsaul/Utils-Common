package me.akhsaul.common.plugin

import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import me.akhsaul.common.enum.HTTPMethod
import me.akhsaul.common.enum.VideoQuality
import me.akhsaul.common.network.Clients
import me.akhsaul.common.network.Clients.Setting.*
import me.akhsaul.common.network.Requests
import me.akhsaul.common.notNull
import me.akhsaul.common.replaceFirst
import me.akhsaul.common.replaceLast

class Xozilla(override val url: HttpUrl) : VideoPlugin, SearchPlugin {
    //override val LOG: Logger = LoggerFactory.getLogger(this::class.java)
    override val client: OkHttpClient
        get() = Clients.Builder().setClient(ENABLE_COOKIE, USE_DEFAULT_CACHE, USE_DNS_CLOUDFLARE).build()
    override val baseUrl: String = "http://www.xozilla.com"
    override var quality: VideoQuality = VideoQuality.HD

    private lateinit var data: String

    override fun isAccepted(url: String): Boolean {
        TODO("Not yet implemented")
    }

    override fun <T : HostPlugin> T.doJob(): T = apply {
        Requests.Builder(client).buildRequest(url, HTTPMethod.GET).build().connect().use { r ->
            r.body.use { b ->
                data = notNull(b).string()
                data = data.replaceFirst(data.indexOf("flashvars = {", ignoreCase = true) + 13)
                data = data.replaceLast(data.indexOfFirst { it == '}' }).replace(Regex("(\\s){2,}"), "").trim()
            }
        }
    }

    override fun getSearchResult(): List<Map<String, String>> {
        TODO("Not yet implemented")
    }

    override fun getVideoSrc(): List<HttpUrl> {
        //data = String(Sys.getResource("page/xozilla.html").readAllBytes())
        val keys = arrayOf("video_url", "video_alt_url")

        return buildList {
            keys.forEach { key ->
                val offset = data.indexOf(key, ignoreCase = true)
                if (offset != -1) {
                    LOG.info("key $key has been found")
                    add(data.value(offset, ",", 2).toHttpUrl())
                }
            }
        }
    }

    override fun getVideoDetails(): List<Map<String, String>> {
        //data = String(Sys.getResource("page/xozilla.html").readAllBytes())
        val keys = arrayOf(
            "video_id", "video_categories", "video_tags",
            "video_url", "video_url_text", "video_alt_url", "video_alt_url_text",
            "preview_url", "preview_url1", "preview_url2", "duration"
        )

        return buildList {
            keys.forEach { key ->
                val offset = data.indexOf(key, ignoreCase = true)
                if (offset != -1) {
                    add(mapOf(key to data.value(offset, "',")))
                }
            }
        }
    }

    private fun String.value(startIndex: Int = 0, stop: String, decrement: Int = 0): String {
        // take one line of startIndex until index of stop
        val line = substring(startIndex, indexOf(stop, startIndex))
        // split between key and value, then take only value
        return line.substring(line.indexOf(':', 0) + 3, line.length - decrement)
    }
}