package day03

import java.io.File

object Day03 {

    @JvmStatic
    fun main(args: Array<String>) = part2()

    fun part1() {
        val rucks = readRucksacks("src/day03/part01-input.txt")
        println(rucks.sumOf { it.commonItem().priority() }) // 7917
    }

    fun part2() {
        val rucks = readRucksacks("src/day03/part01-input.txt")

        val groupSize = 3
        val groups = if (rucks.size % groupSize != 0) {
            error("groupSize=$groupSize is not a factor for ${rucks.size} rucks")
        } else {
            rucks.chunked(groupSize)
        }
        println(groups.sumOf { it.commonItem().priority() }) // 2585
    }

    fun readRucksacks(filename: String): List<Rucksack> =
        File(filename).readLines().map { Rucksack(contents = it) }

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

data class Rucksack(val contents: String) {

    fun commonItem(): Char {
        val midpoint = contents.length / 2
        val s1 = contents.substring(0, midpoint).toSet()
        val s2 = contents.substring(midpoint).toSet()
        val shared = s1.intersect(s2)

        return shared.singleOrNull() ?: error("Not exactly one element shared: s1=$s1, s2=$s2, shared=$shared")
    }

    fun commonItemsWith(other: Set<Char>): Set<Char> = contents.toSet().intersect(other)
}

fun List<Rucksack>.commonItem(): Char {
    val initDecumulator = if (this.isEmpty()) {
        error("List of rucks is empty!")
    } else {
        this.first().contents.toSet()
    }

    val common = findCommonItems(decumulator = initDecumulator, rest = this)

    return common.singleOrNull() ?: error("Not exactly one common element between: rucks=$this, common=$common")
}

private tailrec fun findCommonItems(decumulator: Set<Char>, rest: List<Rucksack>): Set<Char> =
    when (rest.size) {
        0 -> decumulator
        1 -> rest.first().commonItemsWith(decumulator)
        else -> findCommonItems(
            decumulator = rest.first().commonItemsWith(decumulator),
            rest = rest.drop(1),
        )
    }