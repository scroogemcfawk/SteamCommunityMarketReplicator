package utils

import org.json.JSONObject
import java.io.File
import java.io.FileNotFoundException
import java.util.Stack
import kotlin.Exception


private val DEFAULT_SOURCE = System.getenv("CSITEMS")
private val DEFAULT_TARGET = System.getProperty("user.dir") + "/src/main/resources/game/items_game.json"


/**
 * Converts items_game.txt CS:GO config file to json format.
 */
class CSConfigConverter(source: String = "", dest: String = "") {
    val fr: File
    val to: File

    init {
        fr = File(source).takeIf { it.exists() && it.isFile } ?: File(DEFAULT_SOURCE).takeIf { it.exists() && it.isFile }
                ?: throw FileNotFoundException("Config source file not found.")
        to = File(dest).takeIf { dest.isNotBlank() } ?: File(DEFAULT_TARGET).takeIf {
            File(
                it.absolutePath.split("\\").dropLast(1).joinToString("\\")
            ).isDirectory
        } ?: File("./items_game.json")
    }

    fun run() {
        val json = JSONObject()
        val keyChain = Stack<String>()
        for (line in fr.bufferedReader().lines()) {
            when (line.count { it == '\"' }) {
                0 -> {
                    if (line.trim() == "}") {
                        keyChain.pop()
                    }
                }

                2 -> {
                    val k = line.trim().replace("\"", "")
                    val obj = json.getByKeyChain(keyChain)
                    obj.putIfAbsent(k, JSONObject())
                    keyChain.add(k)
                }

                4 -> {
                    val (k, v) = line.trim().split("\t\t").map { it.replace("\"", "") }
                    val obj = json.getByKeyChain(keyChain)
                    obj.accumulate(k, v)
                }

                else -> {
                    throw Exception("Wrong number of values.")
                }
            }
        }
        to.printWriter().use { it.println(json.toString()) }
    }
}

fun main() {
    val c = CSConfigConverter()
    c.run()
}