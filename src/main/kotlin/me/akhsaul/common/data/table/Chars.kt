package me.akhsaul.common.data.table

import kotlin.math.pow

abstract class Chars {
    val codePoint: List<Int> = buildList {
        hexadecimal().forEach {
            add(it.toDecimal())
        }
    }

    abstract fun hexadecimal(): List<String>

    private fun String.toDecimal(): Int {
        var n = length - 1
        var result = 0
        this.forEach {
            result += it.digitToInt(16).times(
                (16f).pow(n)
            ).toInt()
            n--
        }
        return result
    }
}