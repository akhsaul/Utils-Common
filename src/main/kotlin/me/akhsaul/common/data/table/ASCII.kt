package me.akhsaul.common.data.table

object ASCII : Chars() {
    override fun hexadecimal(): List<String> {
        return buildList {
            (32..127).forEach {
                add(it.toString())
            }
        }
    }
}