@file:Suppress("unused")

package me.akhsaul.common.exception

import me.akhsaul.common.enum.HTTPMethod
import me.akhsaul.common.tools.Data
import java.io.File
import java.io.IOException
import java.nio.file.AccessMode
import java.nio.file.Path

class IllegalParameterException : RuntimeException {
    constructor() : super()
    constructor(message: String?) : super(message)
    constructor(message: String?, cause: Throwable?) : super(message, cause)
    constructor(cause: Throwable?) : super(cause)
}

class DeprecatedException : RuntimeException("These feature is Deprecated!")

class UnsupportedMethodException : RuntimeException {
    constructor(method: HTTPMethod, cause: Throwable?) : super(
        buildString {
            append("Method ")
            append(method)
            append(" is Not Supported.")
        }, cause
    )

    constructor(method: HTTPMethod) : this(method, null)
}

class RequirementNotMeetException : RuntimeException {
    constructor(source: Any?, expected: Any, actual: Any, cause: Throwable?) : super(
        buildString {
            buildPath(source)
            append("expected is: ")
            append(expected)
            append(", but actual is: ")
            append(actual)
            append(".")
        },
        cause
    )

    constructor(source: Any?, expected: Any, actual: Any) : this(source, expected, actual, null)
    constructor(expected: Any, actual: Any) : this(null, expected, actual)
}

class IOAccessException : IOException {
    constructor(source: Any, mode: AccessMode, cause: Throwable?) : super(
        buildString {
            buildPath(source)
            append("Access Denied for IO ")
            append(mode)
            append(".")
        },
        cause
    )

    constructor(source: Any, mode: AccessMode) : this(source, mode, null)
}

class ClassLoaderNotFoundException : Exception {
    constructor() : super()
    constructor(message: String?) : super(message)
    constructor(message: String?, cause: Throwable?) : super(message, cause)
    constructor(cause: Throwable?) : super(cause)
}

class ResponseException : Exception {
    constructor() : super()
    constructor(message: String?) : super(message)
    constructor(message: String?, cause: Throwable?) : super(message, cause)
    constructor(cause: Throwable?) : super(cause)
}

class ResourceNotFoundException : Exception {
    constructor() : super()
    constructor(message: String?) : super(message)
    constructor(message: String?, cause: Throwable?) : super(message, cause)
    constructor(cause: Throwable?) : super(cause)
}

class EmptyException : Exception {
    constructor(source: Any, cause: Throwable?) : super(
        buildString {
            when (source) {
                is Array<*> -> {
                    append("Array ")
                }
                is List<*> -> {
                    append("List ")
                }
                is Set<*> -> {
                    append("Set ")
                }
                is Map<*, *> -> {
                    append("Map ")
                }
            }
            append("is EMPTY, ")
            append("Require at Least One.")
        },
        cause
    )

    constructor(source: Any) : this(source, null)
}

class NotExistsException : Exception {
    constructor(source: Any, cause: Throwable?) : super(
        buildString {
            buildPath(source)
            append("is NOT EXISTS, ")
            append("Require exists.")
        },
        cause
    )

    constructor(source: Any) : this(source, null)
}

class DecoderException : Exception {
    constructor() : super()
    constructor(message: String?) : super(message)
    constructor(message: String?, cause: Throwable?) : super(message, cause)
    constructor(cause: Throwable?) : super(cause)
}

private fun StringBuilder.buildPath(source: Any?) = apply {
    if (source != null) {
        when (source) {
            is Path -> {
                append("Path of ")
            }
            is File -> {
                append("File of ")
            }
            is Data -> {
                append("Data of ")
            }
            else -> {}
        }
        append('\'')
        append(source)
        append('\'')
        append(" => ")
    }
}