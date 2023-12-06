package `2023`.day6

import utils.Files
import java.math.BigInteger
import java.math.BigInteger.*

data class Race(val time: BigInteger, val distanceToBeat: BigInteger)

fun List<String>.parse(): List<Race> {
    val times = this[0].split("Time:[ ]+| +".toRegex()).filterNot { it.isEmpty() }
    val distances = this[1].split("Distance:[ ]+| +".toRegex()).filterNot { it.isEmpty() }
    return times.zip(distances).map { pair -> Race(pair.first.toBigInteger(), pair.second.toBigInteger()) }
}

fun List<String>.parsePart2(): List<Race> {
    val times = this[0].split("Time:".toRegex()).filterNot { it.isEmpty() }.map { it.replace(" ", "") }
    val distances = this[1].split("Distance:".toRegex()).filterNot { it.isEmpty() }.map { it.replace(" ", "") }
    return times.zip(distances).map { pair -> Race(pair.first.toBigInteger(), pair.second.toBigInteger()) }
}

tailrec fun findWinningCombinations(pressTime: BigInteger, race: Race, wins: BigInteger): BigInteger {
    return when {
        pressTime > race.time -> wins
        else -> {
            val traveledDistance = (race.time - pressTime) * pressTime
            if (traveledDistance > race.distanceToBeat)
                findWinningCombinations(pressTime+ ONE, race, wins+ ONE)
            else
                findWinningCombinations(pressTime+ ONE, race, wins)
        }
    }
}

class Day6 {
    fun solveFirst(filename: String): BigInteger {
        return Files.read("day6", filename, "2023").parse()
            .map { race -> findWinningCombinations(ZERO, race, ZERO) }
            .also(::println)
            .reduce{acc, i -> i * acc }
    }

    fun solveSecond(filename: String): BigInteger {
        return Files.read("day6", filename, "2023").parsePart2()
            .map { race -> findWinningCombinations(ZERO, race, ZERO) }
            .also(::println)
            .reduce{acc, i -> i * acc }
    }
}

fun main() {
    val day = Day6()

    println("first puzzle")
    println(day.solveFirst("input"))

    println("second puzzle")
    println(day.solveSecond("input"))
}
