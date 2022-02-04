package me.akhsaul.common.core

import me.akhsaul.common.notNull
import java.io.File
import java.lang.IllegalStateException
import java.nio.file.Path
import java.util.LinkedHashSet

object DeleteOnExit : HookTask() {
    override val taskName: String
        get() = "Delete File or Folder on Exit"
    private var files: LinkedHashSet<String>? = LinkedHashSet()

    override fun runTask() {
        val theFiles: LinkedHashSet<String>
        synchronized(DeleteOnExit::class.java) {
            theFiles = notNull(files)
            files = null
        }
        theFiles.forEach {
            val file = File(it)
            if (file.exists()) {
                if (file.isFile) {
                    file.delete()
                } else {
                    file.deleteRecursively()
                }
            }
        }
    }

    @JvmStatic
    fun add(path: Path) {
        add(path.toFile())
    }

    @JvmStatic
    fun add(file: File) {
        notNull(files) {
            IllegalStateException("Hook is Running. Deletion in progress. Too late to add a file")
        }.add(file.path)
    }

    @JvmStatic
    fun addAll(vararg path: Path) {
        path.forEach {
            add(it)
        }
    }

    @JvmStatic
    fun addAll(vararg file: File) {
        file.forEach {
            add(it)
        }
    }

    @JvmStatic
    fun remove(path: Path) {
        remove(path.toFile())
    }

    @JvmStatic
    fun remove(file: File) {
        notNull(files) {
            IllegalStateException("Hook is Running. Deletion in progress. Too late to add a file")
        }.remove(file.path)
    }

    @JvmStatic
    fun removeAll(vararg path: Path){
        path.forEach {
            remove(it)
        }
    }

    @JvmStatic
    fun removeAll(vararg file: File){
        file.forEach {
            remove(it)
        }
    }
}