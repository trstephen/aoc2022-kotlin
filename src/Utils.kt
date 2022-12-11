package util

data class Grid<T: Any>(val elements: List<List<T>>) {

    val numRows = elements.size
    val numCols = elements.first().size

    fun row(idx: Int): List<T> = elements[idx]
    fun col(idx: Int): List<T> = elements.map { it[idx] }

    // I'll probably implement 'transpose' at some point~

    fun prettyPrint() = elements.prettyPrint()

    companion object {
        fun <T: Any> fromLines(lines: List<String>, toElem: (String) -> List<T>) = Grid(
            elements = lines.map { toElem(it) }
        )
    }
}

fun <T: Any> List<List<T>>.prettyPrint() {
    println(this.joinToString(separator = "\n") { line -> line.joinToString("\t") })
}