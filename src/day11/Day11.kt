package day11

import java.io.File

object Day11 {

    @JvmStatic
    fun main(args: Array<String>) = part2()

    val input = File("src/day11/example.txt").readLines()

    fun part1(numRounds: Int = 20) {
        val monkeys = input.chunked(7).map { Monkey.fromLines(it) }.toTypedArray()
        monkeyMod = monkeys.map { it.modulus }.reduce(Int::times)

        repeat(numRounds) {
            monkeys.forEach { m ->
                val results = m.inspectAllAndClear()
                results.distributeTo(monkeys)
            }
        }
        monkeys.prettyPrint()

        val inspectedTimes = monkeys.map { it.inspected }
        inspectedTimes.prettyPrint()

        println(inspectedTimes.sortedDescending().take(2).reduce(Int::times)) // 98280
    }

    var doPart2 = false
    var monkeyMod: Int = Int.MAX_VALUE
    fun part2() {
        // The conceit of part 2 is that with no 'div by 3 every round' and all ops increasing
        // the item value, their values will get real big real fast. Int overflows make the values
        // incorrect, but worse than that is the perf of the modulo operator. The modulus value
        // for monkeys becomes much much smaller than the item value. Oh no the CPU spends a lot
        // of time in factorization in this case.
        //
        // We can avoid unbounded item growth, and preserve the correct behavior of each monkey's
        // modulus if if we first take the modulo of the product of all monkey moduli.
        //    M = m_1.modulus * m_2.modulus * ... * m_n.modulus
        //    nextItem = (item % M ) % m_i.modulus
        // Oh look, all the moduli are primes so we don't even have to bother optimizing for M as
        // the Lowest Common Multiple.
        // And the answer doesn't care about _what_ the values are. We're free to manipulate them
        // as long as we retain the value of `item % m_i.modulus`.
        //
        // What I've just described is a Ring in the abstract algebra sense.

        doPart2 = true
        part1(numRounds = 10_000)
    }

    data class Monkey(
        val id: Int,
        val items: MutableList<Int>,
        val op: (Int) -> Int,
        val modulus: Int,
        val testTrueId: Int,
        val testFalseId: Int,
    ) {

        var inspected: Int = 0

        override fun toString() = "Monkey $id: ${items.joinToString(", ")}"

        private fun Int.inspect(): InspectResult {
            inspected++

            val newItem =
                if (doPart2) op(this) % monkeyMod
                else op(this) / 3

            return InspectResult(
                item = newItem,
                toMonkeyId = if (newItem % modulus == 0) testTrueId else testFalseId,
            )
        }

        fun inspectAllAndClear(): List<InspectResult> {
            val results = items.map { it.inspect() }
            this.items.clear()
            return results
        }

        companion object {
            fun fromLines(lines: List<String>): Monkey {
                val (sId) = """Monkey (\d+):""".toRegex().matchEntire(lines[0])!!.destructured
                val sItems = lines[1].substringAfter(": ").split(", ")
                val (sOp, sOpVal) = lines[2].substringAfter("old ").split(" ")
                val sDiv = lines[3].substringAfter("by ")
                val sTrue = lines[4].substringAfter("monkey ")
                val sFalse = lines[5].substringAfter("monkey ")

                return Monkey(
                    id = sId.toInt(),
                    items = sItems.map { it.toInt() }.toMutableList(),
                    op = when (sOp) {
                        "*" -> { x -> x * (if (sOpVal == "old") x else sOpVal.toInt()) }
                        "+" -> { x -> x + (if (sOpVal == "old") x else sOpVal.toInt())  }
                        else -> error("no op vals for sOp=$sOp sOpVal=$sOpVal")
                    },
                    modulus = sDiv.toInt(),
                    testTrueId = sTrue.toInt(),
                    testFalseId = sFalse.toInt(),
                )
            }
        }
    }

    data class InspectResult(val item: Int, val toMonkeyId: Int)

    fun List<InspectResult>.distributeTo(monkeys: Array<Monkey>) {
        this.forEach { r ->
            monkeys[r.toMonkeyId].items.add(r.item)
        }
    }

    fun Array<Monkey>.prettyPrint() {
        println(joinToString("\n"))
    }

    fun List<Int>.prettyPrint() {
        println(mapIndexed { idx, x -> "$idx=$x" }.joinToString(", "))
    }
}