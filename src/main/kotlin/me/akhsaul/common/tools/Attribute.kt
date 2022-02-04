package me.akhsaul.common.tools

import me.akhsaul.common.logger
import me.akhsaul.common.math.DataSize
import me.akhsaul.common.notNull
import me.akhsaul.common.withLock
import java.io.File
import java.nio.file.LinkOption
import java.nio.file.Path
import java.nio.file.attribute.FileOwnerAttributeView
import java.nio.file.attribute.FileTime
import java.nio.file.attribute.UserPrincipal

/**
 * TODO change every variable into function
 *   we should not store value!!
 *   because, sometimes file or directory can be deleted by another process
 *   that will make different attribute (inconsistent)
 * */
@Suppress("unused")
class Attribute @JvmOverloads constructor(
    private val path: Path,
    private val followLink: Boolean = false
) {
    @JvmOverloads
    constructor(data: Data, followLink: Boolean = false)
            : this(data.path, followLink)

    @JvmOverloads
    constructor(file: File, followLink: Boolean = false)
            : this(file.toPath(), followLink)

    companion object {
        private val LOG = logger { }
    }

    private val provider = path.fileSystem.provider()
    private val supported = path.fileSystem.supportedFileAttributeViews()

    @JvmField
    val isPosix = supported.contains("posix")

    val isFile
        get() = basic(false, "isRegularFile")

    val isDirectory
        get() = basic(false, "isDirectory")

    val isSymbolicLink
        get() = basic(false, "isSymbolicLink")

    val creationTime
        get() = basic<FileTime>(FileTime.fromMillis(-1), "creationTime")

    val lastAccessTime
        get() = basic<FileTime>(FileTime.fromMillis(-1), "lastAccessTime")

    val lastModifiedTime
        get() = basic<FileTime>(FileTime.fromMillis(-1), "lastModifiedTime")

    val owner: UserPrincipal?
        get() = try {
            if (followLink) {
                provider.getFileAttributeView(path, FileOwnerAttributeView::class.java)
            } else {
                provider.getFileAttributeView(path, FileOwnerAttributeView::class.java, LinkOption.NOFOLLOW_LINKS)
            }.owner
        } catch (_: Exception) {
            null
        }

    /**
     * owner name, can be empty string
     * */
    val ownerName
        get() = owner?.name ?: ""

    /**
     * if [get] function return a null then file or directory is not exist
     * */
    val isExist: Boolean
        get() = get<Any>("*") != null

    val isReadOnly
        get() = get("dos:readonly") ?: false

    val isHidden
        get() = get("dos:hidden") ?: false

    val isSystem
        get() = get("dos:system") ?: false

    val isArchive
        get() = get("dos:archive") ?: false

    @Suppress("unchecked_cast")
    private fun <T : Any> get(attr: String): T? {
        return runCatching {
            val result = if (followLink) {
                provider.readAttributes(path, attr)
            } else {
                provider.readAttributes(path, attr, LinkOption.NOFOLLOW_LINKS)
            }

            val key = attr.substring(attr.indexOf(":") + 1, attr.length)
            if (key == "*") {
                result as T
            } else {
                result[key] as T
            }
        }.getOrElse {
            LOG.warn("Exception ignored, when get attribute of $path, name: '$attr'.", it)
            null
        }
    }

    private fun <T : Any> basic(default: T, attr: String): T {
        return if (isPosix) {
            get("posix:$attr") ?: default
        } else {
            get("basic:$attr") ?: default
        }
    }

    private var _size: Long? = null

    @JvmOverloads
    fun size(recursive: Boolean = false): Long {
        if (_size == null) {
            _size = withLock {
                _size ?: if (isFile) {
                    basic(0L, "size")
                } else {
                    if (recursive && isDirectory) {
                        var counted = 0L
                        Data(path).walk(
                            // don't enter trash path
                            onEnter = { !it.isTrash },
                            // ignore exception then set counted to zero
                            onError = { _, exception ->
                                LOG.warn("Exception ignored, when counting file size.", exception)
                                LOG.info("Set 'counted' into zero.")
                                counted = 0L
                                return@walk
                            }).forEach {
                            // walk function will return file or directory
                            // only process if it is a file
                            if (it.isFile) {
                                counted += it.size(false)
                            }
                        }
                        // return counted size
                        // can be zero or more
                        counted
                    } else {
                        0L
                    }
                }
            }
        }
        return notNull(_size)
    }

    private var _sizeStr: String? = null

    @JvmOverloads
    fun readableSize(recursive: Boolean = false): String {
        if (_sizeStr == null) {
            _sizeStr = withLock {
                _sizeStr ?: DataSize(size(recursive)).toString()
            }
        }
        return notNull(_sizeStr)
    }

    override fun toString(): String {
        return "Attribute(path='$path', hashCode=${hashCode()})"
    }
}