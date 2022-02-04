package me.akhsaul.common.custom

import me.akhsaul.common.notNull

object AnsiOutput {
    private const val ENCODE_JOIN = ";"
    private var consoleAvailable: Boolean? = null
    private var ansiCapable: Boolean? = null
    private val OPERATING_SYSTEM_NAME = System.getProperty("os.name").lowercase()
    private const val ENCODE_START = "\u001b["
    private const val ENCODE_END = "m"
    private val RESET = "0;" + AnsiColor.DEFAULT
    /**
     * Returns if ANSI output is enabled.
     * @return if ANSI enabled, disabled or detected
     */
    var enabled = Enabled.DETECT
        @JvmStatic set(value) {
            notNull(value) { IllegalArgumentException("Enabled must not be null") }
            field = value
        }
        @JvmStatic get

    /**
     * Sets if the System.console() is known to be available.
     *
     * @param consoleAvailable if the console is known to be available or `null` to
     * use standard detection logic.
     */
    @JvmStatic
    fun setConsoleAvailable(consoleAvailable: Boolean?) {
        this.consoleAvailable = consoleAvailable
    }

    /**
     * Encode a single [AnsiElement] if output is enabled.
     *
     * @param element the element to encode
     * @return the encoded element or an empty string
     */
    @JvmStatic
    fun encode(element: AnsiElement): String {
        return if (isEnabled()) {
            ENCODE_START + element + ENCODE_END
        } else ""
    }

    /**
     * Create a new ANSI string from the specified elements. Any [AnsiElement]s will
     * be encoded as required.
     *
     * @param elements the elements to encode
     * @return a string of the encoded elements
     */
    @JvmStatic
    fun toString(vararg elements: Any): String {
        val sb = StringBuilder()
        if (isEnabled()) {
            buildEnabled(sb, elements)
        } else {
            buildDisabled(sb, elements)
        }
        return sb.toString()
    }

    private fun buildEnabled(sb: StringBuilder, elements: Array<out Any>) {
        var writingAnsi = false
        var containsEncoding = false
        for (element in elements) {
            if (element is AnsiElement) {
                containsEncoding = true
                if (!writingAnsi) {
                    sb.append(ENCODE_START)
                    writingAnsi = true
                } else {
                    sb.append(ENCODE_JOIN)
                }
            } else {
                if (writingAnsi) {
                    sb.append(ENCODE_END)
                    writingAnsi = false
                }
            }
            sb.append(element)
        }
        if (containsEncoding) {
            sb.append(if (writingAnsi) ENCODE_JOIN else ENCODE_START)
            sb.append(RESET)
            sb.append(ENCODE_END)
        }
    }

    private fun buildDisabled(sb: StringBuilder, elements: Array<out Any>) {
        for (element in elements) {
            if (element !is AnsiElement) {
                sb.append(element)
            }
        }
    }

    private fun isEnabled(): Boolean {
        // force enable ANSI
        enabled = Enabled.ALWAYS

        if (enabled == Enabled.DETECT) {
            if (ansiCapable == null) {
                ansiCapable = detectIfAnsiCapable()
            }
            return ansiCapable!!
        }
        return enabled == Enabled.ALWAYS
    }

    private fun detectIfAnsiCapable(): Boolean {
        return try {
            if (consoleAvailable == false) {
                return false
            }
            if (consoleAvailable == null && System.console() == null) {
                false
            } else !OPERATING_SYSTEM_NAME.contains("win")
        } catch (_: Throwable) {
            false
        }
    }

    /**
     * Possible values to pass to [AnsiOutput.enabled]. Determines when to output
     * ANSI escape sequences for coloring application output.
     */
    enum class Enabled {
        /**
         * Try to detect whether ANSI coloring capabilities are available. The default
         * value for [AnsiOutput].
         */
        DETECT,

        /**
         * Enable ANSI-colored output.
         */
        ALWAYS,

        /**
         * Disable ANSI-colored output.
         */
        NEVER
    }
}