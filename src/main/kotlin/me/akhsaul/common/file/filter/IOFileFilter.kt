package me.akhsaul.common.file.filter

import java.io.File
import java.io.FileFilter
import java.io.FilenameFilter

interface IOFileFilter : FileFilter, FilenameFilter {
    companion object {
        @JvmField
        val EMPTY_STRING_ARRAY = arrayOfNulls<String>(0)

        @JvmField
        val TRUE: IOFileFilter = FilterTrue()

        @JvmField
        val FALSE: IOFileFilter = FilterFalse()

        @JvmField
        val DIRECTORY: IOFileFilter = FilterDir()
    }

    override fun accept(file: File): Boolean
    override fun accept(dir: File, name: String): Boolean

    private class FilterTrue : IOFileFilter {

        override fun accept(file: File): Boolean {
            return true
        }

        override fun accept(dir: File, name: String): Boolean {
            return true
        }
    }

    private class FilterFalse : IOFileFilter {
        /**
         * Returns false.
         *
         * @param file  the file to check (ignored)
         * @return false
         */
        override fun accept(file: File): Boolean {
            return false
        }

        /**
         * Returns false.
         *
         * @param dir  the directory to check (ignored)
         * @param name  the file name (ignored)
         * @return false
         */
        override fun accept(dir: File, name: String): Boolean {
            return false
        }
    }

    private class FilterDir : AbstractFileFilter() {
        /**
         * Checks to see if the file is a directory.
         *
         * @param file  the File to check
         * @return true if the file is a directory
         */
        override fun accept(file: File): Boolean {
            return file.isDirectory
        }
    }
}