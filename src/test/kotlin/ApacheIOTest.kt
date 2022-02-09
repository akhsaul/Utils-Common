/*
import org.apache.commons.io.FileUtils
import org.apache.commons.io.filefilter.TrueFileFilter
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import org.junit.jupiter.api.assertDoesNotThrow
import java.io.File

@TestMethodOrder(OrderAnnotation::class)
class ApacheIOTest {
    private val rootDir = File("D:/IdeaProjects")

    @Test
    @Order(1)
    fun getListFiles(){
        assertDoesNotThrow {
            FileUtils.listFiles(
                rootDir,
                arrayOf("java"),
                true
            )
        }.forEach {
            println(it.absolutePath)
        }
    }

    @Test
    @Order(2)
    // Method to list all files in a directory with Apache Commons IO
    fun listFilesTrueInstance() {
        val files = FileUtils.iterateFiles(
            rootDir,
            TrueFileFilter.INSTANCE,
            TrueFileFilter.INSTANCE
        )
        while (files.hasNext()) {
            println(files.next())
        }
    }

    @Test
    @Order(3)
    // Method to list all files in a directory with Apache Commons IO
    fun listFilesRecursive() {
        // pass extension array as null to return all file extensions
        val files = FileUtils.iterateFiles(rootDir, null, true)
        while (files.hasNext()) {
            println(files.next())
        }
    }

    @Test
    @Order(4)
    // Method to list all files in a directory with Apache Commons IO
    fun listFilesDirs() {
        val files = FileUtils.listFilesAndDirs(
            rootDir,
            TrueFileFilter.INSTANCE,
            TrueFileFilter.INSTANCE
        )
        for (file in files) {
            println(file)
        }
    }
}
 */
