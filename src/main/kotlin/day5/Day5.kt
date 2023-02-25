package day5

import utils.Files
import utils.list.*

class Day5 {
    data class MoveOrder(val move: Int, val from: Int, val to: Int)
    data class Stack(var crates: List<String>)
    data class SupplyStacks(val stacks: List<Stack>, val moveOrders: List<MoveOrder>)

    private fun cleanCrate(crate: String):String {
        return if (crate.trim().isNotBlank())
            crate
                .replace("[", "")
                .replace("]", "")
                .trim()
                .filter { it.isLetter() }
        else ""
    }

    private fun parse(filename: String): SupplyStacks {
        val file = Files.read("day5", filename)
        val (rawStacks, rawMoves) = file.partition { s -> !s.startsWith("move") }

        val cleanedStacks = rawStacks
            .map {  it.chunked(4)  }
            .map { it.map(::cleanCrate) }
            .filter { it.isNotEmpty() }

        val stacks = cleanedStacks
            .transpose()
            .map { it.filter { s -> s.isNotBlank() } }
            .map(::Stack)

        val moves = rawMoves.map {
            val (_, move, _, from, _, to) = it.split(' ')
            MoveOrder(move.toInt(), from.toInt(), to.toInt())
        }

        return SupplyStacks(stacks, moves)
    }

    private fun moveCrates(stacks: List<Stack>, moveOrder: MoveOrder, multiMove: Boolean = false): List<Stack> {
        val noOfCratesToMove = moveOrder.move
        val fromIndex = moveOrder.from.dec()
        val toIndex = moveOrder.to.dec()

        val fromStack = stacks[fromIndex]
        val toStack = stacks[toIndex]

        val movedCrates = when (multiMove) {
            false -> fromStack.crates.take(noOfCratesToMove).reversed() // move one crate at a time
            true -> fromStack.crates.take(noOfCratesToMove) // move multiple crates at once
        }

        val fromStackUpdated = Stack(fromStack.crates.drop(noOfCratesToMove))
        val toStackUpdated = Stack(movedCrates + toStack.crates)

        return stacks.mapIndexed { index, stack ->
            when (index) {
                fromIndex -> fromStackUpdated
                toIndex -> toStackUpdated
                else -> stack
            }
        }
    }

    fun solveFirst(filename: String): String {
        val p = parse(filename)

        return p.moveOrders.fold(p.stacks) { acc, moveOrder ->
            moveCrates(acc, moveOrder)
        }.joinToString(postfix = "", prefix = "", separator = "") { it.crates.first() }
    }

    fun solveSecond(filename: String): String {
        val p = parse(filename)

        return p.moveOrders.fold(p.stacks) { acc, moveOrder ->
            moveCrates(acc, moveOrder, true)
        }.joinToString(postfix = "", prefix = "", separator = "") { it.crates.first() }
    }
}

fun main() {
    val day = Day5()

    println("first puzzle")
    println(day.solveFirst("input"))

    println("second puzzle")
    println(day.solveSecond("input"))
}
