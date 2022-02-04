package me.akhsaul.common.plugin

import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import me.akhsaul.common.network.Clients
import me.akhsaul.common.network.Clients.Setting.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory

interface HostPlugin {
    /**
     * client for doing the job
     *
     * you can override this
     * */
    val client: OkHttpClient
        get() = Clients.Builder().setClient(ENABLE_COOKIE, USE_DEFAULT_CACHE).build()

    /**
     * DO NOT "OVERRIDE" THIS!!
     *
     * use this for logger
     * */
    val LOG: Logger
        get() = LoggerFactory.getLogger(this.javaClass)

    /**
     * name of plugin
     * */
    val name: String
        get() {
            return "Plugin - ${this.javaClass.simpleName}"
        }

    /**
     * url that use for doing the job
     * */
    val url: HttpUrl

    /**
     * base of URL plugin
     * */
    val baseUrl: String

    /**
     * @return T that bounds with HostPlugin
     * */
    fun <T : HostPlugin> T.doJob(): T

    /**
     * determine given url is valid for this plugin or not.
     *
     * if valid then given url can be use for this plugin.
     * @param url for validate
     * @return boolean - valid or not
     * */
    fun isAccepted(url: String): Boolean
}