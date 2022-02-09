package me.akhsaul.common.file.filter

interface ConditionalFileFilter {
    fun addFileFilter(ioFileFilter: IOFileFilter)
    val filters: List<IOFileFilter>
    fun removeFileFilter(ioFileFilter: IOFileFilter): Boolean
}