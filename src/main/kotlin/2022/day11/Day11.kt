package day11

import utils.Files
import utils.list.component6
import java.math.BigInteger

fun List<String>.parse() =
        this.fold(emptyList<List<String>>()) { acc, s ->
            when {
                acc.isEmpty() -> acc + listOf(listOf(s))
                s.isNotBlank() -> acc.dropLast(1) + listOf(acc.last() + s)
                else -> acc + listOf(emptyList())
            }
        }.map {
            val (_, id) = it[0].split(" ", ":")
            val items = it[1].split( ":").last().split(",").map(String::trim).map(String::toBigInteger)
            val (_, _, _, _, operator, value) = it[2].split(":").last().split(" ")
            val (_, _, _, testDivisor) = it[3].split(":").last().split(" ")
            val (_, _, _, _, testTrue) = it[4].split(":").last().split(" ")
            val (_, _, _, _, testFalse) = it[5].split(":").last().split(" ")

            val operation = Operation(operator, value)
            val test = Test(testDivisor.toBigInteger(), testTrue.toInt(), testFalse.toInt())
            Monkey(id.toInt(), items, operation, test)
        }

data class Operation(val operator: String, val value: String)
fun Operation.calculateWorryLevel(old: BigInteger): BigInteger {
    val itemValue = when (value) {
        "old" -> old
        else -> value.toBigInteger()
    }

    return when(operator) {
        "+" -> old + itemValue
        "*" -> old * itemValue
        else -> error("missing operator type $operator")
    }
}

data class Test(val divisor: BigInteger, val ifTrue: Int, val ifFalse: Int)
fun Test.isDivisible(value: BigInteger): Boolean = value.mod(divisor) == BigInteger.ZERO

fun Test.whatMonkeyToThrowTo(value: BigInteger) = ifTrue.takeIf { isDivisible(value) } ?: ifFalse

data class Monkey(val id: Int, val items: List<BigInteger>, val operation: Operation, val test: Test, val noOfInspections: BigInteger = BigInteger.ZERO)

fun figureOutTheMonkeyBusiness(monkeys: List<Monkey>, iterations: Int, boredOfItem: (item: BigInteger) -> BigInteger): BigInteger {
    // for each monkey
    // inspect items
    // for each item calculate worry level
    // monkey get bored by item calculate new worry level
    // throw item to the monkey based on test if dividable by number
    // count the number of times each monkey inspects an item
    // do this n iterations
    // pick the two monkey with most inspections and multiply the inspection counts to get the answer
    return IntRange(1, iterations * monkeys.size).fold(monkeys) { acc, i ->

        val head = acc.first()
        val tail = acc.drop(1)

        val monkeyToItem = head.items
            .map { head.operation.calculateWorryLevel(it) }
            .map(boredOfItem)
            .map { v -> (head.test.whatMonkeyToThrowTo(v) to v) }

        val updatedTail = tail
            .map { monkey ->
                val item = monkeyToItem.filter { it.first == monkey.id }.map { it.second }
                if (item.isEmpty()) monkey
                else {
                    Monkey(
                        id = monkey.id,
                        items = monkey.items + item,
                        operation = monkey.operation,
                        test = monkey.test,
                        noOfInspections = monkey.noOfInspections
                    )
                }
            }

        val updatedHead = Monkey(
            head.id,
            emptyList(), // all items thrown to other monkeys
            head.operation,
            head.test,
            head.noOfInspections + BigInteger.valueOf(monkeyToItem.size.toLong())
        )

        updatedTail + updatedHead
    }
    // sort largest to smallest, pick the two biggest values and multiply together
    .map { it.noOfInspections }.sortedDescending().subList(0, 2).reduce { acc, i -> acc*i }
}

class Day11 {
    fun solveFirst(filename: String): BigInteger {
        val monkeys = Files.read("day11", filename).parse()

        fun getsBoredOfItem(value: BigInteger): BigInteger = value.div(BigInteger.valueOf(3))

        return figureOutTheMonkeyBusiness(monkeys, 20, ::getsBoredOfItem)

    }

    fun solveSecond(filename: String): BigInteger {
        val monkeys = Files.read("day11", filename).parse()

        val superModulo = monkeys.map { it.test.divisor }.reduce { acc, divisor -> acc * divisor }
        fun getsBoredOfItem(value: BigInteger): BigInteger = value % superModulo

        return figureOutTheMonkeyBusiness(monkeys, 10000, ::getsBoredOfItem)
    }
}

fun main() {
    val day = Day11()

    println("first puzzle")
    println(day.solveFirst("input"))

    println("second puzzle")
    println(day.solveSecond("input"))
}
