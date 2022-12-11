package day11

import java.io.File

object Day11 {

    @JvmStatic
    fun main(args: Array<String>) = part1()

    val input = File("src/day11/input.txt").readLines()

    fun part1() {
        val monkeys = input.chunked(7).map { Monkey.fromLines(it) }.toTypedArray()
        val inspectedTimes = IntArray(monkeys.size) { 0 }

        repeat(20) {
            monkeys.forEach { m ->
                val results = m.inspectAllAndClear()
                inspectedTimes[m.id] += results.size
                results.distributeTo(monkeys)
            }
        }
        monkeys.prettyPrint()
        inspectedTimes.prettyPrint()

        println(inspectedTimes.sortedDescending().take(2).reduce(Int::times)) // 98280
    }

    data class Monkey(
        val id: Int,
        val items: MutableList<Int>,
        val op: (Int) -> Int,
        val testDivisor: Int,
        val testTrueId: Int,
        val testFalseId: Int,
    ) {

        override fun toString() = "Monkey $id: ${items.joinToString(", ")}"

        private fun Int.inspect(): InspectResult {
            val newItem = op(this) / 3

            return InspectResult(
                item = newItem,
                toMonkeyId = if (newItem % testDivisor == 0) testTrueId else testFalseId,
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
                    testDivisor = sDiv.toInt(),
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

    fun IntArray.prettyPrint() {
        println(mapIndexed { idx, x -> "$idx=$x" }.joinToString(", "))
    }
}