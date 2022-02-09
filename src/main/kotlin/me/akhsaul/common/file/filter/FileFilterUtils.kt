package me.akhsaul.common.file.filter

object FileFilterUtils {
    fun and(vararg filters: IOFileFilter): IOFileFilter {
        return FilterAnd(filters.toMutableList())
    }

    fun or(vararg filters: IOFileFilter): IOFileFilter {
        return FilterOr(filters.toMutableList())
    }

    fun not(filter: IOFileFilter): IOFileFilter {
        return FilterNot(filter)
    }
}