package `2023`.day5

import utils.Files
import java.math.BigInteger
import java.math.BigInteger.ZERO

val categoryTypes = linkedSetOf(
    "seed-to-soil",
    "soil-to-fertilizer",
    "fertilizer-to-water",
    "water-to-light",
    "light-to-temperature",
    "temperature-to-humidity",
    "humidity-to-location"
)

data class CategoryProcessor(val type: String, val destination: BigInteger, val source: BigInteger, val range: BigInteger) {
    fun getSource(category: BigInteger): BigInteger? {
        if (category >= source && category <= source + range) {
            return destination + (category - source)
        }
        return null
    }
}

data class Almanac(val seeds: List<BigInteger>, val processors: List<CategoryProcessor>)

fun List<String>.parse(): Almanac {
    val seeds = this.first().split("seeds: | ".toRegex()).filterNot { it.isEmpty() }.map { it.toBigInteger() }
    val processors = this.drop(1).fold(listOf<CategoryProcessor>()) { acc, s ->
        when {
            s.isEmpty() -> acc
            s.toCharArray().first().isLetter() -> acc + CategoryProcessor(s.split(" ")[0].trim(), ZERO, ZERO, ZERO)
            s.toCharArray().first().isDigit() -> {
                val digits = s.split(" ").map { it.toBigInteger() }
                acc + CategoryProcessor(acc.last().type, digits[0], digits[1], digits[2])
            }
            else -> acc
        }
    }

    return Almanac(seeds, processors.filterNot { it.destination == ZERO && it.source == ZERO && it.range == ZERO })
}

class Day5 {
    fun solveFirst(filename: String): BigInteger? {
        val almanac = Files.read("day5", filename, "2023").parse()

        val groupedProcessors = almanac.processors.groupBy { it.type }

        return almanac.seeds.map { seed ->
            categoryTypes.fold(seed) { acc, categoryType ->
                groupedProcessors[categoryType]!!.mapNotNull { it.getSource(acc) }.ifEmpty { listOf(acc) }.first()
            }
        }.min()

    }

    fun solveSecond(filename: String): BigInteger {
        val almanac = Files.read("day5", filename, "2023").parse()

        val groupedProcessors = almanac.processors.groupBy { it.type }
        return almanac.seeds.windowed(2, 2)
            .flatMap { seedRange ->
                generateSequence(seedRange.first()) { it + BigInteger.ONE }
                .takeWhile { it < seedRange.first() + seedRange.last() }
                .map { seed ->
                    categoryTypes.fold(seed) { acc, categoryType ->
                        groupedProcessors[categoryType]!!.mapNotNull { it.getSource(acc) }.ifEmpty { listOf(acc) }
                            .first()
                    }
                }
            }
            .min()
    }
}

fun main() {
    val day = Day5()

    println("first puzzle")
    println(day.solveFirst("input"))

    //println("second puzzle")
    //println(day.solveSecond("input"))
}
