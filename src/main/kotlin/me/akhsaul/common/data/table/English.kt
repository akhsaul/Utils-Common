package me.akhsaul.common.data.table

import me.akhsaul.common.tools.Hex
import kotlin.math.pow

object English : Chars() {
    /**
     *  listOf(
    "20", "21", "22", "23", "24",
    "25", "26", "27", "28", "29",
    "2A", "2B", "2C", "2D", "2E",
    "2F", "30", "31", "32", "33",
    "34", "35", "36", "37", "38",
    "39", "3A", "3B", "3C", "3D",
    "3E", "3F", "40", "41", "42",
    "43", "44", "45", "46", "47",
    "48", "49", "4A", "4B", "4C",
    "4D", "4E", "4F", "50", "51",
    "52", "53", "54", "55", "56",
    "57", "58", "59", "5A", "5B",
    "5C", "5D", "5E", "5F", "61",
    "62", "63", "64", "65", "66",
    "67", "68", "69", "6A", "6B",
    "6C", "6D", "6E", "6F", "70",
    "71", "72", "73", "74", "75",
    "76", "77", "78", "79", "7A",
    "7C", "A0", "A7", "A9",
    "2010", "2011", "2013", "2014", "2018",
    "2019", "201C", "201D", "2020", "2021",
    "2026", "2030", "2032", "2033", "20AC",
    )
     * */
    override fun hexadecimal(): List<String>{
        return buildList {
            (20..29).forEach {
                add(it.toString())
            }
        }
    }
}

fun main(){
    English.hexadecimal().forEach {
        println(it)
    }
    println(String(Hex.decodeHex("201D")))
    val tes = ByteArray(2)
    val c = "201D".toDecimal()
    ("68456c4c6f20576f526c4421" as java.lang.String)
    val str = Character.toString(c)
    println(str)
    ("””").forEach {
        println(it.code)
    }
    val g = 0x201D
    val t = 0xDFFF
    println(String(Hex.decodeHex("E2809D".toCharArray())))
    println(String(
        charArrayOf((0x20AC).toChar())
    ))
}

private fun String.toDecimal(): Int {
    var n = length - 1
    var result = 0
    this.forEach {
        println(it.digitToInt(16))
        println(16f.pow(n))
        result += it.digitToInt(16).times(
            (16f).pow(n)
        ).toInt()
        println(result)
        n--
    }
    return result
}