package me.akhsaul.common.core

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import me.akhsaul.common.tools.Sys
import kotlin.time.Duration

/**
 * Use this class if you want shutdown OS by timeSeconds or when JVM Exit
 * */
object ShutdownOS : HookTask() {
    override val taskName: String
        get() = "Shutdown OS"
    private var cmd: CommandLine? = CommandLine()
    private var millis: Long = 0

    /**
     *
     * @param timeMillis - wait for milliSeconds before shutdown.
     * @param untilJvmExit - true, mean OS will shut down when JVM Exit.
     * false, mean Shutdown will be started right now (depend on timeMillis)
     * */
    fun waitFor(timeMillis: Long, untilJvmExit: Boolean = false) {
        require(timeMillis in 0..315360000000) {
            IllegalArgumentException("Need more -1, only accept 0 millis until 10 years (315360000000 millis)")
        }
        checkNotNull(cmd) {
            "Shutdown OS in progress. Too late"
        }

        cmd = if (Sys.isWindows) {
            cmd?.toBuilder("cmd")
                ?.addArgument("shutdown /s /f")?.build()
        } else if (Sys.isLinux) {
            cmd?.toBuilder("??")?.addArgument("shutdown now")?.build()
        } else {
            null
        }
        millis = timeMillis

        if (untilJvmExit) {
            Sys.addHookTask(this)
        } else {
            runTask()
        }
    }

    /**
     *
     * @param timeMillis - wait for milliSeconds before shutdown.
     * @param untilJvmExit - true, mean OS will shut down when JVM Exit.
     * false, mean Shutdown will be started right now (depend on timeMillis)
     * */
    fun waitFor(timeMillis: Duration, untilJvmExit: Boolean) {
        val time = if (timeMillis > Duration.ZERO) timeMillis.inWholeMilliseconds.coerceAtLeast(1) else 0
        waitFor(time, untilJvmExit)
    }

    override fun runTask() {
        var cli: CommandLine?
        var delay: Long
        synchronized(ShutdownOS::class.java) {
            cli = cmd
            delay = millis
            cmd = null
        }

        runBlocking {
            // linux does not support shutdown in second, only shutdown in hour or minute
            // delay before shutdown OS
            delay(delay)
            cli?.start()
        }
    }
}