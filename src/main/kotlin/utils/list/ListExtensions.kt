package utils.list

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
