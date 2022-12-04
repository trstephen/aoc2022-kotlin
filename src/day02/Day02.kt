package day02

import java.io.File

object Day02 {

    @JvmStatic
    fun main(args: Array<String>) = part2()

    fun part1() {
        val strat = readStrategy("src/day02/part01-input.txt") { arg1, arg2 ->
            RPSStrategy1(them = RPS.fromOpponentChoice(arg1), me = RPS.fromMyChoice(arg2))
        }
        println(strat.sumOf { it.score() }) // 11603
    }

    fun part2() {
        val strat = readStrategy("src/day02/part01-input.txt") { arg1, arg2 ->
            RPSStrategy2(them = RPS.fromOpponentChoice(arg1), result = RPSResult.fromString(arg2))
        }
        println(strat.sumOf { it.score() }) // 12725
    }

    fun <T: RPSStrategy> readStrategy(filename: String, block: (arg1: String, arg2: String) -> T): List<T> =
        File(filename).readLines().map {
            val args = it.split(" ")
            block(args[0], args[1])
        }
}

interface RPSStrategy {
    fun score(): Int
}

data class RPSStrategy1(val them: RPS, val me: RPS) : RPSStrategy {
    override fun score(): Int {
        val result = when {
            them == me -> RPSResult.DRAW
            them.losesTo() == me -> RPSResult.WIN
            else -> RPSResult.LOSE
        }

        return result.score + me.selectionScore
    }
}

data class RPSStrategy2(val them: RPS, val result: RPSResult) : RPSStrategy {

    override fun score(): Int {
        val myRPS = when (result) {
            RPSResult.WIN -> them.losesTo()
            RPSResult.DRAW -> them
            RPSResult.LOSE -> them.beats()
        }

        return myRPS.selectionScore + result.score
    }
}

enum class RPSResult(val score: Int) {
    WIN(6), DRAW(3), LOSE(0);

    companion object {
        fun fromString(s: String) = when (s) {
            "X" -> LOSE
            "Y" -> DRAW
            "Z" -> WIN
            else -> error("Unknown RPSResult selection '$s")
        }
    }
}

enum class RPS(val selectionScore: Int) {

    // TIL: Can't model 'beats: RPS' here because 'SCISSORS' isn't init'd when you
    // define 'ROCK'. No problem for the others since the value is the n-1 entry.
    // Work around this with 'beats()' and 'losesTo()'.
    ROCK(1), PAPER(2), SCISSORS(3);

    fun beats() = when (this) {
        ROCK -> SCISSORS
        PAPER -> ROCK
        SCISSORS -> PAPER
    }

    fun losesTo() = when (this) {
        ROCK -> PAPER
        PAPER -> SCISSORS
        SCISSORS -> ROCK
    }

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