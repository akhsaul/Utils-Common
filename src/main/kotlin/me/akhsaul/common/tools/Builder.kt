package me.akhsaul.common.tools

import me.akhsaul.common.*

object Builder {
    class Strings @JvmOverloads constructor(capacity: Int = 8, str: String? = null) {
        private val build = StringBuilder(capacity)
        val length: Int
            get() = build.length

        init {
            appendNotNull(str)
        }

        operator fun set(index: Int, value: Char) = apply {
            build[index] = value
        }

        operator fun get(index: Int) = build[index]

        fun appendNotNull(obj: Any?) = apply {
            build.appendNotNull(obj)
        }

        fun append(obj: Any?) = apply {
            build.append(obj)
        }

        fun append(b: Boolean) = apply {
            build.append(b)
        }

        fun append(i: Int) = apply {
            build.append(i)
        }

        fun append(l: Long) = apply {
            build.append(l)
        }

        fun append(sb: StringBuffer) = apply {
            build.append(sb)
        }

        fun append(cs: CharSequence) = apply {
            build.append(cs)
        }

        fun append(str: CharArray) = apply {
            build.append(str)
        }

        fun append(d: Double) = apply {
            build.append(d)
        }

        fun append(f: Float) = apply {
            build.append(f)
        }

        fun appendClass(obj: Any?) = apply {
            build.appendClass(obj)
        }

        /**
         * this function get char in index 0 then calling [Character.toUpperCase]
         * */
        fun upperFirst() = apply {
            build.upperFirst()
        }

        /**
         * this function get char in index 0 then calling [Character.toLowerCase]
         * */
        fun lowerFirst() = apply {
            build.lowerFirst()
        }

        fun space() = apply {
            build.space()
        }

        fun dot() = apply {
            build.dot()
        }

        fun lastIndexOf(c: Char): Int {
            val max = build.lastIndex
            for (i in max downTo 0) {
                if (build[i] == c) {
                    return i
                }
            }
            return -1
        }

        fun dotEnd() = apply {
            if (build.last() != '.') {
                build.dot()
            }
        }

        fun comma() = apply {
            build.comma()
        }

        fun toStringBuilder(): java.lang.StringBuilder = build

        @JvmOverloads
        fun getChars(dst: CharArray, dstBegin: Int = 0, srcBegin: Int = 0, srcEnd: Int = length) = apply {
            getChars(srcBegin, srcEnd, dst, dstBegin)
        }

        fun getChars(srcBegin: Int, srcEnd: Int, dst: CharArray, dstBegin: Int) = apply {
            build.getChars(srcBegin, srcEnd, dst, dstBegin)
        }

        fun toCharArray(): CharArray = CharArray(build.length).apply {
            getChars(this)
        }

        fun setLength(new: Int) = apply {
            build.setLength(new)
        }

        override fun toString(): String = build.toString()
    }
}

private fun Any?.println(){
    kotlin.io.println(this)
}

fun main() {
    val b = Builder.Strings()
    val a = "\uD869\uDF6E\uD851\uDDA3\uD878\uDF8C"
    println(a.get(4).code)
    println(a.get(5).code)
    println(a.get(4).code + a.get(5).code)
    println(Character.toCodePoint(a.get(4), a.get(5)))
    String(charArrayOf(a.get(4), a.get(5))).println()
    a.println()
    //b.append("\uD869\uDF6E").append('a').append('b')
    b.append('a').append('b')
    b.upperFirst().println()
}