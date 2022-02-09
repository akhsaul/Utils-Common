package me.akhsaul.common.plugin

import okhttp3.HttpUrl
import me.akhsaul.common.enums.VideoQuality

class Analdin(override val url: HttpUrl) : VideoPlugin, SearchPlugin {
    override val baseUrl: String = "http://www.analdin.com"
    override var quality: VideoQuality = VideoQuality.HD

    override fun isAccepted(url: String): Boolean {
        TODO("Not yet implemented")
    }

    override fun getSearchResult(): List<Map<String, String>> {
        TODO("Not yet implemented")
    }

    override fun <T : HostPlugin> T.doJob(): T {
        TODO("Not yet implemented")
    }

    override fun getVideoSrc(): List<HttpUrl> {
        TODO("Not yet implemented")
    }

    override fun getVideoDetails(): List<Map<String, String>> {
        TODO("Not yet implemented")
    }
}