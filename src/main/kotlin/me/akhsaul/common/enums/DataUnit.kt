package me.akhsaul.common.enums

enum class DataUnit(val position: Int, val ext: String, val dif: Int) {
    //convert bytes to automatically choose
    AUTO(-1, "AUTO", -1),
    BYTES(0, "Bytes", 1),
    KILO_BYTES(1, "KB", 1),
    MEGA_BYTES(2, "MB", 1),
    GIGA_BYTES(3, "GB", 1),
    TERA_BYTES(4, "TB", 1),
    PETA_BYTES(5, "PB", 1),
    KIBI_BYTES(-1, "?", -1),
    BITS(0, "bits", 8),
    KILO_BITS(1, "Kb", 8),
    MEGA_BITS(2, "Mb", 8),
    GIGA_BITS(3, "Gb", 8),
    TERA_BITS(4, "Tb", 8),
    PETA_BITS(5, "Pb", 8),
    KIBI_BITS(-1,"?",-1),
}