package day06

import java.io.File

object Day06 {

    @JvmStatic
    fun main(args: Array<String>) = part2()

    val input = File("src/day06/input.txt").readLines().first()

    fun part1() {
        // val example = examples1.map { (s, expected) -> s.findUniqueWindow(4) to expected }
        println(input.findUniqueWindow(windowSize = 4)) // 1175
    }

    fun part2() {
        // val example = examples2.map { (s, expected) -> s.findUniqueWindow(14) to expected }
        println(input.findUniqueWindow(windowSize = 14)) // 3217
    }

    fun String.findUniqueWindow(windowSize: Int): Int {
        val windowIdx = this.asSequence()
            .windowed(size = windowSize)
            .indexOfFirst { it.toSet().size == windowSize } // no dupes in window!

        // The first window is `windowSize` char long and we want the idx of the first
        // non-repeating char. This is the last value in the window.
        return windowIdx + windowSize
    }
}

val examples1 = listOf(
    "mjqjpqmgbljsphdztnvjfqwrcgsmlb" to 7,
    "bvwbjplbgvbhsrlpgdmjqwftvncz" to 5,
    "nppdvjthqldpwncqszvftbrmjlhg" to 6,
    "nznrnfrfntjfmvfwmzdfjlvtqnbhcprsg" to 10,
    "zcfzfwzzqfrljwzlrfnpqdbhtmscgvjw" to 11,
)

val examples2 = listOf(
    "mjqjpqmgbljsphdztnvjfqwrcgsmlb" to 19,
    "bvwbjplbgvbhsrlpgdmjqwftvncz" to 23,
    "nppdvjthqldpwncqszvftbrmjlhg" to 23,
    "nznrnfrfntjfmvfwmzdfjlvtqnbhcprsg" to 29,
    "zcfzfwzzqfrljwzlrfnpqdbhtmscgvjw" to 26,
)