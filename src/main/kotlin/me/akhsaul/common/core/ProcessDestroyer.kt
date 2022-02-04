package me.akhsaul.common.core

import me.akhsaul.common.notNull
import java.util.*
import kotlin.jvm.Throws

/**
 * Only use this if you want to destroy a process when JVM exit
 * */
@Suppress("unused")
object ProcessDestroyer : HookTask() {
    override val taskName: String
        get() = "Process Destroyer"
    private var processes: LinkedHashSet<Process>? = LinkedHashSet()

    @Synchronized
    fun add(process: Process): Boolean {
        return notNull(processes) {
            IllegalStateException("Shutdown Hook is Running. Process Destroyer in progress. Too late to add a process")
        }.let {
            it.add(process)
            // make sure process has been added
            it.contains(process)
        }
    }

    @Throws(IllegalStateException::class)
    @Synchronized
    fun remove(process: Process): Boolean {
        return notNull(processes) {
            IllegalStateException("Shutdown Hook is Running. Process Destroyer in progress. Too late to remove a process")
        }.remove(process)
    }

    @Synchronized
    fun addAll(vararg processes: Process) {
        processes.forEach {
            add(it)
        }
    }

    @Synchronized
    fun removeAll(vararg processes: Process) {
        processes.forEach {
            remove(it)
        }
    }

    override fun runTask() {
        var theProcess: LinkedHashSet<Process>
        synchronized(ProcessDestroyer::class.java) {
            theProcess = notNull(processes)
            processes = null
        }

        theProcess.forEach {
            try {
                it.destroy()
            } catch (t: Throwable){
                System.err.println("Unable to terminate process during process shutdown")
                t.printStackTrace()
            }
        }
    }
}
