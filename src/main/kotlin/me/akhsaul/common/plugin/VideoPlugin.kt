package me.akhsaul.common.plugin

import okhttp3.HttpUrl
import me.akhsaul.common.enum.VideoQuality

interface VideoPlugin : HostPlugin {
    var quality: VideoQuality
    fun getVideoSrc(): List<HttpUrl>
    fun getVideoDetails(): List<Map<String, String>>
}