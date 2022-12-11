package day08

import java.io.File

object Day08 {

    @JvmStatic
    fun main(args: Array<String>) = part2()

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

    fun part2() {
        val forest = input.toForest()
        // forest.treeHeights.prettyPrint()

        // println()

        val scenicScores = forest.treeHeights.mapIndexed { rowIdx, row ->
            row.mapIndexed { colIdx, _ -> forest.scenicScoreFor(rowIdx, colIdx) }
        }
        // scenicScores.prettyPrint()

        println(scenicScores.flatten().max()) // 315495
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

        fun scenicScoreFor(rowIdx: Int, colIdx: Int): Int {
            val h = treeHeights[rowIdx][colIdx]

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

        private fun List<Int>.takeScenicAt(height: Int): Set<Int> {
            val scenic = takeWhile { it <= height }.toSet() // 'toSet' deals with repeats where it == height

            // so we don't obliterate the product in 'reduce'
            return scenic.ifEmpty { setOf(1) }
        }
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