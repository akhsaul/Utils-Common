package me.akhsaul.common.tools

import okhttp3.HttpUrl
import me.akhsaul.common.toHttpUrl
import org.apache.tika.Tika
import org.apache.tika.mime.MimeTypes
import java.io.File
import java.net.URL
import java.nio.file.Files
import java.nio.file.Path

class Extension {
    var extension: String = ".unknown"
    var mimeTypes: String = "unknown"
    val filename: String = ""

    private var extensionByMime: String = ""
    private var extensionByName: String = ""

    constructor(fileUrl: URL) : this(fileUrl.toHttpUrl())

    constructor(filePath: Path) : this(filePath.toFile())

    constructor(file: File) {
        if (file.exists()) {

            mimeTypes = detectByMime(file)
            extensionByMime = mimeToExt(mimeTypes)

            extensionByName = detectByName(file.name)

            /**
             * Please use this with caution, this is not guarantee accurate
             * */
            val byNIO = Files.probeContentType(file.toPath())

            extension = when {
                extensionByMime.isNotBlank() -> extensionByMime
                !byNIO.isNullOrEmpty() && byNIO.isNotBlank() -> byNIO
                else -> extensionByName
            }
        }
    }

    constructor(urlFile: HttpUrl) {
        for (li in urlFile.encodedPathSegments) {
            if (li.contains(".")) {
                extensionByName = detectByName(li)
                //extensionByName = ""
            }
        }

        extension = extensionByName.ifBlank { extensionByMime }
    }

    private fun mimeToExt(mime: String): String {
        return MimeTypes.getDefaultMimeTypes().forName(mime).extension
    }

    fun detectByName(fileName: String): String {
        val tmpExt = mimeToExt(
            Tika().detect(fileName)
        )
        val r = fileName.substringAfterLast(".", "")
        return if (r.isNotBlank()) {
            if (tmpExt == ".$r") {
                ".$r"
            } else {
                tmpExt
            }
        } else {
            r
        }
    }

    private fun detectByMime(file: File): String {
        return Tika().detect(file)
    }

    fun isValid(): Boolean {
        return (extension.isNotBlank()
                && (extension == extensionByMime || extension == extensionByName))
    }

    override fun toString(): String {

        return """
            LOG:
            extension        : $extension
            extension by mime: $extensionByMime
            extension by name: $extensionByName
            mime type        : $mimeTypes
        """.trimIndent()
    }
}