package day15

import util.Point
import util.measurePerf
import util.prettyPrint
import java.io.File
import java.math.BigInteger
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

object Day15 {

    @JvmStatic
    fun main(args: Array<String>) = measurePerf { part2() }

    enum class Config(val file: String, val targetY: Int, val searchLimit: Int) {
        EXAMPLE("example", 10, 20),
        PUZZLE("input", 2_000_000, 4_000_000),
    }
    val config = Config.PUZZLE

    val input = File("src/day15/${config.file}.txt").readLines()
    val targetY = config.targetY
    val searchLimit =  config.searchLimit

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

        val coveredPoints = reports.flatMap { coveringPoints(it) }
            .also { println("Non-unique covered points: ${it.size.prettyPrint()}") } // 13,054,600
            .toSet()
            .also { println("Unique covered points: ${it.size.prettyPrint()}") } // 6,425,134

        // We want to know where beacons _cannot_ be, so remove any beacons
        // at target=y if we covered their position.
        val beaconsAtTarget = reports.map { it.beacon }
            .filter { it.y == targetY }
            .also { println("Beacon points: ${it.size}") } // 5
            .toSet()

        println((coveredPoints - beaconsAtTarget).size) // 6425133
    }

    fun coveringPoints(r: SensorReport): Set<Point> {
        val verticalDistToTarget = abs(targetY - r.sensor.y)

        // not covering
        if (verticalDistToTarget > r.radius) return emptySet()

        // how much of the radius is 'consumed' to reach targetY?
        // we can use the remainder to cover targetY in both
        // horizontal directions.
        return when (val horizontalRadius = r.radius - verticalDistToTarget) {
            0 -> setOf(Point(r.sensor.x, targetY))
            else -> (-horizontalRadius..horizontalRadius).map { dx ->
                Point(r.sensor.x + dx, targetY)
            }.toSet()
        }
    }

    /**
     * Lol, let's put Part01 aside completely.
     *
     * Consider the intersection points of the beacon/sensor perimeter.
     * How many intersection points are outside all beacon/sensor circles?
     * This will give the sole uncovered point.
     *
     * _More words on this approach in the readme reflections._
     */
    fun part2() {
        val reports = input.map { SensorReport.fromString(it) }
        val perimeters = reports.flatMap { it.perimeter }

        // lmao shameful
        val intersectionFreq = emptyMap<Point, Int>().toMutableMap()
        reports.flatMap { r ->
            r.perimeter.map { p ->
                perimeters.forEach { pp ->
                    p.intersectionPointWith(pp)?.let { x ->
                        intersectionFreq[x] = intersectionFreq.getOrDefault(x, 0) + 1
                    }
                }
            }
        }
        println("Perimeters intersect ${intersectionFreq.size} times") // 1025

        // We need at least three intersecting perimeters to fully enclose
        // a point by the sensor->beacon circumference:
        //   2...1.3
        //   .2.1.3.
        //   ..1X3..
        //   .1.2...
        //   1.3.2..
        val candidates = intersectionFreq.entries.filter { it.value >= 3 }
        println("Found ${candidates.size} candidates") // 53

        val uncoveredPoints = candidates
            .filter { (p, _) -> p.x in 0..searchLimit && p.y in 0..searchLimit }
            .also { println("Candidates after limit filter: ${it.size}") } // 49
            .filter { (p, _) -> reports.none { r -> r.contains(p) } }

        val winner = uncoveredPoints.singleOrNull()?.key
            ?: error("Too many uncovered points! (${uncoveredPoints.size}) $uncoveredPoints")

        println("$winner f=${winner.frequency}") // (2749047, 3429555) f=10,996,191,429,555
    }

    // lol, a little int overflow in this problem. as a treat.
    val Point.frequency: BigInteger
        get() = x.toBigInteger() * 4_000_000.toBigInteger() + y.toBigInteger()

    data class Segment(val start: Point, val end: Point) {
        val slope: Float
            get() =
                if (start.x == end.x) Float.POSITIVE_INFINITY // lmao so responsible
                else (end.y.toFloat() - start.y) / (end.x - start.x)

        val yIntercept: Int
            get() = when (slope) {
                0f -> start.y
                Float.POSITIVE_INFINITY -> error("$this has infinite slope and cannot intercept y-axis at a point")
                else -> start.y - (slope * start.x).toInt()
            }

        /**
         * Single point where this Segment intersects [other], or `null` if the two
         * do not intersect.
         */
        fun intersectionPointWith(other: Segment): Point? {
            // If the slopes are the same we know the segments
            //   1. Are parallel, never intersect; or,
            //   2. Overlap, don't yield a single candidate; or,
            //   3. Overlap at the endpoint, then one is trivial
            if (this.slope == other.slope) return null

            // https://en.wikipedia.org/wiki/Line-line_intersection#Given_two_line_equations
            val px = (this.yIntercept - other.yIntercept) / (other.slope - this.slope)
            val py = (other.slope * px) + other.yIntercept
            val p = Point(px.toInt(), py.toInt())

            // Is the intersection of the lines _actually_ in the segments?
            // The segment we check is arbitrary since the intersection must
            // be in both.
            return if (this.contains(p)) p else null
        }

        /**
         * Is [p] an interior point of the Segment?
         */
        fun contains(p: Point): Boolean {
            // These segments tend to be huge (>100k points) so we avoid
            // checking if 'p' is in that list.
            //
            // Instead, we're going to check if the slope of start->p is
            // the same as the segment. If it's not, then p can't be on the
            // line joining start<->end and not an interior point.
            val pSeg = Segment(start = start, end = p)
            if (pSeg.slope != slope) return false

            // Now we check for the case where p is on the start<->end line
            // but on in the interior.
            val minX = min(start.x, end.x)
            val maxX = max(start.x, end.x)
            val minY = min(start.y, end.y)
            val maxY = max(start.y, end.y)

            return (p.x in minX..maxX) && (p.y in minY..maxY)
        }

        override fun toString() = "$start -> $end"
    }

    data class SensorReport(val sensor: Point, val beacon: Point) {

        /**
         * Segments enclosing the circle centered at [sensor] with
         * radius sensor->beacon. These points are outside the circle.
         */
        val perimeter: Set<Segment>
            get() {
                val up = Point(x = sensor.x, y = sensor.y - radius - 1)
                val down = Point(x = sensor.x, y = sensor.y + radius + 1)
                val left = Point(x = sensor.x - radius - 1, y = sensor.y)
                val right = Point(x = sensor.x + radius + 1, y = sensor.y)

                return setOf(
                    Segment(up, right),
                    Segment(right, down),
                    Segment(down, left),
                    Segment(left, up),
                )
            }

        val radius: Int
            get() = sensor.vecTo(beacon).manhattanLength

        /**
         * Is [p] within the circle centered at [sensor] with radius
         * sensor -> beacon?
         */
        fun contains(p: Point) = sensor.vecTo(p).manhattanLength <= radius

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
