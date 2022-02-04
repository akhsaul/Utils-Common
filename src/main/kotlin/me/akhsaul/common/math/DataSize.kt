package me.akhsaul.common.math

import me.akhsaul.common.enum.DataUnit
import java.math.BigDecimal

@Suppress("all")
class DataSize @JvmOverloads constructor(
    size: BigDecimal,
    fromUnit: DataUnit = DataUnit.BYTES,
    toUnit: DataUnit = DataUnit.AUTO,
    private val decimalPrecision: Int = 3,
    stairValue: BigDecimal = BigDecimal(1024)
) {

    @JvmOverloads constructor(
        size: Long,
        fromUnit: DataUnit = DataUnit.BYTES,
        toUnit: DataUnit = DataUnit.AUTO,
        decimalPrecision: Int = 3,
        stairValue: BigDecimal = BigDecimal(1024)
    ) : this(
        BigDecimal.valueOf(size),
        fromUnit,
        toUnit,
        decimalPrecision,
        stairValue
    )

    private var unit = toUnit.ext
    private var result: BigDecimal = BigDecimal(-1)

    init {
        require(fromUnit != DataUnit.AUTO) {
            "fromUnit should not AUTO, AUTO only used in toUnit param"
        }
        require(decimalPrecision >= 2 || decimalPrecision <= 20) {
            "decimalPrecision should be in range 2 until 20"
        }
        if (toUnit == DataUnit.AUTO) {
            var n = 0
            result = size
            do {
                val threshold = stairValue.pow(n)
                if (result == threshold) {
                    result = result.divide(threshold)
                    break
                } else if (result < threshold) {
                    n = if (n != 0) --n else n
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
                else -> throw IllegalStateException("Unknown unit, $size, $n")
            }.let {
                if (fromUnit.dif == 1) {
                    it + "B"
                } else {
                    it + "b"
                }
            }
        } else {
            size.clc(stairValue, fromUnit, toUnit)
        }
    }

    fun getUnit(): String {
        return unit
    }

    fun getResult(): BigDecimal {
        return result
    }

    override fun toString(): String {
        val str = result.toPlainString().replace('.', ',').normalize(decimalPrecision)
        return "$str $unit"
    }

    private fun String.normalize(precision: Int): String {
        val looper = this.findAnyOf(listOf(","))?.first?.minus(1) ?: (this.length - 1)
        val builder = StringBuilder()
        var loop = looper
        var n = 0
        do {
            val c = this[loop]
            if (n == 3) {
                builder.append('.')
                n = 0
            } else {
                builder.append(c)
                n++
                loop--
            }
        } while (loop > -1)

        builder.reverse()

        loop = looper + 1
        n = (loop + 1) + precision
        for (i in loop until n) {
            runCatching {
                builder.append(this[i])
            }.onFailure {
                // ignore
            }
        }
        return builder.toString()
    }

    private fun BigDecimal.clc(stairValue: BigDecimal, fromUnit: DataUnit, toUnit: DataUnit) {
        result = if (fromUnit.position != toUnit.position) {
            if (fromUnit.position > toUnit.position) {
                this.multiply(stairValue.pow(fromUnit.position))
            } else {
                this.divide(stairValue.pow(toUnit.position))
            }
        } else {
            this
        }.let {
            if (fromUnit.dif != toUnit.dif) {
                if (toUnit.dif < fromUnit.dif) {
                    it.divide(fromUnit.dif.toBigDecimal())
                } else {
                    it.multiply(toUnit.dif.toBigDecimal())
                }
            } else {
                it
            }
        }
    }
}