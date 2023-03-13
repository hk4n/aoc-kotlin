package day9

import utils.Files
import utils.Heading
import utils.Position

val yDirections = listOf("L", "R")
val xDirections = listOf("U", "D")

data class Move(val direction: String, val steps: Int)

fun Move.getSteps() =
    when(direction) {
        "L", "U" -> -steps
        "R", "D" -> steps
        else -> throw RuntimeException("direction error!!!!")
    }

fun List<Move>.maxStep() =
    this.fold(listOf(1)) { acc, move ->
        when (move.direction){
            "R", "U" -> acc + (acc.last() + move.steps)
            "L", "D" -> acc + (acc.last() - move.steps)
            else -> throw RuntimeException("nooooooooooooooooooooooooooo!!!")
        }
    }.max()

data class KnotPosition(override val x: Int, override val y: Int, val visited: Boolean = false): Position(x, y) {
    override fun toString(): String {
        return if (visited) "#($x, $y)" else ".($x, $y)"
    }

    override fun equals(other: Any?): Boolean {
        return super.equals(other)
    }

    fun getPositionByDirection(move: Move): Int =
        when(move.direction) {
            in xDirections -> x
            in yDirections -> y
            else -> throw RuntimeException("no direction found for move: $move")
        }

    fun move(position: Int, move: Move) =
        when(move.direction) {
            in xDirections -> KnotPosition(position, y)
            in yDirections -> KnotPosition(x, position)
            else -> throw RuntimeException("no direction found for move: $move")
        }

    fun visit() = KnotPosition(x, y, visited = true)
}

data class Knot(val knotNumber: Int, val position: KnotPosition) {
    override fun toString(): String =
        when (knotNumber) {
            0 -> "."
            99 -> "H"
            999 -> "s"
            else -> "$knotNumber"
        }

    override fun equals(other: Any?): Boolean = when(other) {
        is Knot -> position == other.position
        is Position -> position.equals(other)
        else -> throw RuntimeException("not correct type $other")
    }

    fun isLast(): Boolean = knotNumber == 9
    fun isHead(): Boolean = knotNumber == 0
}

fun calculateRangeOfHeads(knot: Knot, move: Move): List<Knot> {
    val startPosition = knot.position
    val start = startPosition.getPositionByDirection(move)
    val end = start + move.getSteps()

    return (IntRange(start, end).takeIf { start < end } ?: IntRange(end, start).reversed())
        .map { Knot(0, startPosition.move(it, move)) }
}

fun Knot.closeEnoughTo(head: Knot) = head.position.distance(this.position) <= 1.5

fun moveKnotTowardsHead(head: Knot, current: Knot): Knot =
    current.takeIf { it.closeEnoughTo(head) } ?: run {
        val heading = current.position.heading(head.position)

        val newPosition = when(heading) {
            Heading.UR -> KnotPosition(current.position.x - 1, current.position.y + 1 )
            Heading.UL -> KnotPosition(current.position.x - 1, current.position.y - 1 )
            Heading.DR -> KnotPosition(current.position.x + 1, current.position.y + 1 )
            Heading.DL -> KnotPosition(current.position.x + 1, current.position.y - 1 )
            Heading.U -> KnotPosition(current.position.x - 1, current.position.y )
            Heading.D -> KnotPosition(current.position.x + 1, current.position.y )
            Heading.R -> KnotPosition(current.position.x, current.position.y + 1)
            Heading.L -> KnotPosition(current.position.x, current.position.y - 1)
        }

        Knot(current.knotNumber, newPosition)
    }

data class Accumulator(val head: Knot, val tail: List<Knot>, val visitedTails: List<Knot>)

class Day9 {
    fun solveFirst(filename: String) {
        val moves = Files.read("day9", filename)
            .map { it.split(" ") }
            .map { Move(it.first(), it.last().toInt()) }

        val (xMoves, _) = moves.partition { xDirections.contains(it.direction) }

        val x = xMoves.maxStep() - 1

        val startPos = Knot(0, KnotPosition(x, y = 0, visited = true))

        val visitedTails = moves.fold(Accumulator(startPos, listOf(startPos), emptyList())) { acc, move ->

            val heads = calculateRangeOfHeads(acc.head, move)

            val visited = heads.fold(acc.tail) { tails, head ->
                val movedKnot = moveKnotTowardsHead(head, tails.last())
                tails.takeIf { it.last().position == movedKnot.position } ?: (tails + movedKnot)

            }.map { Knot(it.knotNumber, it.position.visit()) }

            Accumulator(heads.last(), listOf(visited.last()), acc.visitedTails + visited)
        }.visitedTails
            .distinct()

        println("visited tails $visitedTails")
        println("no of visited position ${visitedTails.size}")
    }

    fun solveSecond(filename: String) {
            val moves = Files.read("day9", filename)
            .map { it.split(" ") }
            .map { Move(it.first(), it.last().toInt()) }

        val (xMoves, yMoves) = moves.partition { xDirections.contains(it.direction) }

        val x = xMoves.maxStep()
        val y = yMoves.maxStep()

        val headKnot = Knot(0, KnotPosition(x, y, visited = true))
        val tailKnots = IntRange(1, 9).map { Knot(it, headKnot.position) }

        val visitedTails = moves.fold(Accumulator(headKnot, tailKnots, emptyList())) { acc, move ->

            val headRange = calculateRangeOfHeads(acc.head, move)

            data class TailKnotsVisited(val knots: List<Knot>, val visitedEndKnots: List<Knot>)

            val updatedKnots = headRange.fold(TailKnotsVisited(acc.tail, emptyList())) { tail, head ->
                val startKnots = TailKnotsVisited(listOf(head), tail.visitedEndKnots)

                tail.knots.fold(startKnots) { accTail, currentKnot ->

                    val movedKnot = moveKnotTowardsHead(accTail.knots.last(), currentKnot)

                    val visitedEndKnots = accTail.visitedEndKnots.takeUnless { movedKnot.isLast() }
                        ?: (accTail.visitedEndKnots + movedKnot)

                    val newTailKnots = accTail.knots.filterNot { accTail.knots.last().isHead() } + movedKnot

                    TailKnotsVisited(newTailKnots, visitedEndKnots)
                }
            }

            val visited = updatedKnots.visitedEndKnots
                .filter { it.isLast() }
                .map { Knot(it.knotNumber, it.position.visit()) }

            Accumulator(headRange.last(), updatedKnots.knots, acc.visitedTails + visited)
        }.visitedTails
            .distinct()

        println("visited tails $visitedTails")
        println("no of visited position ${visitedTails.size}")
    }

}

fun main() {
    val day = Day9()

    println("first puzzle")
    println(day.solveFirst("input"))

    println("second puzzle")
    println(day.solveSecond("input"))
}
