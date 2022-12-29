package day15

import util.Point
import java.io.File
import kotlin.math.abs

object Day15 {

    @JvmStatic
    fun main(args: Array<String>) = part1()

    val input = File("src/day15/input.txt").readLines()
    val targetY = 2_000_000

    /**
     * My plan is to narrowly solve for the y=target covering.
     * We don't have to find the total covering, just the 'radius'
     * of the covering and then seeing how much of y=target is
     * inside the radius.
     *
     * For each report:
     *   1. find the sensor -> beacon vector (radius)
     *   2. can we reach y=target by going straight up/down
     *      from the sensor?
     *   3. if yes, what are the points we covered?
     * At the end, how many unique points do we have?
     *
     * Will this kneecap me for Part02? Probably!
     */
    fun part1() {
        val reports = input.map { SensorReport.fromString(it) }

        val coveredPoints = reports.flatMap { coveringPoints(it) }.toSet()

        // We want to know where beacons _cannot_ be, so remove any beacons
        // at target=y if we covered their position.
        val beaconsAtTarget = reports.map { it.beacon }.filter { it.y == targetY }.toSet()

        println((coveredPoints - beaconsAtTarget).size) // 6425133
    }

    fun coveringPoints(r: SensorReport): Set<Point> {
        val vec = r.beacon.vecTo(r.sensor)
        val radius = abs(vec.x) + abs(vec.y)
        val verticalDistToTarget = abs(targetY - r.sensor.y)

        // not covering
        if (verticalDistToTarget > radius) return emptySet()

        // how much of the radius is 'consumed' to reach targetY?
        // we can use the remainder to cover targetY in both
        // horizontal directions.
        return when (val horizontalRadius = radius - verticalDistToTarget) {
            0 -> setOf(Point(r.sensor.x, targetY))
            else -> (-horizontalRadius..horizontalRadius).map { dx ->
                Point(r.sensor.x + dx, targetY)
            }.toSet()
        }
    }

    data class SensorReport(val sensor: Point, val beacon: Point) {
        companion object {
            private val re = """Sensor at x=(-?\d+), y=(-?\d+): closest beacon is at x=(-?\d+), y=(-?\d+)""".toRegex()
            fun fromString(s: String): SensorReport {
                val (_, sx, sy, bx, by) = re.matchEntire(s)?.groupValues ?: error("No match on '$s'")
                return SensorReport(
                    sensor = Point(sx.toInt(), sy.toInt()),
                    beacon = Point(bx.toInt(), by.toInt()),
                )
            }
        }
    }
}
