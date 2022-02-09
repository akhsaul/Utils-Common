package me.akhsaul.common.io

import java.io.Writer

class WriterBuilder : Writer {
    private var builder = StringBuilder()

    /**
     * Constructs a new [StringBuilder] instance with default capacity.
     */
    constructor() {
        builder = StringBuilder()
    }

    /**
     * Constructs a new [StringBuilder] instance with the specified capacity.
     *
     * @param capacity The initial capacity of the underlying [StringBuilder]
     */
    constructor(capacity: Int) {
        builder = StringBuilder(capacity)
    }

    /**
     * Constructs a new instance with the specified [StringBuilder].
     *
     *
     * If `builder` is null a new instance with default capacity will be created.
     *
     * @param builder The String builder. May be null.
     */
    constructor(builder: StringBuilder) {
        this.builder = builder
    }

    /**
     * Appends a single character to this Writer.
     *
     * @param value The character to append
     * @return This writer instance
     */
    override fun append(value: Char): Writer {
        builder.append(value)
        return this
    }

    /**
     * Appends a character sequence to this Writer.
     *
     * @param value The character to append
     * @return This writer instance
     */
    override fun append(value: CharSequence): Writer {
        builder.append(value)
        return this
    }

    /**
     * Appends a portion of a character sequence to the [StringBuilder].
     *
     * @param value The character to append
     * @param start The index of the first character
     * @param end The index of the last character + 1
     * @return This writer instance
     */
    override fun append(value: CharSequence, start: Int, end: Int): Writer {
        builder.append(value, start, end)
        return this
    }

    /**
     * Closing this writer has no effect.
     */
    override fun close() {
        // no-op
    }

    /**
     * Flushing this writer has no effect.
     */
    override fun flush() {
        // no-op
    }


    /**
     * Writes a String to the [StringBuilder].
     *
     * @param value The value to write
     */
    override fun write(value: String) {
        if (value != null) {
            builder.append(value)
        }
    }

    /**
     * Writes a portion of a character array to the [StringBuilder].
     *
     * @param value The value to write
     * @param offset The index of the first character
     * @param length The number of characters to write
     */
    override fun write(value: CharArray, offset: Int, length: Int) {
        builder.append(value, offset, length)
    }

    /**
     * Returns the underlying builder.
     *
     * @return The underlying builder
     */
    fun getBuilder(): StringBuilder {
        return builder
    }

    /**
     * Returns [StringBuilder.toString].
     *
     * @return The contents of the String builder.
     */
    override fun toString(): String {
        return builder.toString()
    }
}