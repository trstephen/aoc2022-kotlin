package util

import kotlin.math.abs
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

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

    /**
     * The vector from `this` to [other], treating `this` as the origin.
     */
    fun vecTo(other: Point) = Point(
        x = x - other.x,
        y = y - other.y,
    )

    /**
     * Distance from the origin to this [Point] measured by
     * Manhattan distance.
     */
    val manhattanLength: Int
        get() = abs(x) + abs(y)
}

fun <T: Any> List<List<T>>.prettyPrint() {
    println(this.joinToString(separator = "\n") { line -> line.joinToString("\t") })
}

fun Runtime.usedMemory(): Long = totalMemory() - freeMemory()

/**
 * Print with commas separating digits in groups of 3
 * e.g. 1234567 -> 1,234,567
 */
fun Number.prettyPrint(): String = "%,d".format(this)

@OptIn(ExperimentalTime::class)
fun measurePerf(block: () -> Unit) {
    val runtime = Runtime.getRuntime()
    val memBefore = runtime.usedMemory()

    val t = measureTime(block)
    val memAfter = runtime.usedMemory()
    val usedKb = (memAfter - memBefore) / 1024

    println()
    println("== Perf report")
    println("Runtime: ${t.inWholeMilliseconds.prettyPrint()} ms")
    println("Memory : ${usedKb.prettyPrint()} kB")

}
