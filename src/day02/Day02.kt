package day02

import java.io.File

object Day02 {

    @JvmStatic
    fun main(args: Array<String>) = part1()

    fun part1() {
        val strat = readStrategy("src/day02/part01-input.txt")
        println(strat.sumOf { it.score() }) // 11603
    }

    fun readStrategy(filename: String): List<RPSStrategy> =
        File(filename).readLines().map {
            val parts = it.split(" ")
            RPSStrategy(me = RPS.fromMyChoice(parts[1]), them = RPS.fromOpponentChoice(parts[0]))
        }
}

data class RPSStrategy(val me: RPS, val them: RPS) {
    fun score(): Int {
        val result = when {
            me == them -> RPSResult.DRAW
            (me == RPS.ROCK && them == RPS.SCISSORS) ||
                    (me == RPS.SCISSORS && them == RPS.PAPER) ||
                    (me == RPS.PAPER && them == RPS.ROCK) -> RPSResult.WIN
            else -> RPSResult.LOSE
        }

        return result.score + me.selectionScore
    }
}

enum class RPSResult(val score: Int) {
    WIN(6), DRAW(3), LOSE(0)
}

enum class RPS(val selectionScore: Int) {

    // TIL: Can't model 'beats: RPS' here because 'SCISSORS' isn't init'd when you
    // define 'ROCK'. No problem for the others since the value is the n-1 entry.
    ROCK(1), PAPER(2), SCISSORS(3);

    companion object {
        fun fromOpponentChoice(c: String) = when (c) {
            "A" -> ROCK
            "B" -> PAPER
            "C" -> SCISSORS
            else -> error("Unknown RPS selection '$c'")
        }

        fun fromMyChoice(c: String) = when (c) {
            "X" -> ROCK
            "Y" -> PAPER
            "Z" -> SCISSORS
            else -> error("Unknown RPS selection '$c'")
        }
    }
}