package me.akhsaul.common.custom

enum class AnsiStyle(private val code: String) : AnsiElement {
    NORMAL("0"),
    BOLD("1"),
    FAINT("2"),
    ITALIC("3"),
    UNDERLINE("4");

    override fun toString(): String {
        return this.code
    }
}