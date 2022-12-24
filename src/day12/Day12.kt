package day12

import util.Grid
import util.Point
import java.io.File

object Day12 {

    @JvmStatic
    fun main(args: Array<String>) = part1()

    val input = File("src/day12/input.txt").readLines()

    fun part1() {
        val grid = Grid.fromLines(input) { it.toList() }
        val start = grid.findFirstOrNull('S') ?: error("Couldn't find 'S'")
        val end = grid.findFirstOrNull('E') ?: error("Couldn't find 'E'")
        println("Navigating $start -> $end")
//        grid.prettyPrint()

        // BFS time!
        // This is gonna be a pain to extend to part 2, maybe just hack part1?
        val visited = mutableSetOf<Point>()
        val paths = ArrayDeque<List<Point>>()
        paths.add(listOf(start))
        while (paths.isNotEmpty()) {
            val path = paths.removeFirst()
            val head = path.last()

            if (head in visited) continue
            visited.add(head)

            if (head == end) {
//                println(path.joinToString(" -> "))
                println(path.size - 1) // 440
                return
            }

            grid.neighborsOf(head)
                .filter { grid.canClimb(from = head, to = it) }
                .forEach { paths.add(path + it) }
        }
        error("No path found???")
    }

    fun <T: Any> Grid<T>.neighborsOf(p: Point): List<Point> {
        if (this[p] == null) return emptyList()

        return listOf(0 to -1, 0 to 1, -1 to 0, 1 to 0)
            .map { (dx, dy) -> Point(p.x + dx, p.y + dy) }
            .filterNot { this[it] == null }
    }

    fun Grid<Char>.canClimb(from: Point, to: Point): Boolean {
        val fromV = this[from] ?: return false
        val toV = this[to] ?: return false

        return toV.height() <= fromV.height() + 1
    }

    fun Char.height(): Int =
        when (this) {
            'S' -> 1
            'E' -> 26
            else -> this.code - 'a'.code + 1
        }

    fun List<MutableList<Boolean>>.visit(p: Point) {
        this[p.y][p.x] = true
    }
}
