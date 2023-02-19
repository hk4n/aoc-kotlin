package day1

import utils.Files

data class Elf(val food: Long)

class Day1 {
    private fun groupByElf(filename: String) =
        Files.read("day1", filename)
            .fold(listOf<Elf>()) { acc, s: String ->
                when {
                    acc.isEmpty() -> listOf(Elf(s.toLong()))
                    s.isBlank() -> acc + listOf(Elf(0))
                    else -> {
                        val elf = acc.last()
                        acc.dropLast(1) + Elf(elf.food + s.toLong())
                    }
                }
            }


    fun solveFirst(filename: String): Long {
            return groupByElf(filename)
                .maxBy { it.food }
            .food
    }

    fun solveSecond(filename: String): Long {
        return groupByElf(filename)
            .sortedByDescending { it.food }
            .take(3)
            .sumOf { it.food }
    }

}

fun main(args: Array<String>) {
    val day1 = Day1()

    println("first puzzle")
    println(day1.solveFirst("input"))

    println("second puzzle")
    println(day1.solveSecond("input"))
}
