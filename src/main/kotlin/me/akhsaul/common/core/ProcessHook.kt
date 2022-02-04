package me.akhsaul.common.core

interface ProcessHook {
    val size: Int
    fun add(process: Process): Boolean
    fun remove(process: Process): Boolean
}