package `2023`.day2

import utils.Files

enum class CubeColor {
    red,
    green,
    blue
}
data class Bag(val red: Int, val green: Int, val blue: Int)
fun Bag.getAmount(color: CubeColor): Int =
    when(color) {
        CubeColor.blue -> blue
        CubeColor.green -> green
        CubeColor.red -> red
    }

data class Game(val id: Int, val hands: List<Hand>)
fun Game.isPossible(bag: Bag): Boolean =
    this.hands.filter { hand -> hand.cubes.all{ cube -> bag.getAmount(cube.color) >= cube.amount } }
        .size == hands.size

data class Cube(val color: CubeColor, val amount: Long)

data class Hand(val cubes: List<Cube>)
fun createHand(cubes: List<String>): Hand = Hand(cubes
    .map { it.trim().split(" ") }
    .map { Cube(CubeColor.valueOf(it.last().lowercase()), it.first().toLong()) }
)

class Day2 {
    fun List<String>.parse(): List<Game> =
       this.map { it.split(":") }
           .map { gameRow ->
               Game(
               gameRow.first().removePrefix("Game ").toInt(),
               gameRow.last().split(";")
                   .map { it.split(",") }
                   .map { createHand(it) }
           ) }

    fun solveFirst(filename: String): Int {
        val bag = Bag(12, 13, 14)
        return Files.read("day2", filename, "2023")
            .parse()
            .filter { it.isPossible(bag) }
            .sumOf { it.id }
    }

    fun solveSecond(filename: String): Long {
        return Files.read("day2", filename, "2023")
            .parse()
            .map { game ->
                // pick out all hands, sort its cubes by color and do max amount for each and multiply
                game.hands.flatMap { it.cubes }
                    .groupBy{ it.color }
                    .map { entry -> entry.value.maxBy { it.amount } }
                    .fold(1L){ acc, cube ->
                        acc * cube.amount
                    }
            }
            .sumOf { it }
    }
}

fun main() {
    val day = Day2()

    println("first puzzle")
    println(day.solveFirst("input"))

    println("second puzzle")
    println(day.solveSecond("input"))
}
