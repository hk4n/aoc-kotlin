package utils

import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

enum class Heading {
    U,
    D,
    R,
    L,
    UR,
    UL,
    DR,
    DL
}

open class Position( open val x: Int, open val y: Int) {
    fun distance(that: Position): Double =
        sqrt((abs( x - that.x).toDouble().pow(2)) + (abs(y - that.y).toDouble().pow(2)) )

    fun heading(that: Position) : Heading = when {
        x == that.x && y < that.y -> Heading.R
        x == that.x && y > that.y -> Heading.L

        y == that.y && x > that.x -> Heading.U
        y == that.y && x < that.x -> Heading.D

        y < that.y && x > that.x -> Heading.UR
        y > that.y && x > that.x -> Heading.UL

        y < that.y && x < that.x -> Heading.DR
        y > that.y && x < that.x -> Heading.DL

        else -> throw RuntimeException("could not get the correct heading! $this to $that")
    }

    override fun equals(other: Any?): Boolean = when(other) {
            is Position -> (other.x == x) && (other.y == y)
            else -> false
        }

    override fun toString(): String = "$x, $y"

}