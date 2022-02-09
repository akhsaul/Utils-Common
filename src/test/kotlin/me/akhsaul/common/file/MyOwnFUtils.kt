package me.akhsaul.common.file

import org.junit.jupiter.api.MethodOrderer.OrderAnnotation
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import org.junit.jupiter.api.assertDoesNotThrow
import java.io.File
import java.net.URL
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.attribute.BasicFileAttributes
import java.util.stream.Collectors
import kotlin.io.path.isReadable
import kotlin.io.path.name
import kotlin.io.path.toPath
import kotlin.test.assertEquals


@TestMethodOrder(OrderAnnotation::class)
class MyOwnFUtils {
    val start = File("D:/IdeaProjects")

    @Test
    @Order(1)
    fun listingFiles(){
        println("akhsaul listing")
        val ac = assertDoesNotThrow {
            FileUtils.listFiles(
                start,
                arrayOf("java"),
                true
            )
        }
        val ex = assertDoesNotThrow {
            URL("").toURI().toPath().toFile()
            org.apache.commons.io.FileUtils.listFiles(
                start,
                arrayOf("java"),
                true
            )
        }
        println(ac.size)
        assertEquals(ex, ac)
        assertEquals(ex.size, ac.size)
    }

    @Test
    @Order(2)
    fun walkByJdk(){
        val a = Files.walk(start.toPath(), Int.MAX_VALUE).filter(Files::isReadable)
            .filter(Files::isRegularFile)
            .filter { it.name.endsWith(".java", true) }
            .collect(Collectors.toList())
        println(a.size)
        a.forEach {
            println(it.toAbsolutePath())
        }
    }
    @Test
    @Order(3)
    fun findByJdk(){
        val a = Files.find(start.toPath(), Int.MAX_VALUE,
            { t: Path, _: BasicFileAttributes ->
                return@find t.isReadable()
            }
        ).toList()
        println(a.size)
    }


}