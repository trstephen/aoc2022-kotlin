package day13

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive
import util.measurePerf
import java.io.File

object Day13 {

    @JvmStatic
    fun main(args: Array<String>) = measurePerf { part2() }

    val input = File("src/day13/input.txt").readLines()

    fun part1() {
        val signals = input.chunked(3).mapIndexed { idx, lines ->
            Signal(left = lines[0].toSignal(), right = lines[1].toSignal(), idx = idx + 1)
        }
        signals.forEach(::println)

        val orderedSignals = signals.filter { it.left <= it.right }
        println(orderedSignals.sumOf { it.idx }) // 6623
    }

    fun part2() {
        val dividerSignals = listOf(2, 6).map {
            SignalData.SignalList(elems = listOf(SignalData.SignalList(it)))
        }
        val signals = input.chunked(3).flatMap { lines ->
            listOf(lines[0].toSignal(), lines[1].toSignal())
        } + dividerSignals

        val sortedSignals = signals.sorted()
        val dividerIdxs = dividerSignals.map {
            sortedSignals.indexOf(it) + 1
        }
        println(dividerIdxs) // [117, 197]
        println(dividerIdxs.reduce(Int::times)) // 23049
    }

    data class Signal(val left: SignalData, val right: SignalData, val idx: Int) {
        override fun toString(): String =
            """
                == Signal $idx
                l: $left
                r: $right
                ordered?: ${left <= right}

            """.trimIndent()
    }

    // Oh my, all the input is a valid JSON list~
    fun String.toSignal(): SignalData = Json.parseToJsonElement(this).toSignalData()

    // For less awkward comparisons than raw JsonElement
    fun JsonElement.toSignalData(): SignalData =
        when (val e = this) {
            is JsonArray -> SignalData.SignalList(elems = e.map { it.toSignalData() })
            is JsonPrimitive -> SignalData.SignalInt(value = e.content.toInt())
            else -> error("unexpected element '$e' (${e.javaClass.simpleName})")
        }

    sealed class SignalData : Comparable<SignalData> {
        data class SignalList(val elems: List<SignalData>) : SignalData() {

            constructor(vararg ints: Int) : this(elems = ints.map { SignalInt(it) })

            override fun compareTo(other: SignalData): Int =
                when (other) {
                    // lmao hold on to your butts
                    is SignalList -> when {
                        elems.isEmpty() && other.elems.isEmpty() -> 0
                        elems.isEmpty() -> -1
                        other.elems.isEmpty() -> 1
                        else -> when (val c = elems.first().compareTo(other.elems.first())) {
                            0 -> SignalList(elems.drop(1)).compareTo(SignalList(other.elems.drop(1)))
                            else -> c
                        }
                    }
                    is SignalInt -> compareTo(SignalList(other.value))
                }

            override fun toString() = elems.joinToString(prefix = "[", separator = ",", postfix = "]")
        }
        data class SignalInt(val value: Int) : SignalData() {
            override fun compareTo(other: SignalData): Int =
                when (other) {
                    is SignalList -> SignalList(value).compareTo(other)
                    is SignalInt -> value.compareTo(other.value)
                }

            override fun toString() = value.toString()
        }
    }
}
