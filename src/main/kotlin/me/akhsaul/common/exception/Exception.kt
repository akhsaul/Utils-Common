package me.akhsaul.common.exception

import me.akhsaul.common.*
import me.akhsaul.common.enums.HTTPMethod
import me.akhsaul.common.buildString
import java.io.IOException
import java.nio.file.AccessMode

class IllegalParameterException : RuntimeException {
    constructor() : super()
    constructor(message: String) : super(message)
    constructor(cause: Throwable) : super(cause)
    constructor(message: String, cause: Throwable) : super(message, cause)
}

class DeprecatedException : RuntimeException("These feature is Deprecated!")

class UnsupportedTypeException : RuntimeException {
    companion object {
        private fun msg(type: Class<*>): String {
            return buildString {
                append("Class Type").space()
                append(type).space()
                append("is Not Supported").dot()
            }
        }
    }

    constructor(type: Class<Any>, cause: Throwable) : super(msg(type), cause)
    constructor(type: Class<Any>) : super(msg(type))
}

class UnsupportedMethodException : RuntimeException {
    companion object {
        private fun msg(method: HTTPMethod): String {
            return buildString {
                append("Method").space()
                append(method).space()
                append("is Not Supported").dot()
            }
        }
    }

    constructor(method: HTTPMethod, cause: Throwable) : super(msg(method), cause)
    constructor(method: HTTPMethod) : super(msg(method))
}

class RequirementNotMeetException : RuntimeException {
    companion object {
        private fun msg(source: Any?, expected: Any, actual: Any, moreMsg: String?): String {
            return buildString {
                appendClass(source)
                append("expected is:").space()
                append(expected).comma().space()
                append("but actual is:").space()
                append(actual).dot().space()
                appendNotNull(moreMsg)?.dot()
            }
        }
    }

    constructor(source: Any?, expected: Any, actual: Any, moreMsg: String?, cause: Throwable) : super(
        msg(source, expected, actual, moreMsg),
        cause
    )
    constructor(source: Any?, expected: Any, actual: Any, cause: Throwable) : super(
        msg(source, expected, actual, null),
        cause
    )
    constructor(expected: Any, actual: Any, cause: Throwable) : super(
        msg(null, expected, actual, null),
        cause
    )

    @JvmOverloads
    constructor(source: Any?, expected: Any, actual: Any, moreMsg: String? = null) : super(
        msg(source, expected, actual, moreMsg)
    )
    constructor(expected: Any, actual: Any): super(
        msg(null, expected, actual, null)
    )
}

class IOAccessException : IOException {
    companion object {
        private fun msg(source: Any, mode: AccessMode): String {
            return buildString {
                appendClass(source)
                append("Access Denied for IO").space()
                append(mode).dot()
            }
        }
    }

    constructor(source: Any, mode: AccessMode, cause: Throwable) : super(msg(source, mode), cause)
    constructor(source: Any, mode: AccessMode) : super(msg(source, mode))
}

class ClassLoaderNotFoundException : Exception {
    constructor() : super()
    constructor(message: String) : super(message)
    constructor(cause: Throwable) : super(cause)
    constructor(message: String, cause: Throwable) : super(message, cause)
}

class ResponseException : Exception {
    constructor() : super()
    constructor(message: String) : super(message)
    constructor(cause: Throwable) : super(cause)
    constructor(message: String, cause: Throwable) : super(message, cause)
}

class ResourceNotFoundException : Exception {
    constructor() : super()
    constructor(message: String) : super(message)
    constructor(cause: Throwable) : super(cause)
    constructor(message: String, cause: Throwable) : super(message, cause)
}

class EmptyException : Exception {
    companion object {
        private fun msg(source: Any): String {
            return buildString {
                appendClass(source).space()
                append("is EMPTY").comma().space()
                append("Require at Least One").dot()
            }
        }
    }

    constructor(source: Any, cause: Throwable) : super(msg(source), cause)
    constructor(source: Any) : super(msg(source))
}

class NotExistsException : Exception {
    companion object {
        private fun msg(source: Any): String {
            return buildString {
                appendClass(source)
                append(" => ")
                append("is NOT EXISTS").comma().space()
                append("Require exists").dot()
            }
        }
    }

    constructor(source: Any, cause: Throwable) : super(msg(source), cause)
    constructor(source: Any) : super(msg(source))
}

class DecoderException : Exception {
    constructor() : super()
    constructor(message: String) : super(message)
    constructor(cause: Throwable) : super(cause)
    constructor(message: String, cause: Throwable) : super(message, cause)
}

/**
 * build message with internal buffer 64
 * */
private fun buildMessage(builderAction: StringBuilder.() -> Unit) = buildString(64, builderAction)