package day6

import utils.Files

data class Datastream(val startIndex: Long, val startHeader: Array<Char>)

class Day6 {

    fun findStartIndex(datastream: String, headerLength: Int = 4): Datastream =
        datastream.toCharArray()
            .fold(Datastream(0, emptyArray())) { acc, c ->
                when {
                    acc.startHeader.size == headerLength -> acc
                    acc.startHeader.contains(c) -> Datastream(
                        acc.startIndex.inc(),
                        acc.startHeader.drop(acc.startHeader.indexOf(c) + 1).toTypedArray() + c)

                    else -> Datastream(acc.startIndex.inc(), acc.startHeader + c)
                }
            }

    fun solveFirst(filename: String): Long {
        return findStartIndex(Files.read("day6", filename).first()).startIndex
    }

    fun solveSecond(filename: String): Long {
        return findStartIndex(Files.read("day6", filename).first(), headerLength = 14).startIndex
    }
}

fun main() {
    val day = Day6()

    println("first puzzle")
    println(day.solveFirst("input"))

    println("second puzzle")
    println(day.solveSecond("input"))
}
