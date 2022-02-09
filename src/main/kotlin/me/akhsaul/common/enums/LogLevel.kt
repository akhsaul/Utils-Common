package me.akhsaul.common.enums

import org.apache.logging.log4j.Level

enum class LogLevel(val level: Level) {
    WARN(Level.WARN),
    DEBUG(Level.DEBUG),
    TRACE(Level.TRACE),
    INFO(Level.INFO),
    ALL(Level.ALL),
    ERROR(Level.ERROR),
    FATAL(Level.FATAL),
    OFF(Level.OFF)
}