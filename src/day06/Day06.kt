package day06

import java.io.File

object Day06 {

    @JvmStatic
    fun main(args: Array<String>) = part1()

    val input = File("src/day06/input.txt").readLines().first()

    fun part1() {
        // val example = examples1.map { (s, expected) -> s.findPart1() to expected }
        println(input.findPart1())
    }

    fun String.findPart1(): Int {
        val windowSize = 4
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