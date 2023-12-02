package day3

import utils.Files

data class Rucksack(val compartment1: CharArray, val compartment2: CharArray)
data class Group(val elf1: CharArray, val elf2: CharArray, val elf3: CharArray)

class Day3 {
    val itemPriorities = CharRange('a', 'z').withIndex().associate { it.value to it.index + 1 } +
            CharRange('A', 'Z').withIndex().associate { it.value to it.index + 27 }


    fun solveFirst(filename: String): Long {
        return Files.read("day3", filename)
            .map { it.chunked(it.length.div(2)) }
            .map { Rucksack(it.first().toCharArray(), it.last().toCharArray())}
            .flatMap { rucksack ->
                rucksack.compartment1.filter { item -> rucksack.compartment2.contains(item) }.distinct() }
            .map { itemPriorities[it] }
            .sumOf { it?.toLong() ?: throw RuntimeException("this should not happen!!!") }
    }

    fun solveSecond(filename: String): Long {
        return Files.read("day3", filename)
            .chunked(3)
            .map { Group(it[0].toCharArray(), it[1].toCharArray(), it[2].toCharArray()) }
            .flatMap{ it.elf1.filter { c -> it.elf2.contains(c) }.distinct()
                          .filter { c -> it.elf3.contains(c) }.distinct() }
            .map { itemPriorities[it] }
            .sumOf { it?.toLong() ?: throw RuntimeException("this should not happen!!!") }
    }

}

fun main() {
    val day3 = Day3()

    println("first puzzle")
    println(day3.solveFirst("input"))

    println("second puzzle")
    println(day3.solveSecond("input"))
}
