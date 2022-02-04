package me.akhsaul.common.plugin

import me.akhsaul.common.*
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl
import me.akhsaul.common.enum.BOCAction
import me.akhsaul.common.enum.BOCAction.*

class BOC(private val email: String, private val password: String): ScrapperPlugin {
    override val baseUrl: String = "http://www.elearning.ubpkarawang.ac.id"
    override val url: HttpUrl = baseUrl.toHttpUrl()
    private var action: BOCAction = PRESENCE

    override fun isAccepted(url: String): Boolean {
        TODO("Not yet implemented")
    }

    override fun <T : HostPlugin> T.doJob(): T {
        require(isAccepted(url.toString()))

        return TODO("Not yet implemented")
    }

    fun changeAction(action: BOCAction){
        this.action = action
    }

    private fun presence(){

    }
}