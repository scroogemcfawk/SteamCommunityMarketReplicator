package utils

import org.json.JSONObject
import org.mozilla.universalchardet.UniversalDetector
import java.io.File
import java.nio.charset.Charset
import java.util.*
import kotlin.io.path.Path

object VDFReader {
    private const val BOM = "\uFEFF"

    fun getEncoding(f: File): Charset {
        return Charset.forName(UniversalDetector.detectCharset(f), Charsets.UTF_8)
    }

    fun read(src: String): JSONObject {
        val uri = Path(src).toAbsolutePath().toUri()
        return read(File(uri))
    }

    fun read(src: File): JSONObject {
        if (!src.exists()) {
            throw Exception("Source file does not exist")
        }
        if (!src.isFile) {
            throw Exception("Source path is not a file")
        }
        return parse(src)
    }

    private fun parse(src: File): JSONObject {
        val json = JSONObject()

        val keyChain = Stack<String>()
        var prevK = ""
        var lineNumber = -1
        for (line in src.bufferedReader(getEncoding(src)).lines()) {
            ++lineNumber
            if (line.isBlank()) continue

            val trimmed = line.replace("\\s+".toRegex(), " ").trim().lowercase().replace(BOM, "")

            if (trimmed.startsWith("//")) continue

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
                    val kv = trimmed.split("\" *\"".toRegex())
                    try {
                        val k = kv[0].drop(1)
                        val v = kv[1].dropLast(1)
                        json.getByKeyChain(keyChain).accumulate(k, v)
                        prevK = k
                    } catch (_: Exception) {
                        throw Exception("Wrong format at (${src.name}:$lineNumber)")
                    }
                }
            }
        }
        if (keyChain.size != 0) throw Exception("Wrong format of braces")
        return json
    }
}
