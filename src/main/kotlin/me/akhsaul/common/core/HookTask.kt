package me.akhsaul.common.core

import me.akhsaul.common.require
import me.akhsaul.common.tools.Sys

abstract class HookTask {
    init {
        require(Sys.initHook()) {
            IllegalStateException("Hook-Thread not initialized")
        }
    }
    abstract val taskName: String
    internal abstract fun runTask()
}