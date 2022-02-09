package me.akhsaul.common

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import java.nio.file.Path

internal class ResourceTest {
    companion object {
        private val LOG = debugLogger { }
    }

    @Test
    fun getAll() {
        val res = Resource<Path>().getAll("Elevate.exe")
        assertTrue(res.isNotEmpty())
        LOG.debug("Result, $res")
    }

    @Test
    fun get() {
        assertDoesNotThrow {
            val res = Resource<Path>().get("Elevate.exe")
            LOG.debug("Result, $res")
        }
    }
}