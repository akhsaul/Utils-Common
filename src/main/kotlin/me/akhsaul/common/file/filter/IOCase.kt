package me.akhsaul.common.file.filter

import me.akhsaul.common.io.Utils.isWinSystem
import java.util.*

/**
 * Constructs a new instance.
 *
 * @param CaseName  the name
 * @param isCaseSensitive  the sensitivity
 */
enum class IOCase(val CaseName: String, @field:Transient val isCaseSensitive: Boolean) {
    /**
     * The constant for case sensitive regardless of operating system.
     */
    SENSITIVE("Sensitive", true),

    /**
     * The constant for case insensitive regardless of operating system.
     */
    INSENSITIVE("Insensitive", false),

    /**
     * The constant for case sensitivity determined by the current operating system.
     * Windows is case-insensitive when comparing file names, Unix is case-sensitive.
     *
     *
     * **Note:** This only caters for Windows and Unix. Other operating
     * systems (e.g. OSX and OpenVMS) are treated as case sensitive if they use the
     * Unix file separator and case-insensitive if they use the Windows file separator
     * (see [java.io.File.separatorChar]).
     *
     *
     * If you serialize this constant on Windows, and deserialize on Unix, or vice
     * versa, then the value of the case-sensitivity flag will change.
     */
    SYSTEM("System", !isWinSystem);

    /**
     * Replaces the enumeration from the stream with a real one.
     * This ensures that the correct flag is set for SYSTEM.
     *
     * @return the resolved object
     */
    private fun readResolve(): Any {
        return forName(name)
    }

    /**
     * Compares two strings using the case-sensitivity rule.
     *
     *
     * This method mimics [String.compareTo] but takes case-sensitivity
     * into account.
     *
     * @param str1  the first string to compare, not null
     * @param str2  the second string to compare, not null
     * @return true if equal using the case rules
     * @throws NullPointerException if either string is null
     */
    fun checkCompareTo(str1: String, str2: String): Int {
        Objects.requireNonNull(str1, "str1")
        Objects.requireNonNull(str2, "str2")
        return if (isCaseSensitive) str1.compareTo(str2) else str1.compareTo(str2, ignoreCase = true)
    }

    /**
     * Compares two strings using the case-sensitivity rule.
     *
     *
     * This method mimics [String.equals] but takes case-sensitivity
     * into account.
     *
     * @param str1  the first string to compare, not null
     * @param str2  the second string to compare, not null
     * @return true if equal using the case rules
     * @throws NullPointerException if either string is null
     */
    fun checkEquals(str1: String, str2: String): Boolean {
        Objects.requireNonNull(str1, "str1")
        Objects.requireNonNull(str2, "str2")
        return if (isCaseSensitive) str1 == str2 else str1.equals(str2, ignoreCase = true)
    }

    /**
     * Checks if one string starts with another using the case-sensitivity rule.
     *
     *
     * This method mimics [String.startsWith] but takes case-sensitivity
     * into account.
     *
     * @param str  the string to check, not null
     * @param start  the start to compare against, not null
     * @return true if equal using the case rules
     * @throws NullPointerException if either string is null
     */
    fun checkStartsWith(str: String, start: String): Boolean {
        return str.regionMatches(0, start, 0, start.length, ignoreCase = !isCaseSensitive)
    }

    /**
     * Checks if one string ends with another using the case-sensitivity rule.
     *
     *
     * This method mimics [String.endsWith] but takes case-sensitivity
     * into account.
     *
     * @param str  the string to check, not null
     * @param end  the end to compare against, not null
     * @return true if equal using the case rules
     * @throws NullPointerException if either string is null
     */
    fun checkEndsWith(str: String, end: String): Boolean {
        return str.regionMatches(
            str.length - end.length,
            end, 0,
            end.length, ignoreCase = !isCaseSensitive
        )
    }

    /**
     * Checks if one string contains another starting at a specific index using the
     * case-sensitivity rule.
     *
     *
     * This method mimics parts of [String.indexOf]
     * but takes case-sensitivity into account.
     *
     * @param str  the string to check, not null
     * @param strStartIndex  the index to start at in str
     * @param search  the start to search for, not null
     * @return the first index of the search String,
     * -1 if no match or `null` string input
     * @throws NullPointerException if either string is null
     * @since 2.0
     */
    fun checkIndexOf(str: String, strStartIndex: Int, search: String): Int {
        val endIndex = str.length - search.length
        if (endIndex >= strStartIndex) {
            for (i in strStartIndex..endIndex) {
                if (checkRegionMatches(str, i, search)) {
                    return i
                }
            }
        }
        return -1
    }

    /**
     * Checks if one string contains another at a specific index using the case-sensitivity rule.
     *
     *
     * This method mimics parts of [String.regionMatches]
     * but takes case-sensitivity into account.
     *
     * @param str  the string to check, not null
     * @param strStartIndex  the index to start at in str
     * @param search  the start to search for, not null
     * @return true if equal using the case rules
     * @throws NullPointerException if either string is null
     */
    fun checkRegionMatches(str: String, strStartIndex: Int, search: String): Boolean {
        return str.regionMatches(
            strStartIndex, search, 0,
            search.length, ignoreCase = !isCaseSensitive
        )
    }

    /**
     * Gets a string describing the sensitivity.
     *
     * @return a string describing the sensitivity
     */
    override fun toString(): String {
        return name
    }

    companion object {
        /**
         * Factory method to create an IOCase from a name.
         *
         * @param name  the name to find
         * @return the IOCase object
         * @throws IllegalArgumentException if the name is invalid
         */
        fun forName(name: String): IOCase {
            for (ioCase in values()) {
                if (ioCase.CaseName == name) {
                    return ioCase
                }
            }
            throw IllegalArgumentException("Invalid IOCase name: $name")
        }
    }
}