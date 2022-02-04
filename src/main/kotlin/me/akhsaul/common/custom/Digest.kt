package me.akhsaul.common.custom

import java.nio.ByteBuffer

interface Digest {
    fun update(input: ByteArray, offset: Int, len: Int)
    fun update(input: ByteArray)
    fun update(input: ByteBuffer)
    fun getResult(toLowerCase: Boolean): CharArray
}