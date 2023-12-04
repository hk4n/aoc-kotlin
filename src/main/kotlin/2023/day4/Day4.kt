package `2023`.day4

import utils.Files

data class Scratchcard(val id: Int, val winningNumbers: List<Int>, val numbers: List<Int>)

fun List<String>.parse(): List<Scratchcard> =
    this.map { it.split("[:|\\|]".toRegex()) }
        .map{ card ->
            Scratchcard(
            id = card[0].split("Card[ ]+".toRegex()).last().toInt(),
            winningNumbers = card[1].split(" ",).filterNot { it == "" }.map { it.trim().toInt() },
            numbers = card[2].split(" ",).filterNot { it == "" }.map { it.trim().toInt() } )
        }

class Day4 {
    fun solveFirst(filename: String): Int {
        return Files.read("day4", filename, "2023").parse()
            .mapNotNull { card -> card.numbers.filter { card.winningNumbers.contains(it) }.ifEmpty { null } }
            .map { numbers -> numbers.drop(1).foldIndexed(1) { index, acc, _ -> acc * 2 } }
            .sum()
    }

    // acc is all original + won cards
    tailrec fun cardWinner(cardsToProcess: List<Scratchcard>, original: List<Scratchcard>, acc: List<Scratchcard>): List<Scratchcard> {
        // possible optimization is to cache all the winner cards in each scratchcard, that means that we only need to
        // find the winners once per scratchpad
       return when {
           cardsToProcess.isEmpty() -> {
               return original + acc
           }
        else -> {
            val nextCards = cardsToProcess
                .mapNotNull { card ->
                    val count = card.numbers.count { card.winningNumbers.contains(it) }
                    card.id.inc().rangeTo(count + card.id).toList().ifEmpty { null }
                }
                .flatten()
                .mapNotNull { id -> original.find { it.id == id } }

            cardWinner(nextCards, original, acc + nextCards)
        }
       }
    }

    fun solveSecond(filename: String): Int {
        val cards = Files.read("day4", filename, "2023").parse()
        return cardWinner(cards, cards, listOf()).count()
    }
}

fun main() {
    val day = Day4()

    println("first puzzle")
    println(day.solveFirst("input"))

    println("second puzzle")
    println(day.solveSecond("input"))
}
