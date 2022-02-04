package me.akhsaul.common.core

import me.akhsaul.common.containsOnlyOneOf
import java.io.BufferedReader
import java.io.File
import java.util.stream.Stream

class CommandLine() {
    private var processBuilder: ProcessBuilder? = null
    private var useProcessDestroyer: Boolean = false
    private var streams: StreamHandler? = null

    private constructor(
        processBuilder: ProcessBuilder,
        useProcessDestroyer: Boolean,
        streamsHandler: StreamHandler?
    ) : this() {
        this.processBuilder = processBuilder
        this.useProcessDestroyer = useProcessDestroyer
        this.streams = streamsHandler
    }

    open class ResultHandler(private val commandLine: CommandLine) {
        private val INVALID_EXITVALUE = -559038737
        private val process: Process = requireNotNull(commandLine.processBuilder) {
            throw IllegalArgumentException("Please call CommandLine.Builder() before calling start()")
        }.start().apply {
            if (commandLine.useProcessDestroyer) {
                try {
                    ProcessDestroyer.add(this)
                }catch (t: Throwable){
                    //ignore
                }
            }
        }
        private var caught: Throwable? = null
        private var reader: BufferedReader? = null
        var exitValue = INVALID_EXITVALUE

        init {
            commandLine.streams?.let {
                it.setProcessInputStream(process.outputStream)
                it.setProcessOutputStream(process.inputStream)
                it.setProcessErrorStream(process.errorStream)
            }
            if (commandLine.streams == null) {
                reader = process.inputReader()
            }
        }

        private fun finish(cmd: CommandLine) {
            exitValue = process.waitFor()
            if (cmd.useProcessDestroyer) {
                try {
                    ProcessDestroyer.remove(process)
                }catch (t: Throwable){
                    //ignore
                }
            }
        }

        /**
         * Read output while process running,
         *
         * Don't forget to close the Stream
         * */
        fun read(): Stream<String> {
            return try {
                requireNotNull(reader).lines()
            } catch (t: Throwable) {
                caught = t
                Stream.empty()
            } finally {
                finish(commandLine)
            }
        }

        @OptIn(ExperimentalStdlibApi::class)
        fun readAsList(): List<String> {
            return buildList {
                read().use {
                    it.forEach { s ->
                        add(s)
                    }
                }
            }
        }

        /**
         * Wait the process until finished, read all output, then convert into String
         * */
        fun readAsString(): String {
            return buildString {
                read().use {
                    it.forEach { s ->
                        append(s)
                    }
                }
            }
        }
    }

    open class Builder {
        private var exe: String? = null
        private var arguments: List<String> = listOf()
        private var useProcessDestroyer: Boolean = false
        private var streams: StreamHandler? = null

        constructor(executable: String) {
            exe = executable
        }

        constructor(executable: File) : this(executable.absolutePath) {
            require(executable.exists())
            require(executable.isFile)
            require(executable.canExecute())
        }

        fun addArgument(vararg argument: String) = apply {
            addArgument(argument.toList())
        }

        @OptIn(ExperimentalStdlibApi::class)
        fun addArgument(argument: List<String>) = apply {
            this.arguments = buildList {
                addAll(arguments)
                addAll(argument)
            }
        }

        fun setProcessDestroyer(useProcessDestroyer: Boolean) = apply {
            this.useProcessDestroyer = useProcessDestroyer
        }

        fun setStreamHandler(streamsHandler: StreamHandler) = apply {
            streams = streamsHandler
        }

        @OptIn(ExperimentalStdlibApi::class)
        fun build(): CommandLine {
            requireNotNull(exe) {
                throw IllegalArgumentException("Please set executable file")
            }
            return CommandLine(
                ProcessBuilder(
                    buildList {
                        if (exe!!.containsOnlyOneOf( "cmd", "cmd.exe")) {
                            add("cmd.exe")
                            add("/c")
                            addAll(arguments)
                        }
                    }
                ), useProcessDestroyer, streams
            )
        }
    }

    fun setProcessDestroyer(useProcessDestroyer: Boolean) = apply {
        this.useProcessDestroyer = useProcessDestroyer
    }

    fun setStreamHandler(streamsHandler: StreamHandler) = apply {
        streams = streamsHandler
    }

    fun toBuilder(executable: String): Builder {
        return Builder(executable).apply {
            setProcessDestroyer(useProcessDestroyer)
            streams?.let { setStreamHandler(it) }
        }
    }

    fun toBuilder(executable: File): Builder {
        require(executable.exists())
        require(executable.isFile)
        require(executable.canExecute())
        return toBuilder(executable.absolutePath)
    }

    fun start(): ResultHandler {
        return ResultHandler(this)
    }
}