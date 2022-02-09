package me.akhsaul.common.core

import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import java.io.File

internal class DeleteOnExitTest {

    @Test
    fun add() {
    }

    @Test
    fun testAdd() {
    }

    @Test
    fun addAll() {
    }

    @Test
    fun testAddAll() {
    }

    @Test
    fun remove() {
    }

    @Test
    fun testRemove() {
    }

    @Test
    fun removeAll() {
    }

    @Test
    fun testRemoveAll() {
        //tmpFile = prepareTempFile(LOG)
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(DeleteOnExitTest::class.java)
        private var tmpFile: File? = null

        @BeforeAll
        @JvmStatic
        fun setup() {
            LOG.info("Start make temp file")
            //tmpFile = prepareTempFile(LOG)
        }
    }
}