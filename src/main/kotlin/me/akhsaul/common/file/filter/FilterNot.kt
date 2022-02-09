package me.akhsaul.common.file.filter

import java.io.File

class FilterNot(
    private val filter: IOFileFilter
) : AbstractFileFilter() {

    override fun accept(file: File): Boolean {
        return !filter.accept(file)
    }

    override fun accept(dir: File, name: String): Boolean {
        return !filter.accept(dir, name)
    }

    override fun toString(): String {
        return super.toString() + "(" + filter.toString() + ")"
    }
}