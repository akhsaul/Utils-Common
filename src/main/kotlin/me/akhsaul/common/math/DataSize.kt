package me.akhsaul.common.math

import me.akhsaul.common.enums.DataUnit
import me.akhsaul.common.exception.RequirementNotMeetException
import me.akhsaul.common.replaceSafely
import me.akhsaul.common.require
import java.math.BigDecimal

@Suppress("all")
class DataSize @JvmOverloads constructor(
    size: Long,
    fromUnit: DataUnit = DataUnit.BYTES,
    toUnit: DataUnit = DataUnit.AUTO,
    private val decimalPrecision: Int = 3,
    stairValue: Long = 1024
) {

    private var unit = toUnit.ext
    private var result: BigDecimal = BigDecimal.valueOf(size)

    init {
        require(fromUnit != DataUnit.AUTO) {
            //"fromUnit should not AUTO, AUTO only used in toUnit param"
            RequirementNotMeetException( null, DataUnit.AUTO, fromUnit,
                "fromUnit should not AUTO, AUTO only used in toUnit param"
            )
        }
        require(decimalPrecision >= 2 || decimalPrecision <= 20) {
            //"decimalPrecision should be in range 2 until 20"
            RequirementNotMeetException(null, "decimalPrecision in range 2 until 20", decimalPrecision)
        }

        if (toUnit == DataUnit.AUTO) {
            clcAuto(BigDecimal.valueOf(stairValue), fromUnit)
        } else {
            clc(BigDecimal.valueOf(stairValue), fromUnit, toUnit)
        }
    }

    fun getUnit(): String {
        return unit
    }

    fun getResult(): BigDecimal {
        return result
    }

    private fun clcAuto(stairValue: BigDecimal, fromUnit: DataUnit) {
        var n = 0
        do {
            val threshold = stairValue.pow(n)
            if (result == threshold) {
                result = result.divide(threshold)
                break
            } else if (result < threshold) {
                n = if (n != 0) {
                    --n
                } else {
                    n
                }
                result = result.divide(threshold.divide(stairValue))
                break
            } else {
                ++n
            }
        } while (true)

        unit = when (n) {
            0 -> ""
            1 -> "K"
            2 -> "M"
            3 -> "G"
            4 -> "T"
            5 -> "P"
            else -> throw IllegalStateException("Unknown unit, $result, $n")
        }.let {
            if (fromUnit.dif == 1) {
                it + "B"
            } else {
                it + "b"
            }
        }
    }

    override fun toString(): String {
        val str = result.toPlainString()
        // ensure capacity for internal buffer
        // 6 for dots or commas
        val builder = StringBuilder(str.length + 6 + unit.length).apply {
            append(str)
            // replace any '.' with ','
            do {
                val index = indexOf(".")
                replaceSafely(index, index + 1, ",")
            } while (index != -1)

            // index of ',' or current last index for max looping
            val max = indexOrNull(",")?.minus(1) ?: (this.lastIndex)
            var n = 0
            var loop = max
            do {
                n = if (n == 3) {
                    insert(loop + 1, '.')
                    0
                } else {
                    loop--
                    n + 1
                }
            } while (loop > -1)

            val length = max + 1
            // trim into specified length.
            // unit string doesn't count
            n = if (length <= str.lastIndex) {
                // if length less than result index, then trim.
                (length + decimalPrecision) + 1
            } else {
                // no trim at all, return current length
                length
            }

            // replace n index in builder
            replaceSafely(n, ++n, " ")
            replaceSafely(n, n + unit.length, unit)
            // set builder length
            // N-length + length of 'unit'
            setLength(n + unit.length)
        }
        return builder.toString()
    }

    private fun StringBuilder.indexOrNull(str: String): Int? {
        return indexOf(str).let { if (it != -1) it else null }
    }

    private fun clc(stairValue: BigDecimal, fromUnit: DataUnit, toUnit: DataUnit) {
        if (fromUnit.position != toUnit.position) {
            result = if (fromUnit.position > toUnit.position) {
                result.multiply(stairValue.pow(fromUnit.position))
            } else {
                result.divide(stairValue.pow(toUnit.position))
            }
        }
        if (fromUnit.dif != toUnit.dif) {
            result = if (toUnit.dif < fromUnit.dif) {
                result.divide(fromUnit.dif.toBigDecimal())
            } else {
                result.multiply(toUnit.dif.toBigDecimal())
            }
        }
    }
}