package utils

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.data.forAll
import io.kotest.data.headers
import io.kotest.data.row
import io.kotest.data.table
import io.kotest.matchers.shouldBe
import java.math.RoundingMode
import java.text.DecimalFormat

class PositionTest: ShouldSpec({
    should("calculate the correct distance") {
        forAll(
            table(
                headers("pos a", "pos b", "expected distance"),
                row(Position(0,0), Position(1, 0), 1),
                row(Position(0,0), Position(0, 1), 1),
                row(Position(1,0), Position(0, 0), 1),
                row(Position(0,1), Position(0, 0), 1),
                row(Position(1,1), Position(0, 0), 1.4142135623731),
                row(Position(0,0), Position(1, 1), 1.4142135623731),
            )) { a, b, expectedDistance ->
            val decimal = DecimalFormat("#.#############")
            decimal.roundingMode = RoundingMode.UP
            decimal.format(a.distance(b)).toDouble() shouldBe expectedDistance

        }
    }

    should("get the correct heading") {
        forAll(
            table(
                headers("pos a", "pos b", "expected heading"),
                row(Position(0,0), Position(0,1), Heading.R),
                row(Position(0,0), Position(0,-1), Heading.L),
                row(Position(0,0), Position(1,0), Heading.D),
                row(Position(3,4), Position(1,4), Heading.U),
                row(Position(0,0), Position(-1,0), Heading.U),
                row(Position(4,0), Position(1,2), Heading.UR),
                row(Position(4,0), Position(5,2), Heading.DR),
                row(Position(4,0), Position(1,-2), Heading.UL),
                row(Position(4,0), Position(5,-2), Heading.DL),
            )) { a, b, expectedHeading ->
                a.heading(b) shouldBe expectedHeading

            }
    }
})