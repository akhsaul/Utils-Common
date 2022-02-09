package me.akhsaul.common.tools

import me.akhsaul.common.*
import me.akhsaul.common.core.CommandLine
import me.akhsaul.common.core.HookTask
import me.akhsaul.common.core.ShutdownOS
import me.akhsaul.common.exception.ClassLoaderNotFoundException
import java.awt.GraphicsEnvironment
import java.io.BufferedInputStream
import java.util.*
import kotlin.io.path.Path
import kotlin.io.path.absolutePathString
import kotlin.io.path.isReadable
import kotlin.io.path.isWritable
import kotlin.system.exitProcess
import kotlin.text.contains
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

@Suppress("unused")
object Sys {
    private val LOG = logger { }
    private var hookReady = false
    private val tasks = mutableSetOf<HookTask>()

    /**
     * Adding custom Task for running in Java Shutdown Hook
     * */
    @Synchronized
    @JvmStatic
    fun addHookTask(task: HookTask): Boolean {
        tasks.add(task)
        return tasks.contains(task)
    }

    @Synchronized
    @JvmStatic
    internal fun initHook(): Boolean {
        if (!hookReady) {
            hookReady = runCatching {
                val thread = object : Thread("Hook-Thread") {
                    override fun run() {
                        tasks.forEach { task ->
                            runCatching {
                                task.runTask()
                            }.getOrElse {
                                throw Error("TaskName = ${task.taskName}", it)
                            }
                        }
                    }
                }
                thread.priority = Thread.MAX_PRIORITY
                addShutdownHook(thread)
                true
            }.getOrDefault(false)
        }

        return hookReady
    }

    @JvmField
    val runtime: Runtime = Runtime.getRuntime()

    @JvmField
    val javaVersion = property("java.version")

    @JvmField
    val javaVmName = property("java.vm.name")

    @JvmField
    val javaVmInfo = property("java.vm.info")

    @JvmField
    val javaVmVersion = property("java.vm.version")

    @JvmField
    val javaVendor = property("java.vendor")

    @JvmField
    val javaVendorVersion = property("java.vendor.version")

    @JvmField
    val javaClassVersion = property("java.class.version")

    @JvmField
    val javaRuntimeName = property("java.runtime.name")

    @JvmField
    val locale = Locale.getDefault().toString()

    @JvmField
    val osName = validateWin(property("os.name"))

    @JvmField
    val osVersion = property("os.version")

    @JvmField
    val osArchitect = property("os.arch")

    @JvmField
    val osArchitectModel = property("sun.arch.data.model")

    @JvmField
    val osPatch = property("sun.os.patch.level", " ")

    @JvmField
    val totalProcessor = runtime.availableProcessors()

    @JvmField
    val maxRam = runtime.maxMemory()

    @JvmField
    val freeRam = runtime.freeMemory()

    @JvmField
    val totalRam = runtime.totalMemory()

    @JvmField
    val userHome = property("user.home")

    @JvmField
    val javaHome = property("java.home")

    @JvmField
    val fileSeparator = property("file.separator")

    @JvmField
    val fileEncoding = property("file.encoding")

    @JvmField
    val lineSeparator = property("line.separator")

    @JvmField
    val tmpDir = property("java.io.tmpdir")

    @JvmStatic
    val appTmpDir: String
        get() {
            return Path(tmpDir).let {
                if (it.isReadable() && it.isWritable()) {
                    it
                } else {
                    Path("")
                }
            }.absolutePathString() + "/MyAPICommon/"
        }

    @JvmField
    val envPath = property("java.library.path")

    @JvmField
    val classPath = property("java.class.path")

    @JvmField
    val operatingSystem = "os = $osName $osVersion$osPatch, architecture: $osArchitect $osArchitectModel-bit"

    @JvmField
    val corePoolSize = totalProcessor.coerceAtLeast(2)

    @JvmField
    val maxPoolSize = (totalProcessor * 128).coerceIn(corePoolSize, (1 shl 21) - 2)

    @JvmField
    val isWindows = osName.startsWith("windows", true)

    @JvmField
    val isLinux = osName.startsWith("linux", true)

    @JvmField
    val isMac = osName.startsWith("mac", true)

    @JvmStatic
    fun gc() {
        System.gc()
    }

    @JvmStatic
    fun addShutdownHook(hook: Thread?) {
        runtime.addShutdownHook(hook)
    }

    @JvmStatic
    fun removeShutdownHook(hook: Thread?): Boolean {
        return runtime.removeShutdownHook(hook)
    }

    /**
     * @param timeMillis - a Long represent milliSeconds
     * @see ShutdownOS.waitFor
     * */
    @OptIn(ExperimentalTime::class)
    @JvmStatic
    fun shutdownOS(timeMillis: Long, untilJvmExit: Boolean) {
        if (isWindows || isLinux) {
            ShutdownOS.waitFor(timeMillis, untilJvmExit)
        }
    }

    /**
     * @param timeMillis - a Duration represent milliSeconds
     * @see ShutdownOS.waitFor
     * */
    @OptIn(ExperimentalTime::class)
    @JvmStatic
    fun shutdownOS(timeMillis: Duration, untilJvmExit: Boolean) {
        if (isWindows || isLinux) {
            ShutdownOS.waitFor(timeMillis, untilJvmExit)
        }
    }

    /***
     * Windows 11 is called Win 10 build 22000
     * we should validate if os.name return Windows 10 or Windows 11
     */
    @OptIn(ExperimentalStdlibApi::class)
    @JvmStatic
    private fun validateWin(osName: String): String {
        var validOs = osName

        if (osName.startsWith("windows", true) && osName.contains("10")) {
            val lines = CommandLine.Builder("cmd").setProcessDestroyer(true)
                .addArgument("systeminfo /FO CSV").build()
                .start().readAsList()

            if (lines.isNotEmpty()) {
                val key = removeAndSplit(lines[0])
                val value = removeAndSplit(lines[1])
                for (i in 0..key.size) {
                    if (key[i] == "OS Name") {
                        validOs = value[i]
                        break
                    }
                }
            }
        }

        return validOs
    }

    private fun removeAndSplit(line: String): List<String> {
        return line.replaceFirst("\"", "").replaceLast("\"", "").split("\",\"")
    }

    @OptIn(ExperimentalStdlibApi::class)
    @JvmStatic
    fun allProperty(): Map<String, String> {
        return buildMap {
            System.getProperties().entries.forEach {
                put("${it.key}", "${it.value}")
            }
        }
    }

    @OptIn(ExperimentalStdlibApi::class)
    @JvmStatic
    fun allEnvironment(): Map<String, String> {
        return buildMap {
            System.getenv().forEach {
                put(it.key, it.value)
            }
        }
    }

    @JvmOverloads
    @JvmStatic
    fun property(key: String, prefix: String = ""): String {
        val value = runCatching {
            System.getProperty(key)
        }.getOrDefault("")

        return if (value != "") "$prefix$value" else value
    }

    @JvmOverloads
    @JvmStatic
    fun environment(key: String, prefix: String = ""): String {
        val value = runCatching {
            System.getenv(key)
        }.getOrDefault("")

        return if (value != "") "$prefix$value" else value
    }

    val isHeadless = GraphicsEnvironment.isHeadless()

    @JvmStatic
    fun error(vararg obj: Any) {
        if (obj.isNotEmpty()) {
            val msg = buildString {
                obj.forEach {
                    append(it)
                    append("\n")
                }
            }
            System.err.print(msg)
        }
    }

    @JvmStatic
    fun errorln(vararg obj: Any) {
        if (obj.isNotEmpty()) {
            val msg = buildString {
                obj.forEach {
                    append(it)
                    append("\n")
                }
            }
            System.err.println(msg)
        }
    }

    @JvmOverloads
    @JvmStatic
    fun getClassLoader(myClass: Any = this): ClassLoader {
        val loader: ClassLoader? = catch(null) {
            //try with myClass loader
            myClass.javaClass.classLoader
        } ?: catch(null) {
            //try with thread context
            Thread.currentThread().contextClassLoader
        } ?: catch(null) {
            //try with system loader
            ClassLoader.getSystemClassLoader()
        } ?: catch(null) {
            //last chance to try to get class loader
            ClassLoader.getPlatformClassLoader()
        }

        //if still null, throw an exception
        return notNull(loader) {
            ClassLoaderNotFoundException()
        }
    }

    @JvmStatic
    fun getResourceStream(name: String): BufferedInputStream {
        return Resource<BufferedInputStream>().get(name)
    }

    @JvmStatic
    fun getResourceData(name: String): Data {
        return Resource<Data>().get(name)
    }

    @JvmOverloads
    @JvmStatic
    fun exit(statusCode: Int = 0) {
        exitProcess(statusCode)
    }
}