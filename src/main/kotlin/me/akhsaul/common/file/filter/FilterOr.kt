package me.akhsaul.common.file.filter

import java.io.File

class FilterOr(
    private val fileFilters: MutableList<IOFileFilter>
) : AbstractFileFilter(), ConditionalFileFilter {
    /** The list of file filters.  */
    override val filters: List<IOFileFilter> = fileFilters

    constructor(filter1: IOFileFilter, filter2: IOFileFilter) : this(ArrayList(2)) {
        addFileFilter(filter1)
        addFileFilter(filter2)
    }

    override fun addFileFilter(ioFileFilter: IOFileFilter) {
        fileFilters.add(ioFileFilter)
    }

    override fun removeFileFilter(ioFileFilter: IOFileFilter): Boolean {
        return fileFilters.remove(ioFileFilter)
    }

    override fun accept(file: File): Boolean {
        for (fileFilter in fileFilters) {
            if (fileFilter.accept(file)) {
                return true
            }
        }
        return false
    }

    override fun accept(dir: File, name: String): Boolean {
        for (fileFilter in fileFilters) {
            if (fileFilter.accept(dir, name)) {
                return true
            }
        }
        return false
    }

    override fun toString(): String {
        return buildStr(fileFilters)
    }
}