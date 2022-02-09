package me.akhsaul.common

import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import me.akhsaul.common.math.DataSize
import me.akhsaul.common.tools.Sys
import org.slf4j.Logger
import java.io.File
import kotlin.system.measureNanoTime
import kotlin.time.Duration.Companion.seconds
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.toDuration

private var tmpFile: File? = null
private var LOG: Logger? = null

internal fun debugLogger(func: () -> Unit): Logger {
    return logger(func)
}

@OptIn(ExperimentalTime::class)
@Synchronized
fun prepareTempFile(log: Logger): File {
    LOG = log
    if (tmpFile == null) {
        LOG?.info("Prepare temporarily file")
        val tmp = makeTempFile("File_", ".test", subDir = "Test").toFile()
        tmp.deleteOnExit()
        fillData(5, tmp)
        tmpFile = tmp
    }
    LOG?.info("Temporarily file is ready, $tmpFile")
    runBlocking {
        delay(10.seconds)
    }
    return notNull(tmpFile)
}

private fun fillData(totalRepeat: Int, file: File) {
    val str = "HeLlO WoRlD!"
    val data = StringBuilder()
    val n = 1000
    val k = 1000
    var counter = 0
    try {
        runBlocking {
            repeat(totalRepeat) {
                CoroutineScope(Dispatchers.IO).launch {
                    massiveRun(n, k) {
                        data.append("#${counter + 1} $str\n")
                        counter++
                    }
                }.join()
            }
        }
        require(counter == ((n * k) * totalRepeat)) {
            IllegalStateException("counter = $counter, run = ${((n * k) * totalRepeat)}")
        }

        LOG?.info("counter = $counter, run = ${((n * k) * totalRepeat)}")

        val transferred = DataSize(
            data.toString().buffered(MAX_DISK_BUFFER_SIZE).transfer(
                file.bufferedOutput(MAX_DISK_BUFFER_SIZE, true), MAX_DISK_BUFFER_SIZE
            )
        )


        LOG?.info("data transfered = $transferred")

        LOG?.info("file size ${file.size}")

    } catch (e: Throwable) {
        throw e
    } finally {
        Sys.gc()
    }
}

private val mutex = Mutex()

/**
 * @param n number of coroutines to launch
 * @param k times an action is repeated by each coroutine
 * */
private suspend fun massiveRun(n: Int = 100, k: Int = 1000, lock: Boolean = true, action: suspend () -> Unit) {
    val time = measureNanoTime {
        coroutineScope { // scope for coroutines
            repeat(n) {
                launch {
                    repeat(k) {
                        if (lock) {
                            // protect each increment with lock
                            mutex.withLock {
                                action()
                            }
                        } else {
                            action()
                        }
                    }
                }.join()
            }
        }
    }.toDuration(DurationUnit.NANOSECONDS)
    println("Completed ${n * k} actions in $time")
}