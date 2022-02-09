package me.akhsaul.common.file.filter

import java.io.File

abstract class AbstractFileFilter : IOFileFilter {
    /**
     * Checks to see if the File should be accepted by this filter.
     *
     * @param file  the File to check
     * @return true if this file matches the test
     */
    override fun accept(file: File): Boolean {
        return accept(file.parentFile, file.name)
    }

    /**
     * Checks to see if the File should be accepted by this filter.
     *
     * @param dir  the directory File to check
     * @param name  the file name within the directory to check
     * @return true if this file matches the test
     */
    override fun accept(dir: File, name: String): Boolean {
        return accept(File(dir, name))
    }

    /**
     * Provide a String representation of this file filter.
     *
     * @return a String representation
     */
    override fun toString(): String {
        return javaClass.simpleName
    }

    fun buildStr(data: Array<String>): String {
        val buffer = StringBuilder()
        buffer.append(super.toString())
        buffer.append("(")
        if (data.isNotEmpty()) {
            for (i in data.indices) {
                if (i > 0) {
                    buffer.append(",")
                }
                buffer.append(data[i])
            }
        }
        buffer.append(")")
        return buffer.toString()
    }

    fun buildStr(data: List<IOFileFilter>): String {
        val tmp = arrayListOf<String>()
        data.forEach {
            tmp.add(it.toString())
        }
        return buildStr(tmp.toTypedArray())
    }
}