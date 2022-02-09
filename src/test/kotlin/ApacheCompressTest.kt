/*
import org.apache.commons.compress.archivers.sevenz.SevenZArchiveEntry
import org.apache.commons.compress.archivers.sevenz.SevenZFile
import org.apache.commons.compress.archivers.sevenz.SevenZOutputFile
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import org.junit.jupiter.api.assertDoesNotThrow
import java.io.File
import java.io.FileInputStream
import java.nio.file.Files

@TestMethodOrder(OrderAnnotation::class)
class ApacheCompressTest {
    private val o = File("output/sample.7z")

    @Test
    @Order(1)
    fun compressSevenZip() {
        // Create 7z file.
        if (o.exists()) o.delete()
        SevenZOutputFile(o).use { sevenZOutput ->
            val folderToZip = File("tes")
            // Walk through files, folders & sub-folders.
            Files.walk(folderToZip.toPath()).forEach { p ->
                val file: File = p.toFile()
                // Directory is not streamed, but its files are streamed into 7z file with
                // folder in it's path
                val entry: SevenZArchiveEntry = sevenZOutput.createArchiveEntry(file, file.path)
                sevenZOutput.putArchiveEntry(entry)
                if (!file.isDirectory){
                    println("Seven Zipping file - $file")
                    assertDoesNotThrow {
                        FileInputStream(file).use {
                            sevenZOutput.write(Files.readAllBytes(file.toPath()))
                        }
                    }
                }
                sevenZOutput.closeArchiveEntry()
            }
            // Complete archive entry addition.
            sevenZOutput.finish()
        }
    }

    @Test
    @Order(2)
    fun uncompressSevenZip() {
        // Get 7zip file.
        assertDoesNotThrow {
            SevenZFile(o).use { sevenZFile ->
                sevenZFile.entries.forEach {
                    val file = File(it.name)
                    println("Un seven zipping - $file")
                    if (it.isDirectory) {
                        // Create directory before streaming files.
                        Files.createDirectories(file.toPath())
                    } else {
                        Files.deleteIfExists(file.toPath())
                        assertDoesNotThrow {
                            Files.write(
                                file.toPath(),
                                sevenZFile.getInputStream(it).readAllBytes()
                            )
                        }
                    }
                }
            }
        }
    }
}
*/
