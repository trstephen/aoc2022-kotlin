package day07

import java.io.File

// Trees with parent refs are kind of a PITA to do with Java/Kotlin's 'pass-by-value'
// so we're going to process the input into a map of file path -> size and then
// interrogate that structure.
typealias DirMap = Map<String, Int>

// Categorize each _relevant_ line of the puzzle input
sealed class TermOutput {

    data class FileInfo(val size: Int, val name: String) : TermOutput()
    // We don't need to do anything with the 'dir X' output. CD will create new dirs.
    data class CD(val dirName: String) : TermOutput()
    object LS : TermOutput() {
        override fun toString() = "ls"
    }

    companion object {
        fun fromString(s: String): TermOutput? = when {
            s == "$ ls" -> LS
            s.startsWith("$ cd") -> CD(dirName = s.split(" ").last())
            s.matches("""^\d+ .+""".toRegex()) -> {
                val (size, name) = s.split(" ")
                FileInfo(size.toInt(), name)
            }
            else -> null
        }
    }
}

object Day07 {

    @JvmStatic
    fun main(args: Array<String>) = part1()

    val input = File("src/day07/input.txt").readLines()

    fun part1() {
        val output = input.mapNotNull { TermOutput.fromString(it) }
        val dirMap = processTermOutput(output)
        println(dirMap.values.filter { it < 100_000 }.sum())
    }

    tailrec fun processTermOutput(
        output: List<TermOutput>,
        currPath: List<String> = emptyList(),
        dirMapAcc: DirMap = emptyMap()
    ): DirMap {
        val nextOutput = output.firstOrNull() ?: return dirMapAcc
        val rp = RecursionPal(output, currPath, dirMapAcc)

        val nextRp = when (nextOutput) {
            is TermOutput.CD -> doCD(rp, nextOutput.dirName)
            is TermOutput.LS -> doLS(rp)
            is TermOutput.FileInfo -> error("You goofed, should have handled files in LS")
        }

        return processTermOutput(
            output = nextRp.output,
            currPath = nextRp.currPath,
            dirMapAcc = nextRp.dirMapAcc,
        )
    }

    fun doCD(rp: RecursionPal, dirName: String) = RecursionPal(
        output = rp.output.drop(1),
        currPath = when (dirName) {
            "/" -> listOf("root")
            ".." -> rp.currPath.dropLast(1)
            else -> rp.currPath + dirName
        },
        dirMapAcc = rp.dirMapAcc,
    )

    fun doLS(rp: RecursionPal): RecursionPal {
        val fileOutput = rp.output.drop(1)
            .takeWhile { it is TermOutput.FileInfo }
            .map { it as TermOutput.FileInfo } // the takeWhile doesn't let the compiler know this is a FileInfo

        // For a single 'level'
        val allFileSize = fileOutput.sumOf { it.size }

        // Add total to all dirs 'below' us. 'runningFold' creates the list of paths/keys
        val updatedDirMap = rp.currPath
            .drop(1)
            .runningFold(rp.currPath.first()) { acc, curr -> "$acc/$curr" }
            .associateWith { p -> (rp.dirMapAcc[p] ?: 0) + allFileSize }

        return RecursionPal(
            output = rp.output.drop(1 + fileOutput.size),
            currPath = rp.currPath,
            dirMapAcc = rp.dirMapAcc + updatedDirMap,
        )
    }

    data class RecursionPal(val output: List<TermOutput>, val currPath: List<String>, val dirMapAcc: DirMap)
}