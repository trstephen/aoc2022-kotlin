package day08

import java.io.File

object Day08 {

    @JvmStatic
    fun main(args: Array<String>) = part1()

    val input = File("src/day08/input.txt").readLines()

    fun part1() {
        val forest = input.toForest()
        // forest.treeHeights.prettyPrint()

        val reachable = forest.treeHeights.mapIndexed { rowIdx, row ->
            row.mapIndexed { colIdx, _ -> forest.canReachEdgeFrom(rowIdx, colIdx) }
        }
        // reachable.prettyPrint()

        val total = reachable.sumOf { it.count { it } }
        println(total) // 1812
    }

    data class Forest(val treeHeights: List<List<Int>>, val numRows: Int, val numCols: Int) {

        fun row(idx: Int): List<Int> = treeHeights[idx]
        fun col(idx: Int): List<Int> = treeHeights.map { it[idx] }

        fun canReachEdgeFrom(rowIdx: Int, colIdx: Int): Boolean {
            val h = treeHeights[rowIdx][colIdx]

            val leftHeights = row(rowIdx).take(colIdx)
            if (h.isTallerThan(leftHeights)) return true

            val rightHeights = row(rowIdx).takeLast(numCols - colIdx - 1)
            if (h.isTallerThan(rightHeights)) return true

            val upHeights = col(colIdx).take(rowIdx)
            if (h.isTallerThan(upHeights)) return true

            val downHeights = col(colIdx).takeLast(numRows - rowIdx - 1)
            if (h.isTallerThan(downHeights)) return true

            return false
        }

        private fun Int.isTallerThan(l: List<Int>): Boolean =
            if (l.isEmpty()) true
            else l.max() < this
    }

    fun List<String>.toForest(): Forest {
        val trees = this.map { line -> line.map { it.digitToInt() } }
        return Forest(
            treeHeights = trees,
            numRows = trees.size,
            numCols = trees.first().size,
        )
    }

    fun <T: Any> List<List<T>>.prettyPrint() {
        println(this.joinToString(separator = "\n") { line -> line.joinToString("\t") })
    }
}