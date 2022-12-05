package day03

import java.io.File

object Day03 {

    @JvmStatic
    fun main(args: Array<String>) = part1()

    fun part1() {
        val rucks = readRucksacks("src/day03/part01-input.txt")
        println(rucks.sumOf { it.commonItem().priority() }) // 7917
    }

    fun readRucksacks(filename: String): List<Rucksack> =
        File(filename).readLines().map { Rucksack.fromString(it) }

    /**
     * Give priority of a=1, b=2, ..., z=26, A=27, B=28, ..., Z=52
     */
    fun Char.priority(): Int {
        // Case insensitive, A/a = 10 -> Z/z = 35
        val base = Character.getNumericValue(this) - 9
        val offset = if (this.isUpperCase()) 26 else 0

        return base + offset
    }
}

data class Rucksack(val c1: List<Char>, val c2: List<Char>) {

    private val s1 = c1.toSet()
    private val s2 = c2.toSet()

    fun commonItem(): Char {
        val shared = s1.intersect(s2)
        return shared.firstOrNull() ?: error("Not exactly one element shared: s1=$s1, s2=$s2, shared=$shared")
    }

    companion object {
        fun fromString(s: String): Rucksack {
            val midpoint = s.length / 2
            return Rucksack(
                c1 = s.substring(0, midpoint).toList(),
                c2 = s.substring(midpoint).toList(),
            )
        }
    }
}