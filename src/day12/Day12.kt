package day12

import util.Grid
import util.Point
import java.io.File

object Day12 {

    @JvmStatic
    fun main(args: Array<String>) = part2()

    val input = File("src/day12/input.txt").readLines()

    val grid = Grid.fromLines(input) { it.toList() }

    fun part1() {
        val steps = bfs(
            start = grid.findFirstOrNull('S') ?: error("Couldn't find 'S'"),
            end = 'E',
            via = canClimb,
        )
        println(steps) // 440
    }

    fun part2() {
        // Let's see if we can reverse this by searching E -> first 'a'
        // instead of finding all 'a' and comparing lengths.
        val steps = bfs(
            start = grid.findFirstOrNull('E') ?: error("Couldn't find 'E"),
            end = 'a',
            via = canDescend,
        )
        println(steps) // 439
    }

    fun bfs(
        start: Point,
        end: Char,
        via: Grid<Char>.(Point, Point) -> Boolean,
    ): Int? {
        val visited = mutableSetOf<Point>()
        val paths = ArrayDeque<List<Point>>()
        paths.add(listOf(start))

        while (paths.isNotEmpty()) {
            val path = paths.removeFirst()
            val head = path.last()

            if (head in visited) continue
            visited.add(head)

            if (grid[head] == end) return path.size - 1

            grid.neighborsOf(head)
                .filter { grid.via(head, it) }
                .forEach { paths.add(path + it) }
        }

        return null
    }

    fun <T: Any> Grid<T>.neighborsOf(p: Point): List<Point> {
        if (this[p] == null) return emptyList()

        return listOf(0 to -1, 0 to 1, -1 to 0, 1 to 0)
            .map { (dx, dy) -> Point(p.x + dx, p.y + dy) }
            .filterNot { this[it] == null }
    }

    val canClimb = fun Grid<Char>.(from: Point, to: Point): Boolean {
        val fromV = this[from] ?: return false
        val toV = this[to] ?: return false

        return toV.height() <= fromV.height() + 1
    }

    val canDescend = fun Grid<Char>.(from: Point, to: Point): Boolean {
        val fromV = this[from] ?: return false
        val toV = this[to] ?: return false

        return toV.height() + 1 >= fromV.height()
    }

    fun Char.height(): Int =
        when (this) {
            'S' -> 1
            'E' -> 26
            else -> this.code - 'a'.code + 1
        }
}
