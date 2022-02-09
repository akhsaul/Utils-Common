package me.akhsaul.common.io

import okio.IOException
import me.akhsaul.common.EOF
import me.akhsaul.common.MAX_BUFFER_SIZE
import me.akhsaul.common.MAX_DISK_BUFFER_SIZE
import me.akhsaul.common.MIN_DISK_BUFFER_SIZE
import me.akhsaul.common.requireAtLeastOne
import me.akhsaul.common.enums.CopyOption
import me.akhsaul.common.enums.CopyOption.APPEND_EXISTING
import me.akhsaul.common.enums.CopyOption.REPLACE_EXISTING
import me.akhsaul.common.enums.DeleteOption
import me.akhsaul.common.enums.DeleteOption.ALL_FILES
import me.akhsaul.common.enums.Requirements
import me.akhsaul.common.enums.Requirements.*
import java.io.*
import java.lang.String.join
import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.StandardOpenOption
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.log10

object Utils {
    val SYSTEM_SEPARATOR = File.separatorChar
    val WIN_SEPARATOR = '\\'
    val UNIX_SEPARATOR = '/'
    val isWinSystem: Boolean
        get() {
            return SYSTEM_SEPARATOR == WIN_SEPARATOR
        }

    private val startTime = lazy {
        System.currentTimeMillis()
    }.value

    private var totalSize: Long? = null
    private var currentSize: Long = 0

    init {
        totalSize = File("treesize.7z").length()
    }

    private fun printProgress(startTime: Long, total: Long, current: Long) {
        Thread.sleep(0, 1)
        val eta = if (current == 0L) 0L else (total - current) * (System.currentTimeMillis() - startTime) / current
        val etaHms = if (current == 0L) "N/A" else String.format(
            "%02d:%02d:%02d:%02d",
            TimeUnit.MILLISECONDS.toHours(eta),
            TimeUnit.MILLISECONDS.toMinutes(eta) % TimeUnit.HOURS.toMinutes(1),
            TimeUnit.MILLISECONDS.toSeconds(eta) % TimeUnit.MINUTES.toSeconds(1),
            TimeUnit.MILLISECONDS.toMillis(eta) % TimeUnit.SECONDS.toMillis(1)
        )
        val string = StringBuilder(140)
        val percent = (current / total * 100).toInt()

        string
            .append('\r')
            .append(
                join(
                    "", Collections.nCopies(
                        if (percent == 0) 2 else 2 - log10(percent.toDouble())
                            .toInt(), " "
                    )
                )
            )
            .append(String.format(" %d%% [", percent))
            .append(join("", Collections.nCopies(percent, "=")))
            .append('>')
            .append(join("", Collections.nCopies(100 - percent, " ")))
            .append(']')
            .append(
                join(
                    "", Collections.nCopies(
                        if (current == 0L)
                            log10(total.toDouble()).toInt()
                        else
                            log10(total.toDouble()).toInt() - log10(current.toDouble()).toInt(), " "
                    )
                )
            )
            .append(String.format(" %d/%d, ETA: %s", current, total, etaHms))
        if (current == total) println(string) else print(string)
    }

    fun addProgressListener(currentSize: Long, totalSize: Long) {
        this.currentSize = currentSize
        this.totalSize = totalSize
    }

    // IO
    fun copyFileRecursively(input: File, output: File, option: CopyOption, preserveDate: Boolean) {
        // ensure copying a file not a directory
        if (input.isFile) throw IllegalAccessException("PLEASE USE Utils.copyFile")

        Files.walk(input.toPath()).forEach {
            val f = it.toFile()
            if (f.isDirectory && !f.exists()) {
                Files.createDirectory(it)
            } else {
                copyFile(input, output, option, preserveDate)
            }
        }
    }

    fun copyFile(input: File, output: File, option: CopyOption, preserveDate: Boolean) {
        // TODO
        // ensure copying a file not a directory
        if (input.isDirectory) throw IllegalAccessException("PLEASE USE Utils.copyFileRecursively")
        // ensure not null before opening file
        checkIORequirements(
            input,
            FILES_EXIST,
            READ
        )

        val out = when (option) {
            APPEND_EXISTING -> {
                Files.newOutputStream(
                    output.toPath(),
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND, StandardOpenOption.WRITE
                )
            }
            REPLACE_EXISTING -> {
                delete(output, ALL_FILES)
                Files.newOutputStream(
                    output.toPath(),
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE
                )
            }
        }

        copy(input.inputStream(), out, ByteArray(MIN_DISK_BUFFER_SIZE))

        if (preserveDate) setLastModified(input, output)
    }

    fun copyFileLargeRecursively(input: File, output: File, option: CopyOption, preserveDate: Boolean) {
        // ensure copying a file not a directory
        if (input.isFile) throw IllegalAccessException("PLEASE USE Utils.copyFile")

        Files.walk(input.toPath()).forEach {
            val f = it.toFile()
            if (f.isDirectory && !f.exists()) {
                Files.createDirectory(it)
            } else {
                copyFileLarge(input, output, option, preserveDate)
            }
        }
    }

    fun copyFileLarge(input: File, output: File, option: CopyOption, preserveDate: Boolean) {
        // TODO
        // ensure copying a file not a directory
        if (input.isDirectory) throw IllegalAccessException("PLEASE USE Utils.copyFileRecursively")
        checkIORequirements(
            input,
            READ,
            FILES_EXIST
        )
        copy(input.inputStream(), output.outputStream())
    }

    fun copy(
        input: InputStream, output: OutputStream,
        buffer: ByteArray = ByteArray(MAX_DISK_BUFFER_SIZE)
    ): Long {
        var count = currentSize
        BufferedInputStream(input).use { i ->
            var n: Int
            while (i.read(buffer).also { n = it } != EOF) {
                output.write(n)
                count += n.toLong()
                totalSize?.let { printProgress(startTime, it, count) }
            }
        }
        return count
    }

    private fun setLastModified(src: File, dst: File) {
        if (src.exists() && dst.exists()) {
            if (!dst.setLastModified(src.lastModified())) {
                throw IOException("Can't set Last-Modified into file '$src'")
            }
        } else {
            throw IOException("File '$src' OR File '$dst' NOT EXIST")
        }
    }

    /**
     * @return Boolean - return true if success deleted all them, false if at least one is fail
     * */
    fun delete(fileOrFolder: File, option: DeleteOption, vararg excludeFileNameOrFolderName: String): Boolean {
        // ensure the file or folder is exist
        if (!exists(fileOrFolder, true, true)) {
            throw IOException("Some files or folders NOT EXIST. Can't be continue")
        }

        var folders = false
        var files = false
        when (option) {
            ALL_FILES -> {
                files = true
            }
            DeleteOption.ALL_FOLDERS -> {
                folders = true
            }
            DeleteOption.ALL_FILES_FOLDERS -> {
                folders = true
                files = true
            }
        }
        val listForDel = mutableListOf<File>()

        if (fileOrFolder.isDirectory) {
            Files.walk(fileOrFolder.toPath()).forEach { p ->
                val f = p.toFile()
                if (excludeFileNameOrFolderName.isNotEmpty()) {
                    excludeFileNameOrFolderName.forEach {
                        if (f.name != it) {
                            listForDel.add(f)
                        }
                    }
                } else {
                    listForDel.add(f)
                }
            }
        } else {
            listForDel.add(fileOrFolder)
        }

        var success = false

        listForDel.requireAtLeastOne().forEach {
            if (it.isFile && (files || folders)) {
                success = it.delete()
            } else if (it.isDirectory && (files || folders)) {
                success = it.delete()
            }
        }

        return success
    }

    fun exists(fileOrFolder: File, checkFile: Boolean, checkFolder: Boolean = false): Boolean {
        return if (fileOrFolder.isDirectory) {
            // how many file or folder not exist ?
            var existCount = 0
            Files.walk(fileOrFolder.toPath()).forEach {
                val f = it.toFile()

                if (f.isFile && checkFile && f.exists()
                    || f.isDirectory && checkFolder && f.exists()
                ) {
                    existCount++
                }
            }
            // if this is bigger than zero, that's mean some file or folder not exist
            existCount > 0
        } else {
            if (fileOrFolder.isFile && checkFolder) {
                println(
                    """
                    WARNING:
                    The '$fileOrFolder' is a File.
                    if you know that is a file,
                    PLEASE SET FALSE INTO 'checkFolder' Argument.
                    BUT if you don't know that is a file,
                    you can ignore this warning
                """.trimIndent()
                )
            }

            fileOrFolder.isFile && checkFile && fileOrFolder.exists()
        }
    }

    fun checkEqualSize(src: File, dst: File) {
        if (src.length() != dst.length()) {
            throw IOException(
                "Failed to copy full contents from '$src' to '$dst'"
                        + "Expected length: ${src.length()}. Actual: ${dst.length()}"
            )
        }
    }

    fun buildMessage(vararg message: String): String {
        return buildMessage(message.asList())
    }

    // READER
    fun readFile(input: InputStream, writer: Writer, charset: Charset) {
        read(InputStreamReader(input, charset), writer, CharArray(MIN_DISK_BUFFER_SIZE))
    }

    fun readFileLarge(input: InputStream, writer: Writer, charset: Charset) {
        read(InputStreamReader(input, charset), writer)
    }

    fun read(reader: Reader, writer: Writer, buffer: CharArray = CharArray(MAX_DISK_BUFFER_SIZE)): Long {
        var count: Long = 0
        var n: Int
        while (reader.read(buffer).also { n = it } != EOF) {
            writer.write(buffer, 0, n)
            count += n.toLong()
            totalSize?.let { printProgress(startTime, it, count) }
        }
        return count
    }

    /**
     * Default Charset is US_ASCII
     * */
    fun readToString(file: File, charset: Charset = Charsets.US_ASCII): String {
        return readToString(file.inputStream(), charset)
    }

    fun readToString(input: InputStream, charset: Charset = Charsets.US_ASCII): String {
        val writer = WriterBuilder()
        readFileLarge(input, writer, charset)
        return writer.toString()
    }

    fun readAllBytes(input: InputStream): ByteArray {
        return readNBytes(input, Int.MAX_VALUE)
    }

    fun readNBytes(input: InputStream, len: Int): ByteArray {
        require(len >= 0) { "len < 0" }

        var bufs: MutableList<ByteArray>? = null
        var result: ByteArray? = null
        var total = 0
        var remaining = len
        var n: Int
        do {
            val buf = ByteArray(remaining.coerceAtMost(MAX_DISK_BUFFER_SIZE))
            var nread = 0

            // read to EOF which may read more or less than buffer size
            while (input.read(
                    buf, nread,
                    (buf.size - nread).coerceAtMost(remaining)
                ).also { n = it } > 0 && n != EOF
            ) {
                nread += n
                remaining -= n
            }
            if (nread > 0) {
                if (MAX_BUFFER_SIZE - total < nread) {
                    throw OutOfMemoryError("Required array size too large")
                }
                total += nread
                if (result == null) {
                    result = buf
                } else {
                    if (bufs == null) {
                        bufs = ArrayList()
                        bufs.add(result)
                    }
                    bufs.add(buf)
                }
            }
            // if the last call to read returned -1 or the number of bytes
            // requested have been read then break
        } while (n >= 0 && remaining > 0)

        if (bufs == null) {
            if (result == null) {
                return ByteArray(0)
            }
            return if (result.size == total) result else Arrays.copyOf(result, total)
        }

        result = ByteArray(total)
        var offset = 0
        remaining = total
        for (b in bufs) {
            val count = b.size.coerceAtMost(remaining)
            System.arraycopy(b, 0, result, offset, count)
            offset += count
            remaining -= count
        }

        return result
    }

    /***
     * Internal func,
     * Global func
     * will force to use
     * Extension func
     */
    internal fun fileToInStream(pathToFile: String): InputStream {
        /** TODO
         *   some file maybe in resource project or in system or other place
         *   need to use an option params
         * */
        return resources(pathToFile)
    }

    // ALL private func can't use in another class
    private fun resources(pathToFile: String): InputStream {
        // TODO
        return checkNotNull(ClassLoader.getSystemResourceAsStream(pathToFile)) { "ERROR. FILE NOT FOUND" }
    }

    private fun buildMessage(message: List<String>): String {
        val msg = StringBuilder()
        var maxChar = 0
        message.requireAtLeastOne().forEach {
            maxChar = if (maxChar > it.length) maxChar else it.length
            msg.append(it).append("\n")
        }
        val batas = StringBuilder()
        repeat(maxChar) {
            batas.append("-")
        }
        val tmpMsg = msg.toString().trim()

        msg.clear()
        msg.append(batas.toString())
        msg.append(tmpMsg)
        msg.append(batas.toString())

        return msg.toString().trim()
    }

    private fun checkIORequirements(src: File, vararg options: Requirements) {
        val listFailure = mutableListOf<String>()
        var all = false
        options.sortedDescending().requireAtLeastOne().forEach {
            when (it) {
                ALL_EXIST -> {
                    all = true
                    if (!exists(src, true, true)) {
                        listFailure.add("Some files or folders '$src' NOT EXIST")
                    }
                }
                FILES_EXIST -> {
                    if (all) return@forEach
                    if (!exists(src, true)) {
                        listFailure.add("Some files '$src' NOT EXIST")
                    }
                }
                FOLDERS_EXIST -> {
                    if (all) return@forEach
                    if (!exists(src, checkFile = false, true)) {
                        listFailure.add("Some folders '$src' NOT EXIST")
                    }
                }
                READ -> if (!src.canRead()) listFailure.add("file or folder '$src' can't be read")
                WRITE -> if (!src.canWrite()) listFailure.add("file or folder '$src' can't be write")
                else -> throw IllegalArgumentException("Argument ${it.name} NOT SUPPORTED")
            }
        }

        if (listFailure.isNotEmpty()) {
            throw IOException(
                buildMessage(listFailure)
            )
        }
    }
}