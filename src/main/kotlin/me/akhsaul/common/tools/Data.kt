package me.akhsaul.common.tools

import me.akhsaul.common.logger
import me.akhsaul.common.notNull
import me.akhsaul.common.require
import me.akhsaul.common.withLock
import java.io.File
import java.nio.file.*
import java.nio.file.spi.FileSystemProvider
import kotlin.io.NoSuchFileException

class Data constructor(val path: Path) {
    constructor(file: File) : this(FileSystems.getDefault().getPath(file.path))

    companion object {
        private val LOG = logger { }
        private val trashPath = Path.of("\$RECYCLE.BIN")
    }

    private var onError: (Data, Exception) -> Unit = { data, exception ->
        LOG.error("Error Happened, data = $data.")
        throw exception
    }

    @JvmField
    val isTrash: Boolean = path.contains(trashPath)

    // call only once, improve performance
    private var _attribute: Attribute? = null
    private var _provider: FileSystemProvider? = null

    val isDirectory: Boolean
        get() = getAttribute().isDirectory

    val isFile: Boolean
        get() = getAttribute().isFile

    val canRead: Boolean
        get() = canAccess(AccessMode.READ)

    val canWrite: Boolean
        get() = canAccess(AccessMode.WRITE)

    val canExecute: Boolean
        get() = canAccess(AccessMode.EXECUTE)

    val isExist: Boolean
        get() = getAttribute().isExist

    @JvmOverloads
    fun size(recursive: Boolean = false): Long {
        return getAttribute().size(recursive)
    }

    @JvmOverloads
    fun readableSize(recursive: Boolean = false): String {
        return getAttribute().readableSize(recursive)
    }

    fun getAttribute(): Attribute {
        if (_attribute == null) {
            _attribute = withLock {
                _attribute ?: Attribute(path)
            }
        }
        return notNull(_attribute)
    }

    private fun getProvider(): FileSystemProvider {
        if (_provider == null) {
            _provider = withLock {
                _provider ?: path.fileSystem.provider()
            }
        }
        return notNull(_provider)
    }

    private fun canAccess(mode: AccessMode): Boolean {
        return runCatching {
            getProvider().checkAccess(path, mode)
            true
        }.getOrElse {
            LOG.debug("Can't access with $mode", it)
            false
        }
    }

    fun isSame(other: Path): Boolean {
        return Files.isSameFile(path, other)
    }

    fun isSame(other: File): Boolean {
        return isSame(other.toPath())
    }

    /**
     * - returning list of File in current directory, if this is represented of directory.
     * - returning NULL, if [Exception] happened.
     * - returning empty list, if this is represented of file.
     * */
    fun list(): List<Data>? {
        return try {
            if (isDirectory) {
                buildList {
                    getProvider().newDirectoryStream(path) { true }.use { stream ->
                        stream.forEach {
                            add(Data(it))
                        }
                    }
                }
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            LOG.debug("Exception Ignored. When listing the $this", e)
            null
        }
    }

    fun setOnError(function: (Data, Exception) -> Unit) = apply {
        onError = function
    }

    fun listFiles() {

    }

    @JvmOverloads
    fun walk(
        direction: TreeWalker.Direction = TreeWalker.Direction.UP_DOWN,
        onEnter: ((Data) -> Boolean)? = { !it.isTrash },
        onExit: ((Data) -> Unit)? = null,
        onError: ((Data, Exception) -> Unit)? = this.onError,
        maxDepth: Int = Int.MAX_VALUE
    ): TreeWalker {
        require(maxDepth >= 0 && maxDepth <= Int.MAX_VALUE) {
            IllegalArgumentException("Requirements not meet, maxDepth should be in 0 until ${Int.MAX_VALUE}")
        }
        LOG.info("Start to Walk.")
        return TreeWalker(this, direction, onEnter, onExit, onError, maxDepth)
    }

    fun walkUpDown() {
        walk(TreeWalker.Direction.UP_DOWN)
    }

    fun walkDownUp() {
        walk(TreeWalker.Direction.DOWN_UP)
    }

    @JvmOverloads
    fun copy(recursive: Boolean = false) {
        if (recursive) {
            walk()
        }
    }

    @JvmOverloads
    fun copyIfExist(recursive: Boolean = false) {
        if (recursive) {
        }
    }

    /**
     * delete [Data]
     * @param recursive delete [Data] recursively or not
     * @throws NoSuchFileException if [Data] is not exist
     * @throws DirectoryNotEmptyException if [Data] is directory and it is not empty
     * @return TRUE - if [Data] was deleted
     * */
    @JvmOverloads
    fun delete(recursive: Boolean = false): Boolean {
        return if (recursive) {
            var result = false
            LOG.info("Starting. Delete recursively.")
            walk().forEach {
                result = it.delete()
            }
            LOG.info("Done. Delete recursively.")
            result
        } else {
            LOG.info("Try to delete.")
            getProvider().delete(path)
            isExist
        }
    }

    /**
     * delete [Data] with ignoring [NoSuchFileException]
     * @param recursive delete [Data] recursively or not
     * @throws DirectoryNotEmptyException if [Data] is directory and it is not empty
     * @return TRUE - if [Data] was deleted or is not exist
     * */
    @JvmOverloads
    fun deleteIfExist(recursive: Boolean = false) {
        if (recursive) {
            LOG.info("Starting. DeleteIfExist recursively.")
            walk().forEach {
                it.deleteIfExist()
            }
            LOG.info("Done. DeleteIfExist recursively.")
        } else {
            try {
                LOG.info("Try to deleteIfExist.")
                getProvider().delete(path)
            } catch (e: NoSuchFileException) {
                LOG.debug("NoSuchFileException Ignored, when trying to delete the $this.")
            }
        }
    }

    fun search() {
    }

    fun listByExtension(vararg extensions: String) {
        val result = mutableSetOf<Data>()
        walk().forEach {
            extensions.forEach { ext ->
                if(it.path.toString().endsWith(ext)){
                    result.add(it)
                }
            }
        }
    }

    override fun toString(): String {
        return "Data(path='$path', hashCode=${hashCode()})"
    }
}