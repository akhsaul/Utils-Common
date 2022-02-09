package me.akhsaul.common.tools

import org.slf4j.LoggerFactory
import java.io.IOException
import java.nio.file.Path
import java.util.*

class TreeWalker @JvmOverloads internal constructor(
    private val start: Data,
    private val direction: Direction = Direction.UP_DOWN,
    private val onEnter: ((Data) -> Boolean)? = null,
    private val onExit: ((Data) -> Unit)? = null,
    private val onError: ((Data, IOException) -> Unit)? =null,
    private val maxDepth: Int = Int.MAX_VALUE,
    private val excludePath: List<Path> = listOf(
        Path.of("\$RECYCLE.BIN"),
        Path.of("System Volume Information")
    ),
) : Sequence<Data> {
    companion object{
        private val LOG = LoggerFactory.getLogger(TreeWalker::class.java)
    }
    enum class Direction {
        UP_DOWN,
        DOWN_UP
    }

    private inner class SingleFileState(root: Data) : State(root) {
        private var visited: Boolean = false

        override fun step(): Data? {
            if (visited) {
                return null
            }
            visited = true
            return root
        }
    }

    private abstract inner class DirectoryState(root: Data) : State(root)
    private abstract inner class State(val root: Data) {
        abstract fun step(): Data?
    }

    private inner class TopDownDirectoryState(rootDir: Data) : DirectoryState(rootDir) {
        private var rootVisited = false

        private var fileList: List<Data>? = null

        private var fileIndex = 0

        override fun step(): Data? {
            if (!rootVisited) {
                excludePath.forEach {
                    if(root.toPath().contains(it)){
                        LOG.info("SKIP, $root")
                        return null
                    }
                }

                if (onEnter?.invoke(root) == false) {
                    onExit?.invoke(root)
                    return null
                }
                rootVisited = true
                return root
            } else if (fileList == null || fileIndex < fileList!!.size) {
                if (fileList == null) {
                    // Then read an array of files, if any
                    fileList = root.list()
                    if (fileList == null) {
                        onError?.invoke(
                            root,
                            IOException("Cannot list files in a directory")
                            //AccessDeniedException(file = root, reason = "Cannot list files in a directory")
                        )
                    }
                    if (fileList == null || fileList!!.isEmpty()) {
                        onExit?.invoke(root)
                        return null
                    }
                }
                // Then visit all files
                return fileList!![fileIndex++]
            } else {
                // That's all
                onExit?.invoke(root)
                return null
            }
        }
    }

    private inner class BottomUpDirectoryState(rootDir: Data) : DirectoryState(rootDir) {
        override fun step(): Data? {
            TODO("Not yet implemented")
        }
    }

    private inner class WalkIterator : AbstractIterator<Data>() {
        private val state = ArrayDeque<State>()

        init {
            when {
                start.isDirectory -> state.push(directoryState(start))
                start.isFile -> state.push(SingleFileState(start))
                else -> done()
            }
        }

        private fun directoryState(root: Data): DirectoryState {
            return when (direction) {
                Direction.UP_DOWN -> TopDownDirectoryState(root)
                Direction.DOWN_UP -> BottomUpDirectoryState(root)
            }
        }

        /**
         * Computes the next item in the iterator.
         *
         * This callback method should call one of these two methods:
         *
         * * [setNext] with the next value of the iteration
         * * [done] to indicate there are no more elements
         *
         * Failure to call either method will result in the iteration terminating with a failed state
         */
        override fun computeNext() {
            val nextData = gotoNext()
            if (nextData != null) {
                setNext(nextData)
            } else {
                done()
            }
        }

        private tailrec fun gotoNext(): Data? {
            val topState = state.peek() ?: return null
            val data = topState.step()
            return if (data == null) {
                state.pop()
                gotoNext()
            } else {
                if (data == topState.root || data.isFile || state.size >= maxDepth) {
                    data
                } else {
                    state.push(directoryState(data))
                    gotoNext()
                }
            }
        }
    }

    /**
     * Returns an [Iterator] that returns the values from the sequence.
     *
     * Throws an exception if the sequence is constrained to be iterated once and `iterator` is invoked the second time.
     */
    override fun iterator(): Iterator<Data> {
        return WalkIterator()
    }
}