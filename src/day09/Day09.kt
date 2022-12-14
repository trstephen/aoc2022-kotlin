package day09

import java.io.File

object Day09 {

    @JvmStatic
    fun main(args: Array<String>) = part2()

    val input = File("src/day09/input.txt").readLines()

    fun part1() {
        val moves = input.toMoves()

        // I gambling that we don't care about the grid for Part 2 so
        // I'm not going to model that. Instead, for every move I'll
        //   1. Move the Head accordingly
        //   2. Find the next tail position given the new head
        //   3. GOTO 1
        // If I keep track of all the tail locations I can .toSet() it
        // and get the number of locations visited.
        var head = Point(0, 0)
        var tail = Point(0, 0)
        val path = mutableSetOf<Point>()

        moves.forEach { m ->
            head = head.move(m)
            tail = nextTail(tail, head)
            path.add(tail)
        }

        println(path.size) // 6563
    }

    fun part2() {
        val moves = input.toMoves()

        var head = Point(0, 0)
        var tails = List(9) { Point(0, 0) }
        val covered = mutableSetOf<Point>()

        moves.forEach { m ->
            head = head.move(m)
            tails = tails.fold(listOf(head)) { acc, tail ->
                acc + nextTail(tail = tail, head = acc.last())
            }.drop(1) // lol something is busted with my fold. It adds 'head' every cycle and grows in length
            covered.add(tails.last())
        }

        println(covered.size) // 2653
    }

    data class Point(val x: Int, val y: Int) {

        fun vecTo(other: Point) = Pair(other.x - x, other.y - y)

        fun move(m: Move): Point = when (m) {
            Move.UP -> Point(x, y + 1)
            Move.DOWN -> Point(x, y - 1)
            Move.LEFT -> Point(x - 1, y)
            Move.RIGHT -> Point(x + 1, y)
        }

        fun doMoves(vararg ms: Move): Point = ms.fold(this) { acc, m -> acc.move(m) }

        override fun toString() = "($x, $y)"
    }

    /**
     * If everything goes as planned then [head] can never be outside of
     * these positions, when [tail] is at the origin:
     * ```
     *      -2  -1   0  +1  +2
     *   +2  -   .   .   .   -
     *   +1  .   .   .   .   .
     *    0  .   .   T   .   .
     *   -1  .   .   .   .   .
     *   -2  -   .   .   .   -
     * ```
     * Let's put that into a `when` baybeeeee
     *
     * Part 2: Now we can land on - spaces
     */
    fun nextTail(tail: Point, head: Point): Point =
        when (val vec = tail.vecTo(head)) {
            // No moves required for the 3x3 grid around the origin of T
            Pair(-1, 1), Pair(0, 1), Pair(1, 1),
                Pair(-1, 0), Pair(0, 0), Pair(1, 0),
                Pair(-1, -1), Pair(0, -1), Pair(1, -1) -> tail
            Pair(-2, 2) -> tail.doMoves(Move.LEFT, Move.UP)
            Pair(-1, 2) -> tail.doMoves(Move.LEFT, Move.UP)
            Pair(0, 2) -> tail.doMoves(Move.UP)
            Pair(1, 2) -> tail.doMoves(Move.RIGHT, Move.UP)
            Pair(2, 2) -> tail.doMoves(Move.RIGHT, Move.UP)
            Pair(-2, 1) -> tail.doMoves(Move.LEFT, Move.UP)
            Pair(2, 1) -> tail.doMoves(Move.RIGHT, Move.UP)
            Pair(-2, 0) -> tail.doMoves(Move.LEFT)
            Pair(2, 0) -> tail.doMoves(Move.RIGHT)
            Pair(-2, -1) -> tail.doMoves(Move.LEFT, Move.DOWN)
            Pair(2, -1) -> tail.doMoves(Move.RIGHT, Move.DOWN)
            Pair(-2, -2) -> tail.doMoves(Move.LEFT, Move.DOWN)
            Pair(-1, -2) -> tail.doMoves(Move.LEFT, Move.DOWN)
            Pair(0, -2) -> tail.doMoves(Move.DOWN)
            Pair(1, -2) -> tail.doMoves(Move.RIGHT, Move.DOWN)
            Pair(2, -2) -> tail.doMoves(Move.RIGHT, Move.DOWN)
            else -> error("Tail=$tail too far from Head=$head vec=$vec")
        }

    fun List<String>.toMoves(): List<Move> = this.flatMap { line ->
        // e.g. 'R 3' -> [RIGHT, RIGHT, RIGHT]
        val (sMove, sTimes) = line.split(" ")
        List(sTimes.toInt()) { Move.fromString(sMove) }
    }

    enum class Move {
        UP, DOWN, LEFT, RIGHT;

        companion object {
            fun fromString(s: String): Move = when (s) {
                "U" -> UP
                "D" -> DOWN
                "L" -> LEFT
                "R" -> RIGHT
                else -> error("No move for '$s'")
            }
        }
    }
}