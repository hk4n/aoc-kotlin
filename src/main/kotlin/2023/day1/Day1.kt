package `2023`.day1

import utils.Files

class Day1 {
    private val letterNumberPairs = listOf(
        Pair("one", '1'),
        Pair("two", '2'),
        Pair("three", '3'),
        Pair("four", '4'),
        Pair("five", '5'),
        Pair("six", '6'),
        Pair("seven", '7'),
        Pair("eight", '8'),
        Pair("nine", '9'),
        Pair("zero", '0'))

    fun replaceLettersRollingWindow(calibration: String, acc: List<Char> = listOf<Char>(), position: Int = 0): String {
        if (position == calibration.length)
            return acc.joinToString(separator = "")

        val positionNumber = letterNumberPairs.filter { calibration.startsWith(it.first, position) }
            .map { Pair(position, it.second )}
            .firstOrNull()

        return if (positionNumber != null) {
            replaceLettersRollingWindow(calibration, acc + positionNumber.second, position + 1)
        } else {
            replaceLettersRollingWindow(calibration, acc + calibration[position], position + 1)
        }
    }

    fun solveFirst(filename: String): Int =
        Files.read("day1", filename, "2023")
            .map { s -> s.chars().toArray() }
            .map { chars -> chars.filter { c -> c in 48..57 }.map { it.toChar() } }
            .map { chars -> "${chars.first()}${chars.last()}".toInt() }
            .sumOf { it }

    fun solveSecond(filename: String): Int =
        Files.read("day1", filename, "2023")
            .asSequence()
            .map { replaceLettersRollingWindow(it).chars().toArray() }
            .map { chars -> chars.filter { c -> c in 48..57 }.map { it.toChar() } }
            .map { chars -> "${chars.first()}${chars.last()}".toInt() }
            .sumOf { it }
}

fun main() {
    val day = Day1()

    println("first puzzle")
    println(day.solveFirst("input"))

    println("second puzzle")
    println(day.solveSecond("input"))
}
