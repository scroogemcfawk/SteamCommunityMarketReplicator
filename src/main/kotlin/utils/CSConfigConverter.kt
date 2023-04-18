package utils

import java.io.File
import java.io.FileNotFoundException
import java.util.Stack
import kotlin.Exception


private const val DEFAULT_SOURCE = "D:/SteamLibrary/steamapps/common/Counter-Strike Global Offensive/csgo/scripts/items/items_game" +
        ".txt"
private const val DEFAULT_TARGET = "C:/Users/scroo/IdeaProjects/RePricerDBM/src/main/resources/game/items_game.json"


/**
 * Converts items_game.txt CS:GO config file to json format.
 */
class CSConfigConverter(
    source: String = DEFAULT_SOURCE,
    target: String = DEFAULT_TARGET,
) {
    private val fr: File
    private val to: File

    init {
        fr = File(source)
        if (!fr.exists()) throw FileNotFoundException()
        to = File(target)
    }

    private val tab = "\t"

    fun run() {
        // TODO: optimize with StringBuilder? because long concat and current time: 2m 5s
        val stack = Stack<HashMap<String, Int>>()
        stack.add(HashMap())
        var res = ""
        res += "{\n"
        var cl = 1
        for (line in fr.bufferedReader().lines()) {
            val qcount = line.count { it == '\"' }

            when (qcount) {
                0 -> {
                    if (line.trim() == "{") {
                        ++cl
                        res += "{\n"
                        stack.add(HashMap())
                    }
                    if (line.trim() == "}") {
                        --cl
                        if (res.takeLast(2) == ",\n") {
                            res = res.dropLast(2) + "\n"
                        }
                        res += tab.repeat(cl) + "}" + if (cl > 1) ",\n" else "\n"
                        stack.pop()
                    }
                }

                2 -> {
                    val ks = stack.peek()
                    val key = line.trim().replace("\"", "")
                    val c = ks[key] ?: 0
                    ks[key] = c + 1
                    res += "${tab.repeat(cl)}\"$key${if (c > 0) c else ""}\": "
                }

                4 -> {
                    val ks = stack.peek()
                    val (k, v) = line.trim().split("\t\t").map { it.replace("\"", "") }
                    val c = ks[k] ?: 0
                    ks[k] = c + 1

                    res += tab.repeat(cl) + "\"$k${if (c > 0) c else ""}\": \"$v\",\n"
                }

                else -> {
                    throw Exception("Wrong number of values.")
                }
            }
        }
        res += "}\n"
        to.printWriter().use { it.println(res) }
    }
}

fun main() {
    val c = CSConfigConverter()
    c.run()
}