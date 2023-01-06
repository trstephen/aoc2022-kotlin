package day16

import util.permute
import util.prettyPrint
import java.io.File
import java.time.Instant

object Day16 {

    @JvmStatic
    fun main(args: Array<String>) = part1()

    val input = File("src/day16/input.txt").readLines()

    val valves = input.map { Valve.fromString(it) }

    fun part1() {
        initFW()
        val nonZeroValves = valves.filter { it.flowRate > 0 }.sortedByDescending { it.flowRate }.map { it.name }
        println("There are ${nonZeroValves.size} non-zero flow rate valves")

        var cnt = 0.toBigInteger()
        val logInterval = 100_000_000.toBigInteger()

        var bestPath = Pair(0, emptyList<String>())
        permute(nonZeroValves).forEach { path ->
            cnt++
            if (cnt % logInterval == 0.toBigInteger()) println("${Instant.now()} ${cnt.prettyPrint()}")

            val released = flowReleased(path)
            if (released > bestPath.first) {
                bestPath = Pair(released, path)
                println("== New best path! $bestPath")
            }
        }
        println("Checked $cnt permutations of valves $nonZeroValves")
        println("Best: $bestPath")

    }

    fun flowReleased(path: List<String>): Int =
        (listOf("AA") + path).windowed(2)
            .fold(listOf(FlowSegment(timeRemaining = 30, valve = valves["AA"]))) { acc, (from, to) ->
                val distance = fwDist[from, to]
                val remaining = acc.last().timeRemaining - distance - 1
                acc + FlowSegment(timeRemaining = remaining, valve = valves[to])
            }.filter { it.timeRemaining > 0 }
            .fold(0) { acc, seg -> acc + seg.released }

    data class FlowSegment(val timeRemaining: Int, val valve: Valve) {
        val released: Int = timeRemaining * valve.flowRate
    }

    /**
     * Initialize [fwDist] and [fwPath] using the Floyd-Warshall algorithm.
     *
     * https://en.wikipedia.org/wiki/Floyd-Warshall_algorithm
     */
    fun initFW() {
        // Matrix init
        edges.forEach { e ->
            fwDist[e] = fwWeight(e)
            fwPath[e] = e.to
        }
        valves.forEach { v ->
            fwDist[v.name, v.name] = 0
            fwPath[v.name, v.name] = v.name
        }

        // FW time!
        val vn = valves.map { it.name }
        (vn).forEach { k ->
            (vn).forEach { i ->
                (vn).forEach { j ->
                    if (fwDist[i, j] > fwDist[i, k] + fwDist[k, j]) {
                        fwDist[i, j] = fwDist[i, k] + fwDist[k, j]
                        fwPath[i, j] = fwPath[i, k]
                    }
                }
            }
        }
    }

    /**
     * A consistent association of [Valve.name] to indices of the various arrays in the solution.
     * Initially for the Floyd-Warshall distance and path matrices but I'm sure others will come up.
     */
    val valveIdx = valves.mapIndexed { idx, v -> v.name to idx }.toMap()

    val edges = valves.flatMap { v -> v.tunnelsTo.map { Edge(from = v.name, to = it) } }

    /**
     * Sentinel for 'infinity' in a reachability matrix. We avoid [Integer.MAX_VALUE] and such
     * because of overflow problems if our algorithm requires adding to this value.
     *
     * We take a value of 'size + 1' because the max acyclic path through the graph is 'size',
     * where every node is visited exactly once.
     */
    val UNREACHABLE = valves.size + 1

    val fwDist = List(valves.size) { MutableList(valves.size) { UNREACHABLE } }

    val fwPath = List(valves.size) { MutableList<String?>(valves.size) { null } }

    /**
     * Assigns a default weight if [edge] is actually in the graph.
     *
     * The problem doesn't have explicit costs associated with the paths and I couldn't
     * figure out how to model the [Valve.flowRate] as a cost (benefit).
     */
    fun fwWeight(edge: Edge): Int =
        if (edge.to in valves[edge.from].tunnelsTo) 1
        else UNREACHABLE

    fun shortestPath(from: String, to: String): List<String> {
        // From https://en.wikipedia.org/wiki/Floyd-Warshall_algorithm

        if (fwPath[from, to] == null) return emptyList()

        val path = emptyList<String>().toMutableList()
        var position = from
        while (position != to) {
            position = fwPath[position, to] ?: error("$from -> $to is unreachable! $path")
            path += position
        }

        return path
    }

    data class Valve(val name: String, val flowRate: Int, val tunnelsTo: List<String>) {

        companion object {

            // Valve AA has flow rate=0; tunnels lead to valves DD, II, BB
            // Valve HH has flow rate=22; tunnel leads to valve GG
            private val re = """Valve (\w+) has flow rate=(\d+); tunnel[s]? lead[s]? to valve[s]? (.+)""".toRegex()

            fun fromString(s: String): Valve {
                val (name, rate, tunnelsRaw) = re.matchEntire(s)?.destructured ?: error("No match on '$s'")
                return Valve(
                    name = name,
                    flowRate = rate.toInt(),
                    tunnelsTo = tunnelsRaw.split(", "),
                )
            }
        }
    }

    data class Edge(val from: String, val to: String)

    // Wherein I discover all the ways kotlin lets me override operators and proceed
    // to use almost all of them! Original motivation was to allow access to my distance
    // and path matrices by valve name instead of index. It just kinda grew from there as
    // I added whatever was convenient to access, either an Edge or raw from/to pairs.
    //
    // So, you can access these matrices for from/to valve _names_ by:
    // 1. M[from][to] (built-in, index only)
    // 2. M[from, to]
    // 3. M[Edge(from, to)]

    operator fun <T> List<List<T>>.get(from: String, to: String): T =
        this[valveIdx[from]!!][valveIdx[to]!!]

    operator fun <T> List<List<T>>.get(edge: Edge): T =
        this[edge.from, edge.to]

    operator fun <T> List<MutableList<T>>.set(from: String, to: String, value: T) {
        this[valveIdx[from]!!][valveIdx[to]!!] = value
    }

    operator fun <T> List<MutableList<T>>.set(e: Edge, value: T) {
        this[e.from, e.to] = value
    }

    operator fun List<Valve>.get(name: String): Valve = valves[valveIdx[name]!!]

    /**
     * Print graph matrices with col/row labels.
     */
    fun <T> List<List<T>>.printWithNames() {
        println(valveIdx.keys.joinToString(prefix = "\t", separator = " ") { it.padStart(4, ' ') })
        valveIdx.keys.forEachIndexed { idx, valveName ->
            println(this[idx].joinToString(prefix = "$valveName\t", separator = " ") { it.toString().padStart(4, ' ') })
        }
    }
}
