package day05

import java.io.File

// Keys are columns, values are stacks of letters
typealias Cargo = Map<Int, ArrayDeque<Char>>
typealias CargoMover = Cargo.(move: Move) -> Unit

// I tried, and failed, to make an extension function on the companion object of ArrayDequeue
// that would let me `ArrayDequeue.of('A', 'B', 'C')`.

// Trying to read the initial stacks from the input is a mug's game, so I'm just hardcoding them
// in `CargoType.initCargo`. If it's more complicated than `readLines()` I'm not gonna do it~

enum class CargoType(val filename: String, val initCargo: Cargo) {
    EXAMPLE(
        filename = "src/day05/part01-example-moves.txt",
        initCargo = mapOf(
            1 to ArrayDeque(listOf('Z', 'N')),
            2 to ArrayDeque(listOf('M', 'C', 'D')),
            3 to ArrayDeque(listOf('P')),
        )
    ),
    INPUT(
        filename = "src/day05/part01-input-moves.txt",
        initCargo = mapOf(
            1 to ArrayDeque(listOf('B', 'P', 'N', 'Q', 'H', 'D', 'R', 'T')),
            2 to ArrayDeque(listOf('W', 'G', 'B', 'J', 'T', 'V')),
            3 to ArrayDeque(listOf('N', 'R', 'H', 'D', 'S', 'V', 'M', 'Q')),
            4 to ArrayDeque(listOf('P', 'Z', 'N', 'M', 'C')),
            5 to ArrayDeque(listOf('D', 'Z', 'B')),
            6 to ArrayDeque(listOf('V', 'C', 'W', 'Z')),
            7 to ArrayDeque(listOf('G', 'Z', 'N', 'C', 'V', 'Q', 'L', 'S')),
            8 to ArrayDeque(listOf('L', 'G', 'J', 'M', 'D', 'N', 'V')),
            9 to ArrayDeque(listOf('T', 'P', 'M', 'F', 'Z', 'C', 'G')),
        )
    )
    ;

    fun readMoves(): List<Move> = File(filename).readLines().map { Move.fromString(it) }

    fun applyMoves(mover: CargoMover): Cargo = initCargo.applyMoves(readMoves(), mover)
}

data class Move(val num: Int, val src: Int, val dest: Int) {
    companion object {

        private val re = """^move (\d+) from (\d+) to (\d+)""".toRegex()
        fun fromString(s: String): Move {
            val match = re.matchEntire(s) ?: error("No move match for '$s'")
            val (num, src, dest) = match.destructured
            return Move(num.toInt(), src.toInt(), dest.toInt())
        }
    }
}

/**
 * Moves elements from src -> dest one at a time. Reverses order of removed elements.
 */
val mover1 = fun Cargo.(move: Move) =
    repeat(move.num) {
        val elem = this[move.src]!!.removeLast()
        this[move.dest]!!.addLast(elem)
    }

/**
 * Moves elements from src -> dest in a group. Preserves order of removed elements.
 */
val mover2 = fun Cargo.(move: Move) {
    // I can't `removeLast(n: Int)` ????
    // Don't feel like mucking with Map.Value mutability so let's use the Dequeue primitives
    // but preserve order with a temp dequeue we insert from the bottom. I don't trust / want
    // to futz with the `subList` commands, see above gripe about mutability.
    val moved = ArrayDeque<Char>()
    repeat(move.num) {
        val elem = this[move.src]!!.removeLast()
        moved.addFirst(elem)
    }
    this[move.dest]!!.addAll(moved)
}

fun Cargo.message(): String = values.map { it.last() }.joinToString(separator = "")

private tailrec fun Cargo.applyMoves(moves: List<Move>, mover: CargoMover): Cargo =
    if (moves.isEmpty()) this
    else {
        mover(moves.first())
        this.applyMoves(moves.drop(1), mover)
    }

object Day05 {

    @JvmStatic
    fun main(args: Array<String>) = part2()

    fun part1() {
        val cargoType = CargoType.INPUT
        val finalCargo = cargoType.applyMoves(mover1)

        println(finalCargo)
        println(finalCargo.message()) // ZBDRNPMVH
    }

    fun part2() {
        val cargoType = CargoType.INPUT
        val finalCargo = cargoType.applyMoves(mover2)

        println(finalCargo)
        println(finalCargo.message()) // WDLPFNNNB
    }
}