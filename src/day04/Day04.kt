package day04

import java.io.File

object Day04 {

    @JvmStatic
    fun main(args: Array<String>) = part1()

    fun part1() {
        val assignments = readAssignments("src/day04/part01-input.txt")
        println(assignments.count { it.isInclusive() }) // 462
    }

    fun readAssignments(filename: String): List<Pair<Assignment, Assignment>> =
        File(filename).readLines().map { line ->
            val (a1, a2) = line.split(",").map {
                val (start, end) = it.split("-").map { it.toInt() }
                Assignment(start, end)
            }
            Pair(a1, a2)
        }
}

data class Assignment(val startSection: Int, val endSection: Int) {

    /**
     * Do the inclusive bounds of [this] contain [other]?
     */
    fun contains(other: Assignment): Boolean =
        this.startSection <= other.startSection && this.endSection >= other.endSection
}

/**
 * One of [first] or [second] contains the other.
 */
fun Pair<Assignment, Assignment>.isInclusive(): Boolean =
    first.contains(second) || second.contains(first)