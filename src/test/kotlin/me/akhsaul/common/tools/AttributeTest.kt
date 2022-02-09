package me.akhsaul.common.tools

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import me.akhsaul.common.debugLogger
import me.akhsaul.common.enums.DataUnit
import me.akhsaul.common.math.DataSize
import org.junit.jupiter.api.Test
import oshi.SystemInfo
import java.nio.file.LinkOption
import java.nio.file.Path
import java.nio.file.attribute.DosFileAttributes
import kotlin.time.Duration.Companion.seconds

internal class AttributeTest {
    private val path = Path.of("E:/")
    private val file = Path.of("E:/video_20211008_161833.mp4")
    private val other = Path.of("E:\\xampp\\perl\\lib\\auto\\mro\\mro.xs.dll")

    @Test
    fun size() {
        val attr = Attribute(path)
        /*
        val size = attr.size(true)
        println(size)
        val sizeStr = DataSize(size, toUnit = DataUnit.BYTES)
        println(sizeStr)
         */
    }

    companion object {
        val LOG = debugLogger { }
    }

    @Test
    fun fileSize() {
        val d = Data(other)
        println(d)
        println(Attribute(other))
        runBlocking {
            delay(5.seconds)
            println(d.isFile)
            println(d.isDirectory)
            delay(5.seconds)
            println(d.canExecute)
            println(d.canRead)
            val p = SystemInfo().hardware.powerSources[0]
            println(p)
            var start = System.currentTimeMillis()
            val oneSecond = 5.seconds.inWholeMilliseconds
            /*
            while (true) {
                val current = System.currentTimeMillis()
                if ((current - start) >= oneSecond) {
                    p.updateAttributes()
                    LOG.info(p.toString())
                    start = System.currentTimeMillis()
                }
            }*/
        }
    }

    @Test
    fun dos() {
        val attr = other.fileSystem.provider().readAttributes(other, DosFileAttributes::class.java)
        println(attr.isReadOnly)
        println(attr.isSystem)
        println(attr.isHidden)
        println(attr.isArchive)
        val attrs = other.fileSystem.provider().readAttributes(other, "dos:readonly", LinkOption.NOFOLLOW_LINKS)
        println(attrs)
    }
}