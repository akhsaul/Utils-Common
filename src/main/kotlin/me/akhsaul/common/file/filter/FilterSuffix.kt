package me.akhsaul.common.file.filter

import me.akhsaul.common.requireAtLeastOne
import java.io.File

class FilterSuffix(suffixes: List<String>, caseSensitivity: IOCase) : AbstractFileFilter() {
    private val suffixes: Array<String> = suffixes.requireAtLeastOne().toTypedArray()
    private val caseSensitivity: IOCase = caseSensitivity

    constructor(vararg suffixes: String) : this(suffixes.toList(), caseSensitivity = IOCase.SENSITIVE)

    constructor(suffixes: List<String>) : this(suffixes, IOCase.SENSITIVE)

    override fun accept(file: File): Boolean {
        if (suffixes.isNotEmpty()) {
            val name = file.name
            for (suffix in suffixes) {
                if (caseSensitivity.checkEndsWith(name, suffix)) {
                    return true
                }
            }
        }
        return false
    }

    /**
     * Checks to see if the file name ends with the suffix.
     *
     * @param dir  the File directory
     * @param name  the file name
     * @return true if the file name ends with one of our suffixes
     */
    override fun accept(dir: File, name: String): Boolean {
        if (suffixes.isNotEmpty()) {
            for (suffix in suffixes) {
                if (caseSensitivity.checkEndsWith(name, suffix)) {
                    return true
                }
            }
        }
        return false
    }

    /**
     * Provide a String representation of this file filter.
     *
     * @return a String representation
     */
    override fun toString(): String {
        return buildStr(suffixes)
    }
}