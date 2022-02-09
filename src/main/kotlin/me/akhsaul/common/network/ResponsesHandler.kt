package me.akhsaul.common.network

import java.util.*
import java.util.concurrent.ConcurrentHashMap


class ResponsesHandler : Thread() {
    override fun run() {
        super.run()
        val data: Set<String> = Collections.newSetFromMap(ConcurrentHashMap())
    }
}