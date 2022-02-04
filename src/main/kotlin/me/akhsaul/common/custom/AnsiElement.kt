package me.akhsaul.common.custom

interface AnsiElement {
    /**
     * @return the ANSI escape code
     */
    override fun toString(): String
}