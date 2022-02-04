package me.akhsaul.common.tools

import me.akhsaul.common.data.table.ASCII
import me.akhsaul.common.data.table.Chars
import kotlin.random.Random
import kotlin.random.asKotlinRandom

class Randomizer() {
    private var random: Random = Random

    constructor(random: java.util.Random) : this() {
        this.random = random.asKotlinRandom()
    }

    class StringBuilder() {
        private var random: Random = Random
        private var codePoints = mutableSetOf<Int>()
        private var filters = mutableSetOf<(codePoint: Int) -> Boolean>()

        constructor(random: java.util.Random) : this() {
            this.random = random.asKotlinRandom()
        }

        fun range(minimumCodePoint: Int, maximumCodePoint: Int) = apply {
            require(minimumCodePoint <= maximumCodePoint) {
                throw IllegalArgumentException("Parameter minimum ($minimumCodePoint) must be greater than maximum ($maximumCodePoint)")
            }
            codePoints.addAll(minimumCodePoint..maximumCodePoint)
        }

        fun range(minChar: Char, maxChar: Char) = apply {
            range(minChar.code, maxChar.code)
        }

        /**
         * Creates a random string whose length is the number of characters
         * specified.
         *
         * Characters will be chosen from the set of characters whose
         * ASCII value is between 32 and 126 (inclusive).
         */
        fun fromAscii() = selectFrom(ASCII)

        fun selectFrom(charset: Chars) = apply {
            codePoints.addAll(charset.codePoint)
        }

        fun selectFrom(vararg char: Char) = apply {
            char.forEach {
                selectFrom(it.code)
            }
        }

        fun selectFrom(vararg codePoint: Int) = apply {
            codePoint.forEach {
                codePoints.add(it)
            }
        }

        fun only(letters: Boolean, numbers: Boolean) = apply {
            val filter = if (letters && numbers) {
                { codePoint: Int -> Character.isLetterOrDigit(codePoint) }
            } else if (letters) {
                { codePoint: Int -> Character.isLetter(codePoint) }
            } else {
                { codePoint: Int -> Character.isDigit(codePoint) }
            }
            fromAscii().filteredBy(filter)
        }

        /**
         * every codepoint match with this filter will be use for generator,
         * if codepoint does not match with this filter then codepoint will not be used
         * @param filter filter to be use when randomize codepoint, if true then codepoint will be accepted,
         * if false then codepoint will be skipped
         * */
        fun filteredBy(vararg filter: (codePoint: Int) -> Boolean) = apply {
            filters.addAll(filter)
        }

        fun exclude(vararg char: Char) = apply {
            char.forEach {
                exclude(it.code)
            }
        }

        fun exclude(vararg codePoint: Int) = apply {
            codePoint.forEach {
                filteredBy({ codePoint -> codePoint != it })
            }
        }

        fun generateCharArray(length: Int): CharArray {
            if (length <= 0) {
                throw IllegalArgumentException("Length $length is smaller than zero.")
            } else {
                val chars = CharArray(length)
                var i = 0
                do {
                    val codePoint: Int = if (codePoints.isNotEmpty()) {
                        codePoints.random()
                    } else {
                        random.nextInt(0, Character.MAX_CODE_POINT)
                    }

                    when (Character.getType(codePoint).toByte()) {
                        Character.UNASSIGNED, Character.PRIVATE_USE, Character.SURROGATE -> continue // skip
                        else -> {
                            // let's go
                        }
                    }

                    if (filters.isNotEmpty()) {
                        var skip = 0
                        for (n in 0 until filters.size) {
                            val filter = filters.elementAt(n)
                            if (!filter(codePoint)) {
                                ++skip
                            }
                        }
                        if (skip > 0) {
                            // skip
                            continue
                        }
                    }
                    chars[i] = codePoint.toChar()
                    i++
                } while (i != length)
                return chars
            }
        }

        /**
         * @param minLength minimum length (inclusive) for generate string, should be more than zero
         * @param maxLength maximum length (inclusive) for generate string
         * @return Char Array represent String Randomized
         * */
        fun generateCharArray(minLength: Int, maxLength: Int): CharArray {
            return generateCharArray(random.nextInt(minLength, maxLength + 1))
        }

        /**
         * @param minLength minimum length (inclusive) for generate string, should be more than zero
         * @param maxLength maximum length (inclusive) for generate string
         * @return String Randomized
         * */
        fun generate(minLength: Int, maxLength: Int): String {
            return generate(random.nextInt(minLength, maxLength + 1))
        }

        fun generate(length: Int): String {
            return String(generateCharArray(length))
        }
    }

    fun generateUserAgent(): String {
        val data = me.akhsaul.common.data.UserAgent.data.value
        return data[random.nextInt(0, data.size)]
    }
}