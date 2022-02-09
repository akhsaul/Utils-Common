package me.akhsaul.common.math

import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory

internal class StatisticTest {

    @Test
    fun mean() {
        val result = Statistic.mean(arrayListOf(5, 6, 7, 8, 9, 10), arrayListOf(9, 10, 12, 6, 2, 1))
        // make sure 6.625 is float not double
        assertEquals((6.625).toFloat(), result)
    }

    @Test
    fun median() {
        val result = Statistic.median(arrayListOf(8, 9, 10, 5, 6, 7), arrayListOf(6, 2, 1, 9, 10, 12))
        assertEquals(7, result)
    }

    @Test
    fun modus() {
        val result = Statistic.modus(
            arrayListOf(
                102, 108, 106, 104, 108,
                105, 104, 105, 108, 106,
                106, 106, 104, 102, 105,
                105, 102, 106, 105, 106,
                104, 106, 105, 106, 102,
                105, 104, 104, 106, 105,
                106, 106, 105, 104, 102
            )
        )
        assertEquals(106, result)
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(StatisticTest::class.java)
        @BeforeAll
        fun setup() {
            LOG.info("another before")
        }

        @AfterAll
        fun tearDown() {
            LOG.info("another after")
        }
    }
}