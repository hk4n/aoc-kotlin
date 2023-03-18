package day10

import utils.Files

typealias Sprite = Int
sealed class Instruction(val cycles: Int)
data class Addx(val value: Int) : Instruction(2)
class Noop : Instruction(1)

fun shouldCalculateSignalStrength(cycles: Int) =
    when(cycles) {
        20, 60, 100, 140, 180, 220 -> true
        else -> false
    }

data class SignalStrength(val cycle: Int, val signalStrength: Int)
fun calcSignalStrength(cycles: Int, value: Int): SignalStrength = SignalStrength(cycles, cycles.times(value))

data class InstructionCycles(val cycle: Int, val xRegister: Int, val signalStrengths: List<SignalStrength>)
fun InstructionCycles.addCycles(cycles: Int, value: Int): InstructionCycles =
    IntRange(1, cycles).fold(this) { acc, i ->
        val newCycle = acc.cycle + 1

        val newValue = when(i) {
            cycles -> acc.xRegister + value
            else -> acc.xRegister
        }

        val newSignalStrengths = acc.signalStrengths.takeUnless { shouldCalculateSignalStrength(newCycle) }
            ?: (acc.signalStrengths + calcSignalStrength(newCycle, acc.xRegister))

        InstructionCycles(newCycle, newValue, newSignalStrengths)
    }

fun Sprite.hit(cycle: Int): Boolean = this == cycle || this+1 == cycle || this-1 == cycle

fun List<String>.updatePixel(cycle: Int, pixel: String): List<String> =
    this.mapIndexed { index, s ->
        when(index) {
            cycle -> pixel
            else -> s
        }
    }

data class CrtDisplay(val cycle: Int, val sprite: Sprite, val display: List<List<String>> = emptyList())
fun CrtDisplay.updateDisplay(newSpritePos: Sprite): CrtDisplay {
    val updatedDisplay: List<List<String>> = display.mapIndexed { index, row ->
        val max = (index + 1) * 40
        val min = max - 40
        if (cycle in min..max) {
            val normalizedCycle = cycle - min

            when {
                sprite.hit(normalizedCycle) -> row.updatePixel(normalizedCycle, "#")
                else -> row.updatePixel(normalizedCycle, ".")
            }
        }
        else row
    }

    return CrtDisplay(cycle + 1, newSpritePos, updatedDisplay)
}

fun initDisplay(): List<List<String>> {
    return Array(6) {
        Array(40) { "_" }.toList()
    }.toList()
}

fun List<String>.parse(): List<Instruction> =
    this.map { it.split(" ") }
        .map {
            when(it.first()) {
                "addx" -> Addx(it.last().toInt())
                "noop" -> Noop()
                else -> error("should not happen!!")
            }
        }

class Day10 {
    fun solveFirst(filename: String) {
        val instructions = Files.read("day10", filename).parse()

        val instructionCycles = instructions.fold(InstructionCycles(0, 1, emptyList())) {
                acc, instruction ->
            when(instruction) {
                is Noop -> acc.addCycles(1, 0)
                is Addx -> acc.addCycles(2, instruction.value)
            }
        }

        println(instructionCycles.signalStrengths)
        println(instructionCycles.signalStrengths.sumOf(SignalStrength::signalStrength))
    }

    fun solveSecond(filename: String) {
        val instructions = Files.read("day10", filename).parse()
        val crtDisplay = instructions.fold(CrtDisplay(0, 1, initDisplay())) {
                acc, instruction ->

            val sprite = acc.sprite + when(instruction) {
                is Noop -> 0
                is Addx -> instruction.value
            }

            when(instruction) {
                is Noop -> acc.updateDisplay(sprite)
                is Addx -> acc.updateDisplay(acc.sprite).updateDisplay(sprite)
            }
        }

        println("1234567890123456789012345678901234567890")
        crtDisplay.display
            .map { it.joinToString("","","") }
            .forEach(::println)

    }
}

fun main() {
    val day = Day10()

    println("first puzzle")
    day.solveFirst("input")

    println("second puzzle")
    day.solveSecond("input")
}
