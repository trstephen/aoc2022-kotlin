package day08

import util.Grid
import util.prettyPrint
import java.io.File

typealias ForestGrid = Grid<Int>

object Day08 {

    @JvmStatic
    fun main(args: Array<String>) = part2()

    val input = File("src/day08/input.txt").readLines()

    fun part1() {
        val forestGrid = Grid.fromLines(input) { line -> line.map { it.digitToInt() } }
        // forestGrid.prettyPrint()

        val reachable = forestGrid.elements.mapIndexed { rowIdx, row ->
            List(row.size) { colIdx -> forestGrid.canReachEdgeFrom(rowIdx, colIdx) }
        }
        // reachable.prettyPrint()

        val total = reachable.sumOf { it.count { it } }
        println(total) // 1812
    }

    fun ForestGrid.canReachEdgeFrom(rowIdx: Int, colIdx: Int): Boolean {
        val h = elements[rowIdx][colIdx]

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

    fun part2() {
        val forestGrid = Grid.fromLines(input) { line -> line.map { it.digitToInt() } }
        // forestGrid.prettyPrint()

        // println()

        val scenicScores = forestGrid.elements.mapIndexed { rowIdx, row ->
            List(row.size) { colIdx -> forestGrid.scenicScoreFor(rowIdx, colIdx) }
        }
        // scenicScores.prettyPrint()

        println(scenicScores.flatten().max()) // 315495
    }

    fun ForestGrid.scenicScoreFor(rowIdx: Int, colIdx: Int): Int {
        val h = elements[rowIdx][colIdx]

        val viewLeft = row(rowIdx).take(colIdx).reversed()
        val scenicLeft = viewLeft.scenicScoreAt(h)

        val viewRight = row(rowIdx).takeLast(numCols - colIdx - 1)
        val scenicRight = viewRight.scenicScoreAt(h)

        val viewUp = col(colIdx).take(rowIdx).reversed()
        val scenicUp = viewUp.scenicScoreAt(h)

        val viewDown = col(colIdx).takeLast(numRows - rowIdx - 1)
        val scenicDown = viewDown.scenicScoreAt(h)

        return listOf(
            scenicLeft, scenicRight, scenicUp, scenicDown
        ).map { it.coerceAtLeast(1) }.reduce { a, b -> a * b }
    }

    private fun List<Int>.scenicScoreAt(height: Int): Int {
        val treesLessThanHeight = this.takeWhile { it < height }.size

        // Did we stop because we ran out of trees or hit one of the same height?
        // lmao my fanciness failed.
        return if (treesLessThanHeight == this.size) treesLessThanHeight else treesLessThanHeight + 1
    }
}