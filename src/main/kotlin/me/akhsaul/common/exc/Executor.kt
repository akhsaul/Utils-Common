package me.akhsaul.common.exc

import me.akhsaul.common.io.Utils
import java.io.File

class Executor(binaryPath: String) {
    enum class STATE {
        WAITING,
        RUNNING,
        FINISHED,
        EXIT,
        FAILURE,
    }

    var output = ""
    private val binary: ProcessBuilder
    private var ready = false

    init {
        ready = if (File(binaryPath).exists()) {
            binary = ProcessBuilder(binaryPath)
            printVersion()
            true
        } else {
            throw Exception("ERROR. BINARY FILE NOT FOUND")
        }
    }

    private fun printVersion() {
        println(addArgument("-version").execute().output)
    }

    fun addArgument(vararg arg: String): Executor {
        if (ready) {
            binary.command(*arg)
        }
        return this
    }

    fun execute(): Executor {
        binary.start().also { output = Utils.readToString(it.inputStream) }
        return this
    }

    fun main() {
        val p1 = Runtime.getRuntime().exec("D:\\Clone_GitHub\\ffmpeg\\bin\\ffmpeg.exe -version")
        val a = ProcessBuilder("D:\\Clone_GitHub\\ffmpeg\\bin\\ffprobe.exe", "-version")
        val p2 = a.start()
        val inp1 = Utils.readToString(p1.inputStream)
        val inp2 = Utils.readToString(p2.inputStream)
        println(inp1)
        println(inp2)
        println(p1.exitValue())
        println(p2.exitValue())
    }
}