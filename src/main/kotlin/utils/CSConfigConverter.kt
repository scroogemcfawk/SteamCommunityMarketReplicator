package utils

import org.json.JSONObject
import java.io.File
import java.io.FileNotFoundException
import java.nio.charset.Charset
import java.util.Stack
import kotlin.Exception

data class ConvertorOptions(var source: String, var target: String, var encoding: Charset) {
    fun setDefaultItemsSource() {
        source = System.getenv("CSITEMS")
        encoding = Charsets.UTF_8
    }

    fun setDefaultEnglishSource() {
        source = System.getenv("CSENGLISH")
        encoding = Charsets.UTF_16
    }
}


private val DEFAULT_SOURCE = System.getenv("CSITEMS")
private val DEFAULT_TARGET = System.getProperty("user.dir") + "/src/main/resources/game/items_game.json"

/**
 * Converts items_game.txt CS:GO config file to json format.
 */
class CSConfigConverter(source: String = "", dest: String = "", private val sourceEncoding: Charset) {
    val src: File
    val tar: File

    init {
        src = File(source).takeIf { it.exists() && it.isFile } ?: File(DEFAULT_SOURCE).takeIf { it.exists() && it.isFile }
                ?: throw FileNotFoundException("Config source file not found.")
        tar = File(dest).takeIf { dest.isNotBlank() } ?: File(DEFAULT_TARGET).takeIf {
            File(
                it.absolutePath.split("\\").dropLast(1).joinToString("\\")
            ).isDirectory
        } ?: File("./items_game.json")
    }

    /**
     * Run conversion.
     */
    fun run() {
        val json = JSONObject()
        val keyChain = Stack<String>()
        var prevK =""
        for (line in src.bufferedReader(sourceEncoding).lines()) {
            var trimmed = line.trim().split(" ", "\t", "\\n").filter { it.isNotBlank() }.joinToString(" ")
            while (trimmed.contains("\" ")) {
                trimmed = trimmed.replace("\" ", "\"")
            }
            if (trimmed.startsWith("//")) {
                continue
            }
            when (trimmed.count { it == '\"' }) {
                0 -> {
                    if (trimmed == "}") {
                        keyChain.pop()
                    }
                }

                1 -> {
                    val obj = json.getByKeyChain(keyChain)
                    obj.accumulate(prevK, trimmed.replace("\"", ""))
                }

                2 -> {
                    val k = trimmed.replace("\"", "")
                    val obj = json.getByKeyChain(keyChain)
                    obj.putIfAbsent(k, JSONObject())
                    keyChain.add(k)
                }

                else -> {
                    if (trimmed.contains("\"\"")) {
                        val kv = trimmed.split("\"\"")
                        val k = kv[0].drop(1)
                        val v = kv[1].dropLast(1)
                        val obj = json.getByKeyChain(keyChain)
                        obj.accumulate(k, v)
                        prevK = k
                    } else {
                        throw Exception("Wrong number of values.")
                    }
                }
            }
        }
        tar.printWriter().use { it.println(json.toString()) }
    }

    fun getSource(): String {
        return src.absolutePath
    }

    fun getTarget(): String {
        return tar.absolutePath
    }
}

fun main() {
    val c = CSConfigConverter(
        "C:\\Users\\scroo\\IdeaProjects\\PriceStalkerDBM\\src\\main\\resources\\temp\\translate.txt",
        "C:\\Users\\scroo\\IdeaProjects\\PriceStalkerDBM\\src\\main\\resources\\game\\csgo_english.json",
        Charsets.UTF_16
    )
    c.run()
}