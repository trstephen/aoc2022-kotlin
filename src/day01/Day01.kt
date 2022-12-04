package day01

import java.io.File

object Day01 {

    @JvmStatic
    fun main(args: Array<String>) = part1()

    fun part1() {
        val elves = readElves("src/day01/part01-input.txt")
        println(elves.size)
        println(elves.maxOf { it.calories.sum() }) // 71934
    }

    fun readElves(filename: String): List<Elf> {
        // Make a little state machine that accumulates a list of calories until it sees
        // a blank line. Then it dumps the list into an 'Elf'.
        // My hunch is part 2 will ask something about all elves, so I'm keeping track of
        // all of them rather than the one with most calories.

        val elves = mutableListOf<Elf>()
        val calories = mutableListOf<Int>()
        File(filename).forEachLine {
            when(it) {
                "" -> {
                    // end of an elf!
                    elves.add(Elf(calories.toList())) // cast as 'List', otherwise we get a ref to the MutableList
                    calories.clear()
                }
                else -> {
                    // must be a calorie
                    calories.add(it.toInt())
                }
            }
        }

        // The last line probably wasn't blank, so we have to dump our accumulator for
        // the last elf.
        if (calories.isNotEmpty()) { elves.add(Elf(calories)) }

        return elves
    }
}

data class Elf(val calories: List<Int>)