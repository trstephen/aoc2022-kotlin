package day14

import util.Point
import util.measurePerf
import java.io.File
import kotlin.math.max
import kotlin.math.min

object Day14 {

    @JvmStatic
    fun main(args: Array<String>) = measurePerf { part2() }

    val input = File("src/day14/input.txt").readLines()

    fun part1() {
        // drop sand into the occupied points
        // descend until 1) we reach the lowest y 2) we exhaust possible moves
        var reachedFloor = false
        while (!reachedFloor) {
            var sandPoint = Point(500, 0)
            while (true) {
                when (val result = descend(sandPoint)) {
                    DescendResult.AtFloor -> {
                        reachedFloor = true
                        break
                    }
                    is DescendResult.Falling -> {
                        sandPoint = result.p
                    }
                    is DescendResult.Settled -> {
                        occupiedPoints += result.p
                        break
                    }
                }
            }
        }
        println(occupiedPoints.size - rockPoints.size) // 825
    }

    fun part2() {
        // drop sand into the occupied points
        // descend until 1) we reach the lowest y 2) we exhaust possible moves
        var settledAtTop = false
        while (!settledAtTop) {
            var sandPoint = Point(500, 0)
            while (true) {
                when (val result = descend(sandPoint, floor + 1)) {
                    DescendResult.AtFloor -> {
                        occupiedPoints += sandPoint
                        break
                    }
                    is DescendResult.Falling -> {
                        sandPoint = result.p
                    }
                    is DescendResult.Settled -> {
                        occupiedPoints += result.p
                        if (result.p.y == 0) settledAtTop = true
                        break
                    }
                }
            }
        }
        println(occupiedPoints.size - rockPoints.size) // 26729
    }

    fun descend(start: Point, limit: Int = floor): DescendResult {
        if (start.y == limit) return DescendResult.AtFloor

        return listOf(
            Point(start.x, start.y + 1),
            Point(start.x - 1, start.y + 1),
            Point(start.x + 1, start.y + 1),
        ).firstOrNull { it !in occupiedPoints }
            ?.let { DescendResult.Falling(it) }
            ?: DescendResult.Settled(start)
    }

    sealed class DescendResult {
        object AtFloor : DescendResult()
        data class Falling(val p: Point) : DescendResult()
        data class Settled(val p: Point) : DescendResult()
    }

    val rockPoints = input.flatMap { line ->
        line.split(" -> ").windowed(2).flatMap { pair ->
            val (from, to) = pair.map { it.split(",") }.map { it.toPoint() }
            interpolate(from, to)
        }.toSet()
    }.toSet()

    // This list is gonna get _big_ as it fills with sand. Hoist it into a global so we
    // can avoid copying through function assignments
    val occupiedPoints = rockPoints.toMutableSet()

    // The lowest platform. Assuming there's not something weird like a |
    val floor = rockPoints.maxOf { it.y }

    fun List<String>.toPoint() = Point(
        x = this[0].toInt(),
        y = this[1].toInt(),
    )

    fun interpolate(from: Point, to: Point): List<Point> =
        (min(from.x, to.x)..max(from.x, to.x)).flatMap { x ->
            (min(from.y, to.y)..max(from.y, to.y)).map { y ->
                Point(x, y)
            }
        }
}
