package me.akhsaul.common.file.filter

import java.io.File

class FilterAnd : AbstractFileFilter, ConditionalFileFilter {
    private var fileFilters: MutableList<IOFileFilter> = ArrayList(2)
    override val filters: List<IOFileFilter> = fileFilters

    constructor()
    constructor(filters: List<IOFileFilter>) {
        fileFilters = filters.toMutableList()
    }

    constructor(filter1: IOFileFilter, filter2: IOFileFilter) {
        addFileFilter(filter1)
        addFileFilter(filter2)
    }

    /**
     * {@inheritDoc}
     */
    override fun addFileFilter(ioFileFilter: IOFileFilter) {
        fileFilters.add(ioFileFilter)
    }

    /**
     * {@inheritDoc}
     */
    override fun removeFileFilter(ioFileFilter: IOFileFilter): Boolean {
        return fileFilters.remove(ioFileFilter)
    }

    /**
     * {@inheritDoc}
     */
    override fun accept(file: File): Boolean {
        if (fileFilters.isEmpty()) {
            return false
        }
        for (fileFilter in fileFilters) {
            if (!fileFilter.accept(file)) {
                return false
            }
        }
        return true
    }

    /**
     * {@inheritDoc}
     */
    override fun accept(dir: File, name: String): Boolean {
        if (fileFilters.isEmpty()) {
            return false
        }
        for (fileFilter in fileFilters) {
            if (!fileFilter.accept(dir, name)) {
                return false
            }
        }
        return true
    }

    /**
     * Provide a String representation of this file filter.
     *
     * @return a String representation
     */
    override fun toString(): String {
        return buildStr(filters)
    }
}