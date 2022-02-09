package me.akhsaul.common.file

import me.akhsaul.common.file.filter.FileFilterUtils
import me.akhsaul.common.file.filter.FilterSuffix
import me.akhsaul.common.file.filter.IOFileFilter
import java.io.File
import java.io.FileFilter
import java.util.*

object FileUtils {
    fun listFiles(
        directory: File,
        fileFilter: IOFileFilter,
        dirFilter: IOFileFilter
    ): Collection<File> {
        return innerListFilesOrDirectories(directory, fileFilter, dirFilter, false)
    }

    /**
     * valid extension = ".java",".xml"
     * */
    fun listFiles(
        directory: File,
        extensions: Array<String>,
        recursive: Boolean = true
    ): Collection<File> {
        val filter: IOFileFilter = FilterSuffix(*extensions)
        return listFiles(
            directory, filter,
            if (recursive) IOFileFilter.TRUE else IOFileFilter.FALSE
        )
    }

    private fun innerListFilesOrDirectories(
        directory: File,
        fileFilter: IOFileFilter,
        dirFilter: IOFileFilter,
        includeSubDirectories: Boolean
    ): Collection<File> {
        validateListFilesParameters(directory, fileFilter)
        val effFileFilter = setUpEffectiveFileFilter(fileFilter)
        val effDirFilter = setUpEffectiveDirFilter(dirFilter)

        //Find files
        val files: MutableCollection<File> = LinkedList()
        if (includeSubDirectories) {
            files.add(directory)
        }
        innerListFiles(
            files, directory,
            FileFilterUtils.or(effFileFilter, effDirFilter), includeSubDirectories
        )
        return files
    }

    private fun innerListFiles(
        files: MutableCollection<File>,
        directory: File,
        filter: IOFileFilter,
        includeSubDirectories: Boolean
    ) {
        val found = directory.listFiles(filter as FileFilter)
        if (found != null) {
            for (file in found) {
                if (file.isDirectory) {
                    if (includeSubDirectories) {
                        files.add(file)
                    }
                    innerListFiles(files, file, filter, includeSubDirectories)
                } else {
                    files.add(file)
                }
            }
        }
    }

    private fun validateListFilesParameters(
        directory: File,
        fileFilter: IOFileFilter
    ) {
        require(directory.isDirectory) { "Parameter 'directory' is not a directory: $directory" }
        Objects.requireNonNull(fileFilter, "fileFilter")
    }

    private fun setUpEffectiveDirFilter(dirFilter: IOFileFilter): IOFileFilter {
        return FileFilterUtils.and(
            dirFilter,
            IOFileFilter.DIRECTORY
        )
    }

    private fun setUpEffectiveFileFilter(fileFilter: IOFileFilter): IOFileFilter {
        return FileFilterUtils.and(fileFilter, FileFilterUtils.not(IOFileFilter.DIRECTORY))
    }
}