package day2

import utils.Files

sealed class Hand(val value: Long) {
    operator fun compareTo(elf: Hand): Int =
        when {
            this is Rock && elf is Paper -> -1
            this is Rock && elf is Scissor -> 1

            this is Paper && elf is Scissor -> -1
            this is Paper && elf is Rock -> 1

            this is Scissor && elf is Rock -> -1
            this is Scissor && elf is Paper -> 1

            this == elf -> 0

            else -> throw RuntimeException("couldn't compare")
        }
}

object Rock : Hand(1)
object Paper : Hand(2)
object Scissor : Hand(3)

private const val WIN = 6
private const val DRAW = 3
private const val LOST = 0

data class Round(val elf: Hand, val you: Hand) {

    fun score(): Long =
        when {
            you > elf -> you.value + WIN
            you < elf -> you.value + LOST
            you == elf -> you.value + DRAW
            else -> throw RuntimeException("should not happen!!!")
        }

    companion object {
        private fun createHandType(char: Char): Hand =
            when(char) {
                'A', 'X' -> Rock
                'B', 'Y' -> Paper
                'C', 'Z' -> Scissor
                else -> throw RuntimeException("character $char not found!")
            }

        fun create(string: String): Round {
            val hands = string.split(' ').map(String::first).map(::createHandType)
            return Round(hands.first(), hands.last())
        }

        fun createByEndStrategy(string: String): Round {
            val (elf, strategy) = string.split(' ').map(String::first)
            val elfHand = createHandType(elf)
            return Round(elfHand, createHandFromStrategy(elfHand, strategy))
        }

        private fun createHandFromStrategy(elf: Hand, strategy: Char): Hand {
            val rulesToWin = mapOf(Rock to Paper, Paper to Scissor, Scissor to Rock)
            val rulesToLoose = mapOf(Rock to Scissor, Paper to Rock, Scissor to Paper)

            val hand = when(strategy) {
                'X' -> rulesToLoose[elf]
                'Y' -> elf // draw
                'Z' -> rulesToWin[elf]
                else -> throw RuntimeException("not a valid strategy $strategy")
            }

            return hand!!
        }
    }
}

class Day2 {
    fun solveFirst(filename: String): Long {
        val rounds = Files.read("day2", filename).map(Round::create)
        return rounds.sumOf(Round::score)
    }

    fun solveSecond(filename: String): Long {
        val rounds = Files.read("day2", filename).map(Round::createByEndStrategy)
        return rounds.sumOf(Round::score)
    }
}

fun main() {
    val day2 = Day2()
    println(day2.solveFirst("input"))
    println(day2.solveSecond("input"))
}
