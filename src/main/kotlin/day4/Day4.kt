package day4

import utils.Files

data class SpaceRange(val start: Long, val end: Long)

fun SpaceRange.inside(that: SpaceRange): Boolean =
    (that.start <= this.start && that.end >= this.end)

fun SpaceRange.overlap(that: SpaceRange): Boolean =
    (that.start <= this.start && that.end >= this.start) or
            (that.start <= this.end && that.end >= this.end)

class Day4 {

    fun parse(filename: String): List<Pair<SpaceRange, SpaceRange>> {
        return Files.read("day4", filename)
            .map { pair ->
                val x = pair.split(',')
                    .map { it.split('-') }
                    .map { SpaceRange(it.first().toLong(), it.last().toLong()) }
                (x.first() to x.last())
            }
    }

    fun solveFirst(filename: String): Long {
        // In how many assignment pairs does one range fully contain the other?
        return parse(filename)
            .count() { it.first.inside(it.second) or it.second.inside(it.first) }
            .toLong()
    }

    fun solveSecond(filename: String): Long {
        // In how many assignment pairs do the ranges overlap?
        return parse(filename)
            .count() { it.first.overlap(it.second) or it.second.overlap(it.first)}
            .toLong()
    }
}

fun main() {
    val day = Day4()

    println("first puzzle")
    println(day.solveFirst("input"))

    println("second puzzle")
    println(day.solveSecond("input"))
}
