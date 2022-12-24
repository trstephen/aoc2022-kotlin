package util

data class Grid<T: Any>(val elements: List<List<T>>) {

    val numRows = elements.size
    val numCols = elements.first().size

    fun row(idx: Int): List<T> = elements[idx]
    fun col(idx: Int): List<T> = elements.map { it[idx] }
    operator fun get(p: Point): T? =
        try { elements[p.y][p.x] } catch (_: IndexOutOfBoundsException) { null }

    /**
     * Returns the [Point] of the first occurrence of [value] starting at (0,0)
     * and searching left->right, top->bottom.
     */
    fun findFirstOrNull(value: T): Point? {
        elements.forEachIndexed { y, row ->
            val x = row.indexOf(value)
            if (x != -1) return Point(x, y)
        }
        return null
    }

    // I'll probably implement 'transpose' at some point~

    fun prettyPrint() = elements.prettyPrint()

    companion object {
        fun <T: Any> fromLines(lines: List<String>, toRow: (String) -> List<T>) = Grid(
            elements = lines.map { toRow(it) }
        )
    }
}

data class Point(val x: Int, val y: Int) {
    override fun toString() = "($x, $y)"
}

fun <T: Any> List<List<T>>.prettyPrint() {
    println(this.joinToString(separator = "\n") { line -> line.joinToString("\t") })
}
