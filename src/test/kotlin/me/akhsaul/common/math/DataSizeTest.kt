package me.akhsaul.common.math

import kotlinx.coroutines.*
import me.akhsaul.common.*
import me.akhsaul.common.core.Core
import me.akhsaul.common.enums.DataUnit
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.io.File
import java.math.BigDecimal
import kotlin.time.DurationUnit
import kotlin.time.toDuration
/*
internal class DataSizeTest {
    private val value = 398679666688
    private val bigValue = BigDecimal("371.99")
    private val BIT_BYTE = "49.834.958.336 B"
    private val BIT_GIGABIT = "371,299 Gb"
    private val GIGABIT_BIT = "399.421.221.109,76 b"
    private val GIGABIT_GIGABYTE = "46,498 GB"
    private val BYTE_BIT = "3.189.437.333.504 b"
    private val BYTE_GIGABYTE = "371,299 GB"
    private val GIGABYTE_BYTE = "399.421.221.109,76 B"
    private val GIGABYTE_GIGABIT = "2.975,92 Gb"
    private val precision = 3

    @Test
    fun bitToByte() {
        val actual = DataSize(value, fromUnit = DataUnit.BIT, toUnit = DataUnit.BYTE, precision).toString()
        assertEquals(
            BIT_BYTE,
            actual
        )
    }

    @Test
    fun bitToGigaBit() {
        val actual = DataSize(value, fromUnit = DataUnit.BIT, toUnit = DataUnit.GIGABIT, precision).toString()
        assertEquals(
            BIT_GIGABIT,
            actual
        )
    }

    @Test
    fun gigaBitToBit() {
        val actual = DataSize(bigValue, fromUnit = DataUnit.GIGABIT, toUnit = DataUnit.BIT, precision).toString()
        assertEquals(
            GIGABIT_BIT,
            actual
        )
    }

    @Test
    fun byteToBit() {
        val actual = DataSize(value, fromUnit = DataUnit.BYTE, toUnit = DataUnit.BIT, precision).toString()
        assertEquals(
            BYTE_BIT,
            actual
        )
    }

    @Test
    fun byteToGigaByte() {
        val actual = DataSize(value, fromUnit = DataUnit.BYTE, toUnit = DataUnit.GIGABYTE, precision).toString()
        assertEquals(
            BYTE_GIGABYTE,
            actual
        )
    }

    @Test
    fun gigaByteToByte() {
        val actual = DataSize(bigValue, fromUnit = DataUnit.GIGABYTE, toUnit = DataUnit.BYTE, precision).toString()
        assertEquals(
            GIGABYTE_BYTE,
            actual
        )
    }

    @Test
    fun gigaByteToGigaBit() {
        val actual = DataSize(bigValue, fromUnit = DataUnit.GIGABYTE, toUnit = DataUnit.GIGABIT, precision).toString()
        assertEquals(
            GIGABYTE_GIGABIT,
            actual
        )
    }

    @Test
    fun gigaBitToGigaByte() {
        val actual = DataSize(bigValue, fromUnit = DataUnit.GIGABIT, toUnit = DataUnit.GIGABYTE, precision).toString()
        assertEquals(
            GIGABIT_GIGABYTE,
            actual
        )
    }

    @OptIn(DelicateCoroutinesApi::class, kotlin.time.ExperimentalTime::class)
    private fun File.test() {
        val length = length()
        var count = 0L
        var end: Long
        val start = System.nanoTime()
        try {
            inputStream().use { inp ->
                var threshold = 0
                while (inp.read().also { count += it } != EOF) {
                    end = (System.nanoTime() - start).toDuration(DurationUnit.NANOSECONDS).inWholeSeconds
                    if (end > threshold) {
                        runBlocking {
                            Core.forImmediateTask().asyncJoin {
                                val bytesPerSec: Double = (count / end).toDouble()
                                val msg = DataSize(
                                    BigDecimal(count / end),
                                    decimalPrecision = 3
                                ).toString() + "/s"
                                write(
                                    "$msg. ${count.convert()} of ${length.convert()}",
                                    "Running on ${Thread.currentThread()}",
                                    flush = false
                                )
                                threshold++
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            throw e
        } finally {
            end = (System.nanoTime() - start).toDuration(DurationUnit.NANOSECONDS).inWholeSeconds.let {
                if (it == 0L) 1 else it
            }
            write(
                "\nread = ${count.convert()}, total = ${length.convert()}, time = $end sec, average = ${(count / end).convert()}/s",
                "Finished on ${Thread.currentThread()}",
                flush = false
            )
        }
    }

    @Test
    fun byteToAuto() {
        withGlobalTest { tmp ->
            runBlocking {
                Core.forIOTask().asyncJoin {
                    write("Start running on ${Thread.currentThread()}", flush = false)
                    tmp.test()
                }
            }
        }
    }
}*/