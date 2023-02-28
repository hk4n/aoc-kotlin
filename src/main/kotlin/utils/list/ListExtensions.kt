package utils.list

typealias Matrix2d<T> = List<List<T>>

operator fun <T> List<T>.component6(): T {
    return get(5)
}

inline fun <reified T> List<List<T>>.transpose(): List<List<T>> {
    val columns = this.first().size
    val rows = this.size

    return Array(columns) { column ->
        Array(rows) { row ->
            this[row][column]
        }.toList()
    }.toList()
}

inline fun <reified T> Matrix2d<T>.combineWith(that: Matrix2d<T>,
                                               combiner: (row: Int, column: Int, a: Matrix2d<T>, b: Matrix2d<T>) -> T): List<List<T>> {
    return when {
        this.isEmpty() and that.isNotEmpty() -> that
        that.isEmpty() and this.isNotEmpty() -> this
        this.isEmpty() and that.isEmpty() -> this
        else -> {
            val columns = this.first().size
            val rows = this.size

            Array(rows) { row ->
                Array(columns) { column ->
                    combiner(row, column, this ,that)
                }.toList()
            }.toList()
        }
    }
}

