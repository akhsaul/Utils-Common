package me.akhsaul.common.math

object Statistic {
    fun mean(value: List<Long>, frequency: List<Long>): Float {
        assert(value.isNotEmpty() and frequency.isNotEmpty()) { throw IllegalArgumentException("data must not be empty") }
        assert(value.size == frequency.size) { throw IllegalArgumentException("must be same size") }
        var fx = 0f
        repeat(value.size) {
            fx += value[it] * frequency[it]
        }
        return fx.div(frequency.sum())
    }

    fun median(value: List<Long>, frequency: List<Long>): Long {
        assert(value.isNotEmpty() and frequency.isNotEmpty()) { throw IllegalArgumentException("data must not be empty") }
        assert(value.size == frequency.size) { throw IllegalArgumentException("must be same size") }
        val result = reSortedAndreIndex(value.kali(frequency))
        val data = result[0].kali(result[1])
        val sum = frequency.sum()
        assert(data.size.toLong() == sum) { throw IllegalStateException("must be same size") }
        val n = sum.div(2).toInt()
        return if (sum.mod(2) == 1) {
            // ganjil
            data[n]
        } else {
            // genap
            (data[n] + data[n + 1]).div(2)
        }
    }

    private fun List<Long>.kali(frequency: List<Long>): List<Long> {
        val data = arrayListOf<Long>()
        repeat(size) {
            repeat(frequency[it].toInt()) { _ ->
                data.add(get(it))
            }
        }
        return data
    }

    private fun reSortedAndreIndex(value: List<Long>): List<List<Long>> {
        @Suppress("NAME_SHADOWING")
        val value = value.sorted()
        val data = arrayOfNulls<Long>(value.size)
        val frequency = arrayOfNulls<Long>(value.size)
        var i = 0
        repeat(value.size) {
            if (!data.contains(value[it])) {
                data[i] = value[it]
            }
            frequency[i] = frequency[i]?.plus(1) ?: 1
            try {
                if (!data.contains(value[it + 1])) {
                    i += 1
                }
            } catch (e: Exception) {
                i += 1
            }
        }
        return listOf(data.filterNotNull(), frequency.filterNotNull())
    }

    fun modus(value: List<Long>): Long {
        val result = reSortedAndreIndex(value)
        val frequency = result[1]
        val max = frequency.maxOrNull()!!
        return result[0][frequency.indexOf(max)]
    }
}