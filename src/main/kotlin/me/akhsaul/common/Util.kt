@file:JvmName("Util")
@file:Suppress("unused", "unused_parameter")

package me.akhsaul.common

import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import me.akhsaul.common.enums.LogLevel
import me.akhsaul.common.exception.EmptyException
import me.akhsaul.common.exception.IOAccessException
import me.akhsaul.common.exception.NotExistsException
import me.akhsaul.common.exception.RequirementNotMeetException
import me.akhsaul.common.math.DataSize
import me.akhsaul.common.tools.Headers
import me.akhsaul.common.tools.Sys
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.Response
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.core.LoggerContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.*
import java.net.URL
import java.nio.ByteBuffer
import java.nio.file.AccessMode
import java.nio.file.Files
import java.nio.file.Path
import java.util.regex.Pattern
import kotlin.contracts.ExperimentalContracts
import kotlin.io.path.Path
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.ln
import kotlin.reflect.KClass

private val LOG: Logger = LoggerFactory.getLogger("me.akhsaul.common.Util")

/**
 * The maximum size of array to allocate.
 * Some VMs reserve some header words in an array.
 * Attempts to allocate larger arrays may result in
 * OutOfMemoryError: Requested array size exceeds VM limit
 */
@Deprecated("jvm always throw out of memory")
const val MAX_BUFFER_SIZE = Int.MAX_VALUE - 8

/**
 * For disk access. The fastest size can be 32K to 64 KB
 * */
@Deprecated("sometimes jvm throw out of memory")
const val MIN_DISK_BUFFER_SIZE = (32 * 1024)

/**
 * For disk access. The fastest size can be 32K to 64 KB
 * */
@Deprecated("sometimes jvm throw out of memory")
const val MAX_DISK_BUFFER_SIZE = (64 * 1024)

/**
 * End Of File. Use for handle input when reading
 * @see java.io.InputStream.read
 * */
const val EOF = -1

val mutex = Mutex()

@JvmOverloads
fun <T> withLock(owner: Any? = null, action: () -> T): T = runBlocking {
    mutex.withLock(owner, action)
}

/**
 * call this function in "companion object" or static variable
 * */
fun logger(func: () -> Unit): Logger = LoggerFactory.getLogger(func.javaClass.name.substringBefore('$'))

/**
 * ignore [Throwable] without send to [Logger]
 * @param T generic type of the returned [action]
 * @param action an action to do something
 * @return null or [T]
 * */
fun <T> ignore(action: () -> T): T? = try {
    action()
} catch (_: Throwable) {
    null
}

/**
 * ignore [Throwable] and send with default message into [Logger] with level is [LogLevel.TRACE]
 * @param T generic type of the returned [action]
 * @param action an action to do something
 * @return null or [T]
 * */
fun <T> ignoreAndLog(
    action: () -> T
): T? = ignoreAndLog(LogLevel.TRACE, "Some exception are ignored.", action = action)

/**
 * ignore [Throwable] and send to [Logger] with specified [level]
 * @param T generic type of the returned [action]
 * @param action an action to do something
 * @param level [Logger] level
 * @param format message or format message
 * @param args argument for format message, can be null or empty
 * @return null or [T]
 * */
@JvmOverloads
fun <T> ignoreAndLog(
    level: LogLevel,
    format: String,
    vararg args: Any? = emptyArray(),
    action: () -> T
): T? = try {
    action()
} catch (t: Throwable) {
    when (level) {
        LogLevel.TRACE -> LOG.trace(format, *args, Exception())
        LogLevel.ERROR -> LOG.error(format, *args, Exception())
        LogLevel.DEBUG -> LOG.debug(format, *args, Exception())
        LogLevel.WARN -> LOG.warn(format, *args, Exception())
        LogLevel.INFO -> LOG.info(format, *args, Exception())
        else -> LOG.trace(format, *args, Exception())
    }
    null
}

/**
 * catch [Throwable] without send to [Logger]
 * @param T generic type of the returned [action]
 * @param default default value if [Throwable] happened
 * @param action an action to do something
 * @return [T] or [default]
 * */
fun <R, T : R> catch(default: R, action: () -> T): R = ignore(action) ?: default

/**
 * catch [Throwable] and send with default message into [Logger] with level is [LogLevel.TRACE],
 * then return [action] or [default]
 * @param T generic type of the returned [action]
 * @param default default value if [Throwable] happened
 * @param action an action to do something
 * @return [T] or [default]
 * */
fun <R, T : R> catchAndLog(default: R, action: () -> T): R = ignoreAndLog(action) ?: default

/**
 * catch [Throwable] and send to [Logger] with specified [level], then return [action] or [default]
 * @param T generic type of the returned [action]
 * @param default default value if [Throwable] happened
 * @param action an action to do something
 * @param level [Logger] level
 * @param format message or format message
 * @param args argument for format message, can be null or empty
 * @return [T] or [default]
 * */
@JvmOverloads
fun <R, T : R> catchAndLog(
    default: R,
    level: LogLevel,
    format: String,
    vararg args: Any? = emptyArray(),
    action: () -> T
): R = ignoreAndLog(level, format, *args, action = action) ?: default

fun setLogLevel(level: LogLevel) {
    val ctx: LoggerContext = LogManager.getContext(false) as LoggerContext
    ctx.configuration.rootLogger.level = level.level
    ctx.updateLoggers()
}

/**
 * encapsulate [Throwable] with [exception] then throw it
 * @param T generic type of the returned [action]
 * @param exception a [Throwable] to encapsulate other [Throwable] (cause)
 * @param action an action to do something
 * @return [T]
 * */
fun <T> throws(exception: (cause: Throwable) -> Throwable, action: () -> T): T = try {
    action()
} catch (t: Throwable) {
    throw exception(t)
}

/**
 * Throws an [NullPointerException] if the [value] is null.
 * */
@JvmOverloads
inline fun <T : Any> notNull(
    value: T?,
    exception: () -> Exception = { NullPointerException("The value was NULL.") }
): T = value ?: throw exception()

/**
 * Throws an [RequirementNotMeetException] if the [value] is false.
 * */
@JvmOverloads
fun require(value: Boolean, exception: () -> Exception = { RequirementNotMeetException("TRUE", "FALSE") }) =
    if (!value) throw exception() else Unit

/**
 * Throws an [RequirementNotMeetException] if the [value] is negative.
 * */
fun requireNonNegative(value: Int) = require(value >= 0) { RequirementNotMeetException("NON-NEGATIVE", value) }

/**
 * Throws an [RequirementNotMeetException] if the [value] is negative.
 * */
fun requireNonNegative(value: Long) = require(value >= 0) { RequirementNotMeetException("NON-NEGATIVE", value) }

fun <T : Any> MutableList<T>.addNonDuplicate(element: T) = apply {
    if (!contains(element)) {
        add(element)
    }
}

/**
 * Throws an [EmptyException] if the [T] is empty.
 * */
@JvmOverloads
inline fun <T : Collection<*>> T.requireAtLeastOne(
    exception: () -> Exception = { EmptyException(this) }
): T = ifEmpty { throw exception() }

/**
 * Throws an [EmptyException] if the [T] is empty.
 * */
@JvmOverloads
inline fun <T : Any> Array<T>.requireAtLeastOne(
    exception: () -> Exception = { EmptyException(this) }
): Array<T> = ifEmpty { throw exception() }

/**
 * Throws an [NotExistsException] if the [File] does not exist.
 * */
@JvmOverloads
inline fun File.requireExist(
    exception: () -> Exception = { NotExistsException(this) }
): File = if (exists()) this else throw exception()

/**
 * Throws an [NotExistsException] if the [Path] does not exist.
 * */
@JvmOverloads
inline fun Path.requireExist(
    exception: () -> Exception = { NotExistsException(this) }
): Path = if (Files.exists(this)) this else throw exception()

/**
 * Throws an [IOAccessException] if the [Path] does not support read and write.
 *
 * Throws an [NotExistsException] if the [Path] does not exist.
 * */
fun Path.requireIO(): Path = apply {
    requireExist()
    needRead()
    needWrite()
}

/**
 * Throws an [IOAccessException] if the [File] does not support read and write.
 *
 * Throws an [NotExistsException] if the [File] does not exist.
 * */
fun File.requireIO(): File = apply {
    requireExist()
    needRead()
    needWrite()
}

val Path.isDirectory get() = Files.isDirectory(this)

/**
 * Throws an [RequirementNotMeetException] if the [Path] does not exist, or this is a file.
 * */
fun Path.shouldDir(): Unit = require(requireExist().isDirectory) {
    RequirementNotMeetException(this, "Directory", "File")
}

/**
 * Throws an [RequirementNotMeetException] if the [File] does not exist, or this is a file.
 * */
fun File.shouldDir(): Unit = require(requireExist().isDirectory) {
    RequirementNotMeetException(this, "Directory", "File")
}

val Path.isFile get() = Files.isRegularFile(this)

/**
 * Throws an [RequirementNotMeetException] if the [Path] does not exist, or this is a directory.
 * */
fun Path.shouldFile(): Unit = require(requireExist().isFile) {
    RequirementNotMeetException(this, "File", "Directory")
}

/**
 * Throws an [RequirementNotMeetException] if the [File] does not exist, or this is a directory.
 * */
fun File.shouldFile(): Unit = require(requireExist().isFile) {
    RequirementNotMeetException(this, "File", "Directory")
}

fun Path.canRead() = Files.isReadable(this)

/**
 * Throws an [IOAccessException] if the [Path] does not support read.
 * */
fun Path.needRead() = require(
    throws({ cause -> IOAccessException(this, AccessMode.READ, cause) }, { canRead() })
) { IOAccessException(this, AccessMode.READ) }

/**
 * Throws an [IOAccessException] if the [File] does not support read.
 * */
fun File.needRead() = require(
    throws({ cause -> IOAccessException(this, AccessMode.READ, cause) }, { canRead() })
) { IOAccessException(this, AccessMode.READ) }

fun Path.canWrite() = Files.isWritable(this)

/**
 * Throws an [IOAccessException] if the [Path] does not support write.
 * */
fun Path.needWrite() = require(
    throws({ cause -> IOAccessException(this, AccessMode.WRITE, cause) }, { canWrite() })
) { IOAccessException(this, AccessMode.WRITE) }

/**
 * Throws an [IOAccessException] if the [File] does not support write.
 * */
fun File.needWrite() = require(
    throws({ cause -> IOAccessException(this, AccessMode.WRITE, cause) }, { canWrite() })
) { IOAccessException(this, AccessMode.WRITE) }

/**
 * @return [size] - human readable
 * */
val File.size: String
    get() = DataSize(length()).toString()

/**
 * @return [size] - human readable
 * */
val Path.size: String
    get() = DataSize(Files.size(this)).toString()

fun <T : Any> KClass<T>.instanceOf(other: KClass<*>): Boolean = this.java.instanceOf(other.java)

fun <T : Any> Class<T>.instanceOf(other: KClass<*>): Boolean = this.instanceOf(other.java)

/**
 * this function has same behaviour like `is` in kotlin or `instanceof` in java
 *
 * determine [T] is same or not with [other]
 * */
fun <T : Any> Class<T>.instanceOf(other: Class<*>): Boolean {
    if (other.isInterface) {
        if (this == other) {
            return true
        } else {
            this.interfaces.forEach {
                val faces: Class<*>? = it
                if (faces != null) {
                    if (faces == other) {
                        return true
                    } else {
                        faces.instanceOf(other)
                    }
                }
            }
            return false
        }
    } else {
        var supers: Class<in T>? = null
        if (this == other) {
            return true
        } else {
            while (true) {
                supers = if (supers == null) {
                    this.superclass
                } else {
                    supers.superclass
                }

                if (supers != null) {
                    if (supers == other) {
                        return true
                    }
                } else {
                    return false
                }
            }
        }
    }
}

/**
 * Creates a buffer input stream from [String].
 * @param bufSize the buffer size to use.
 */
@JvmOverloads
fun String.buffered(bufSize: Int = DEFAULT_BUFFER_SIZE): BufferedInputStream = byteInputStream().buffered(bufSize)

/**
 * check if [Double] is "power of two"
 */
fun Double.isPowerOfTwo(): Boolean {
    val c = ln(this) / ln(2.0)
    return (ceil(c).toInt() == floor(c).toInt())
}

/**
 * check if [Long] is "power of two"
 */
fun Long.isPowerOfTwo(): Boolean = toDouble().isPowerOfTwo()

/**
 * check if [Int] is "power of two"
 */
fun Int.isPowerOfTwo(): Boolean = toDouble().isPowerOfTwo()

fun StringBuilder.dot() = apply{
    append('.')
}

fun StringBuilder.appendNotNull(src: Any?) = apply{
    if (src != null){
        append(src)
    }
}

fun StringBuilder.comma() = apply{
    append(',')
}

fun StringBuilder.space() = apply{
    append(' ')
}

fun StringBuilder.replaceSafely(start: Int, end: Int, value: String) = apply{
    if (start != -1 && start < end){
        replace(start, end, value)
    }
}

fun CharSequence.indexOfFirst(c: Char): Int? {
    when (this) {
        is StringBuilder -> {
            val index = this.indexOf("$c")
            return if (index == -1){ null } else{ index }
        }
        else -> {
            for (index in this.indices) {
                if (c == this[index]) {
                    return index
                }
            }
        }
    }
    return null
}

/**
 * Enhanced from [String.replace] function.
 *
 * You can use this, if you have [oldValue] more than one to replace with [newValue]
 * @see requireAtLeastOne
 * @throws [IllegalStateException] if [oldValue] is empty
 * @param newValue replacement
 * @param oldValue should be one or more
 * @return a new [String]
 * */
@JvmOverloads
fun String.replaceAll(
    ignoreCase: Boolean = false,
    newValue: String,
    vararg oldValue: String
): String {
    oldValue.requireAtLeastOne()
    var result = this
    for (value in oldValue) {
        result = result.replace(value, newValue, ignoreCase)
    }
    return result
}

/**
 * replace [String] from index 0 until [endIndex] with [replacement]
 * @see replace
 * @see replaceRange
 * */
@JvmOverloads
fun String.replaceFirst(
    endIndex: Int,
    replacement: String = ""
): String = replaceRange(0, endIndex, replacement)

/**
 * replace [String] from [String.lastIndex] until [stopIndex] with [replacement].
 * @see replace
 * @see replaceRange
 * */
@JvmOverloads
fun String.replaceLast(
    stopIndex: Int,
    replacement: String = ""
): String = replaceRange(stopIndex, this.length, replacement)

fun String.replaceLast(oldValue: String, newValue: String): String {
    val index = lastIndexOf(oldValue)
    return if (index == -1) this else replaceRange(index, index + 1, newValue)
}

fun String.takeByRegex(
    pattern: Regex
): String {
    var result = ""
    for (i in pattern.findAll(this)) {
        result = i.value
    }
    return result
}

fun String.takeByRegex(
    pattern: Regex,
    contain: String
): String {
    var result = ""
    for (i in pattern.findAll(this)) {
        if (i.value.contains(contain)) {
            result = i.value
        }
    }
    return result
}

fun String.takeByRegex(
    pattern: Pattern,
    contain: String
): String = takeByRegex(pattern.toRegex(), contain)

fun String.takeByRegex(
    pattern: Pattern
): String = takeByRegex(pattern.toRegex())

/**
 * Enhanced from "contains" function.
 * This can be used
 * if you have more char sequence to check
 * with other sequence of characters.
 *
 * It will be checked all the value before returned,
 * if one value is FALSE, then it returns false
 * @param ignoreCase true to ignore character case when comparing strings. No Default Value
 * @param other CharSequence, can be more than 1
 * @return Boolean.
 * @see CharSequence.contains
 * */
fun CharSequence.containsAll(
    ignoreCase: Boolean,
    vararg other: CharSequence
): Boolean {
    other.requireAtLeastOne()
    var result = true
    for (key in other) {
        if (!contains(key, ignoreCase)) {
            result = false
            break
        }
    }
    return result
}

/**
 * Enhanced from "contains" function.
 * This can be used
 * if you have more char sequence to check
 * with other sequence of characters.
 *
 * It will be return immediately when one of all value is TRUE
 * @param ignoreCase true to ignore character case when comparing strings. No Default Value
 * @param other CharSequence, can be more than 1
 * @return Boolean.
 * @see CharSequence.contains
 * */
@JvmOverloads
fun CharSequence.containsOnlyOneOf(
    vararg other: CharSequence, ignoreCase: Boolean = true
): Boolean {
    other.requireAtLeastOne()
    var result = false
    for (sequence in other) {
        result = contains(sequence, ignoreCase)
        if (result) break
    }
    return result
}

/**
 * Enhanced from "contains" function.
 * Default value of IgnoreCase is Always True.
 * If you want use "contains" function
 * without ignore character Case, then you should use
 * contains( "string to check", false)
 * @param other CharSequence, can not more than 1
 * @return boolean.
 * @see kotlin.text.contains
 * */
fun CharSequence.contains(
    other: CharSequence
): Boolean = contains(other, true)

/**
 * @return [HttpUrl]
 * @throws [IllegalStateException] if the given [URL] can not convert into [HttpUrl]
 * @see toHttpUrlOrNull
 * @see notNull
 * */
fun URL.toHttpUrl(): HttpUrl = notNull(toHttpUrlOrNull())

/**
 * Creates a buffered input stream from [File].
 * @param bufSize the buffer size to use.
 * @return [BufferedInputStream]
 * @throws IllegalStateException if some requirement does not meet
 * @see FileInputStream
 * @see BufferedInputStream
 * */
@JvmOverloads
fun File.bufferedInput(bufSize: Int = DEFAULT_BUFFER_SIZE): BufferedInputStream {
    // always try to create directory
    mkdirs()
    // make sure this file is exist
    requireExist()
    // make sure this file can be read
    needRead()
    return BufferedInputStream(FileInputStream(this), bufSize)
}

/**
 * Creates a buffered output stream from [File].
 * @param bufSize the buffer size to use.
 * @return [BufferedOutputStream]
 * @throws IllegalStateException if some requirement does not meet
 * @see FileOutputStream
 * @see BufferedOutputStream
 * */
@JvmOverloads
fun File.bufferedOutput(bufSize: Int = DEFAULT_BUFFER_SIZE, append: Boolean = false): BufferedOutputStream {
    // always try to create directory
    mkdirs()
    // if exist then check IO requirement
    if (exists()) {
        // make sure this is a file
        shouldFile()
        // make sure this file dan be written
        needWrite()
    }
    return BufferedOutputStream(FileOutputStream(this, append), bufSize)
}

/**
 * Transfer input into output, auto close stream
 * @param output output stream for write a data
 * @param bufSize the buffer size to use.
 * @return [Long] - total bytes written
 * @see consume
 * */
@JvmOverloads
fun InputStream.transfer(output: OutputStream, bufSize: Int = DEFAULT_BUFFER_SIZE): Long {
    var transferred: Long = 0
    consume(output) { inp, out ->
        val buffer = ByteArray(bufSize)
        var read: Int
        while (inp.read(buffer).also { read = it } != EOF) {
            out.write(buffer, 0, read)
            transferred += read
        }
    }

    return transferred
}

/**
 * Executes the given [block] function on this resource and then closes it down correctly whether an exception
 * is thrown or not.
 * @param block a function to process this [Closeable] resource.
 * @return the result of [block] function invoked on this resource.
 * @see use
 */
@OptIn(ExperimentalContracts::class)
inline fun <I : InputStream?, O : OutputStream?, R> I.consume(output: O, block: (I, O) -> R): R {
    use { inp ->
        output.use { out ->
            return block(inp, out)
        }
    }

    /*
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    var exception: Throwable? = null
    try {
        return block(this, output)
    } catch (e: Throwable) {
        exception = e
        throw e
    } finally {
        when {
            this == null || output == null -> {}
            exception == null -> {
                close()
                output.close()
            }
            else -> try {
                close()
                output.close()
            } catch (closeException: Throwable) {
                // cause.addSuppressed(closeException) // ignored here
            }
        }
    }
     */

}

/**
 * make temporarily file with specific name and specific location.
 *
 * [prefix] and [suffix] for file name.
 *
 * [parentDir] and [subDir] for location of temporarily file.
 *
 * Default of [parentDir] is [Sys.appTmpDir]
 * @param prefix file first name.
 * @param suffix file last name, this is can be used as file extension.
 * @param subDir subdirectory for location of temporarily file.
 * @param parentDir parent directory for location of temporarily file.
 * @throws IllegalStateException if some requirement does not meet.
 * @return [Path]
 * @see requireIO
 * @see makeTempDir
 * @see Files.createTempFile
 * */
@JvmOverloads
fun makeTempFile(
    prefix: String? = null,
    suffix: String? = null,
    subDir: String? = null,
    parentDir: String = Sys.appTmpDir
): Path = Files.createTempFile(makeTempDir(subDir, parentDir), prefix, suffix).requireIO()

/**
 * make temporarily directory with specific location.
 *
 * [parentDir] and [subDir] for location.
 *
 * Default of [parentDir] is [Sys.appTmpDir].
 * @param subDir subdirectory for location.
 * @param parentDir parent directory for location.
 * @throws IllegalStateException if some requirement does not meet.
 * @return [Path]
 * @see requireIO
 * @see Files.createDirectories
 * */
@JvmOverloads
fun makeTempDir(
    subDir: String? = null,
    parentDir: String = Sys.appTmpDir
): Path = Files.createDirectories(Path(parentDir + subDir.orEmpty()).normalize()).requireIO()

/**
 * convert [String] into [ByteBuffer]
 * @see ByteBuffer.wrap
 * */
fun String.byteBuffer(): ByteBuffer {
    return ByteBuffer.wrap(this.toByteArray())
}

infix fun Byte.and(mask: Int): Int = toInt() and mask

val Response.Accept: String get() = headers[Headers.ACCEPT] ?: ""
val Response.AcceptEncoding: String get() = headers[Headers.ACCEPT_ENCODING] ?: ""
val Response.AcceptRanges: String get() = headers[Headers.ACCEPT_RANGES] ?: ""
val Response.Cookie: String get() = headers[Headers.COOKIE] ?: ""
val Response.Connection: String get() = headers[Headers.CONNECTION] ?: ""
val Response.ContentDisposition: String get() = headers[Headers.CONTENT_DISPOSITION] ?: ""
val Response.ContentLength: String get() = headers[Headers.CONTENT_LENGTH] ?: ""
val Response.ContentType: String get() = headers[Headers.CONTENT_TYPE] ?: ""
val Response.UserAgent: String get() = headers[Headers.USER_AGENT] ?: ""
val Response.Host: String get() = headers[Headers.HOST] ?: ""
val Response.Range: String get() = headers[Headers.RANGE] ?: ""
