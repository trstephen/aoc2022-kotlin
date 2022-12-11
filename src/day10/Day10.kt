package day10

import kotlin.collections.ArrayDeque
import kotlin.math.*
import java.io.File

object Day10 {

    @JvmStatic
    fun main(args: Array<String>) = part2()

    val input = File("src/day10/input.txt").readLines()

    fun part1() {
        val instructions = ArrayDeque(input.map { Instruction.fromString(it) })
        var currInstruction = instructions.removeFirst()
        var remainingCycles = currInstruction.cycles
        var register = 1

        val signalStrength = mutableListOf<Int>()
        var idx = 1
        while (instructions.isNotEmpty()) {
            if (idx in setOf(20, 60, 100, 140, 180, 220)) {
                println("idx=$idx r=$register")
                signalStrength.add(idx * register)
            }

            remainingCycles--
            if (remainingCycles == 0) {
                when (val i = currInstruction) {
                    Instruction.NOOP -> { }
                    is Instruction.AddX -> register += i.value
                }
                currInstruction = instructions.removeFirst()
                remainingCycles = currInstruction.cycles
            }

            idx++
        }

        println(signalStrength.sum()) // 13440
    }

    fun part2() {
        val instructions = ArrayDeque(input.map { Instruction.fromString(it) })
        var currInstruction = instructions.removeFirst()
        var remainingCycles = currInstruction.cycles
        var register = 1
        val crtPixels: MutableList<String> = mutableListOf()
        val crtWidth = 40

        var idx = 1
        while (instructions.isNotEmpty()) {
            remainingCycles--
            if (remainingCycles == 0) {
                when (val i = currInstruction) {
                    Instruction.NOOP -> { }
                    is Instruction.AddX -> register += i.value
                }
                currInstruction = instructions.removeFirst()
                remainingCycles = currInstruction.cycles
            }

            if (abs(register - (idx % crtWidth)) <= 1) crtPixels.add("#") else crtPixels.add(".")

            idx++
        }

        val crt = crtPixels.chunked(crtWidth).joinToString("\n") { it.joinToString("") }
        println(crt) // PBZGRAZA, close enough ;)
    }

    sealed class Instruction(val cycles: Int) {
        object NOOP : Instruction(1) {
            override fun toString() = "noop"
        }
        data class AddX(val value: Int) : Instruction(2)

        companion object {
            fun fromString(s: String) = when (s) {
                "noop" -> NOOP
                else -> AddX(s.split(" ").last().toInt())
            }
        }
    }
}