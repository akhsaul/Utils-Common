package me.akhsaul.common.tools

import me.akhsaul.common.data.table.Italian
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertSame

internal class RandomizerTest {
    private val size = 10

    @Test
    fun byRangeCode() {
        val expected = '0'.code..'z'.code
        val actual = Randomizer.StringBuilder().range('0'.code, 'z'.code).generateCharArray(size)

        for (c in actual) {
            assertTrue(expected.contains(c.code), String(actual))
        }
        assertSame(size, actual.size, String(actual))
    }

    @Test
    fun byRangeChar() {
        val expected = 'a'..'z'
        val actual = Randomizer.StringBuilder().range('a', 'z').generateCharArray(size)

        for (c in actual) {
            assertTrue(expected.contains(c), String(actual))
        }
        assertSame(size, actual.size, String(actual))
    }

    @Test
    fun fromAscii() {
        val expected = 32..127
        val actual = Randomizer.StringBuilder().fromAscii().generateCharArray(size)

        for (c in actual) {
            assertTrue(expected.contains(c.code), String(actual))
        }
        assertSame(size, actual.size, String(actual))
    }

    @Test
    fun selectFromCharsClass() {
        val expected = Italian.codePoint
        val actual = Randomizer.StringBuilder().selectFrom(Italian).generateCharArray(size)

        for (c in actual) {
            assertTrue(expected.contains(c.code), String(actual))
        }
        assertSame(size, actual.size, String(actual))
    }

    @Test
    fun selectFromChars() {
        val expected = listOf('a', 'b', 'c', 'z', 'x', 'v')
        val actual = Randomizer.StringBuilder().selectFrom('a', 'b', 'c', 'z', 'x', 'v').generateCharArray(size)

        for (c in actual) {
            assertTrue(expected.contains(c), String(actual))
        }
        assertSame(size, actual.size, String(actual))
    }

    @Test
    fun selectFromCodes() {
        val expected = listOf(33, 45, 50, 60, 70, 90)
        val actual = Randomizer.StringBuilder().selectFrom(33, 45, 50, 60, 70, 90).generateCharArray(size)

        for (c in actual) {
            assertTrue(expected.contains(c.code), String(actual))
        }
        assertSame(size, actual.size, String(actual))
    }

    @Test
    fun onlyAlphabetic() {
        repeat(3) {
            val actual = Randomizer.StringBuilder().only(letters = true, false).generate(size)
            assertTrue(actual.contains(Regex("[a-zA-Z]")), actual)
            assertSame(size, actual.length, actual)
        }
    }

    @Test
    fun onlyNumber() {
        repeat(3) {
            val actual = Randomizer.StringBuilder().only(false, numbers = true).generate(size)
            assertTrue(actual.contains(Regex("\\d")), actual)
            assertSame(size, actual.length, actual)
        }
    }

    @Test
    fun filtered() {
        val filter: Array<(codePoint: Int) -> Boolean> = arrayOf(
            // only accept even number
            { codePoint -> codePoint.mod(2) == 0 },
            // there is no number can be zero after divide by ten
            { codePoint -> codePoint.mod(10) == 0 },
            // only accept letter or digit
            { codePoint -> Character.isLetterOrDigit(codePoint) }
        )
        repeat(3) {
            val actual = Randomizer.StringBuilder().fromAscii().filteredBy(*filter).generate(size)
            println(actual)
            // check if result match with first filter
            assertTrue(actual.codePoints().allMatch { code -> code.mod(2) == 0 })
            // check if result match with second filter
            assertTrue(actual.codePoints().allMatch { code -> code.mod(10) == 0 })
            // check if result match with third filter
            assertTrue(actual.contains(Regex("[a-zA-Z0-9]")), actual)
            assertSame(size, actual.length, actual)
        }
    }

    @Test
    fun excludedByChar() {
        val filter = charArrayOf('A', 'k', 'H', 'S', 'a', 'U', 'l')
        val actual = Randomizer.StringBuilder().only(letters = true, false).exclude(*filter).generate(50)
        // check if result does not contain char in filter
        assertFalse(actual.contains(String(filter)), actual)
        assertSame(50, actual.length, actual)
    }

    @Test
    fun excludedByCode() {
        val filter = intArrayOf(65, 75, 83, 85, 97, 104, 108)
        val actual = Randomizer.StringBuilder().only(letters = true, false).exclude(*filter).generate(50)
        // check if result does not contain codePoint in filter
        assertTrue(actual.codePoints().allMatch { code -> !filter.contains(code) })
        assertSame(50, actual.length, actual)
    }

    @Test
    fun verifyGeneratorArrayLimit() {
        val limit = 3
        repeat(3) {
            val actual = Randomizer.StringBuilder().range('a', 'z').generateCharArray(1, limit)
            assertTrue(actual.size <= limit, String(actual))
        }
    }

    @Test
    fun verifyGeneratorStringLimit() {
        val limit = 3
        repeat(3) {
            val actual = Randomizer.StringBuilder().range('a', 'z').generate(1, limit)
            assertTrue(actual.length <= limit, actual)
        }
    }

    @Test
    fun exceptionInRange() {
        val actual = assertThrows<IllegalArgumentException> {
            Randomizer.StringBuilder().range(50, 10).generate(size)
        }
        assertEquals("Parameter minimum (50) must be greater than maximum (10)", actual.message)
    }

    @Test
    fun exceptionWhenZeroLength() {
        val actual = assertThrows<IllegalArgumentException> {
            repeat(3) {
                Randomizer.StringBuilder().range(10, 100).generate(0, 0)
            }
        }
        assertEquals("Length 0 is smaller than zero.", actual.message)
    }

    @Test
    fun exceptionWhenSameMinMaxLength() {
        val actual = assertThrows<IllegalArgumentException> {
            repeat(3) {
                Randomizer.StringBuilder().range(10, 100).generate(3, 1)
            }
        }
        assertEquals("bound must be greater than origin", actual.message)
    }
}