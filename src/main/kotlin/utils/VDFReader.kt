package utils

import org.json.JSONObject
import org.mozilla.universalchardet.UniversalDetector
import java.io.File
import java.nio.charset.Charset
import java.util.*
import kotlin.io.path.Path

/**
 * Object for reading VDF (Valve Data Format) files.
 *
 * **All the keys are converted to lowercase, because of cross-file case difference.**
 *
 */
object VDFReader {
    private const val BOM = "\uFEFF"
    private const val VDF_Line_Pattern = "^(\\s*\"[^\"]+\"\\s*\"[^\"]*((?:\\\\\")?[^\"]*(?:\\\\\")?)*[^\"]*\"?)[^\"]*(?://.*)*\$"

    /**
     * Inspects a file and tries to define its encoding.
     * @return defined [Charset]
     */
    fun getEncoding(f: File): Charset {
        return Charset.forName(UniversalDetector.detectCharset(f), Charsets.UTF_8)
    }

    /**
     * Reads a VDF file by given name.
     * @param src name of the VDF file.
     * @return JSONObject representation of the file.
     */
    fun read(src: String): JSONObject {
        val uri = Path(src).toAbsolutePath().toUri()
        return read(File(uri))
    }

    /**
     * Reads a VDF file.
     * @param src VDF file.
     * @return JSONObject representation of the file.
     */
    fun read(src: File): JSONObject {
        if (!src.exists()) {
            throw Exception("Source file does not exist")
        }
        if (!src.isFile) {
            throw Exception("Source path is not a file")
        }
        return parse(src)
    }

    /**
     * Builds a JSON object based on VDF file.
     *
     * Keys are converted to lowercase.
     *
     * Key duplicates are accumulated to one key with an array of values.
     *
     * @param src VDF file.
     * @return JSONObject representation of the file.
     */
    private fun parse(src: File): JSONObject {
        val json = JSONObject()

        val keyChain = Stack<String>()
        var prevK = ""
        var lineNumber = 0
        for (line in src.bufferedReader(getEncoding(src)).lines()) {
            ++lineNumber
            if (line.isBlank()) continue

            val trimmed = line.replace("\\s+".toRegex(), " ").trim().replace(BOM, "")

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
                    val match = Regex(VDF_Line_Pattern).matchEntire(trimmed)
                    if (match != null) {
                        val kv = match.groups[1]!!.value.split("\" *\"".toRegex())
                        try {
                            // lowercase is needed, because the same keys can have different case in different files
                            val k = kv[0].drop(1).lowercase()
                            val tempV = kv[1].dropLast(1)
                            // if the value starts with '#' - it is a key in another table (so again, convert it to lowercase)
                            val v = if (tempV.startsWith("#")) tempV.lowercase() else tempV
                            json.getByKeyChain(keyChain).accumulate(k, v)
                            prevK = k
                        } catch (_: Exception) {
                            throw Exception("Invalid format at (${src.name}:$lineNumber)")
                        }
                    } else {
                        throw Exception("Invalid format at (${src.name}:$lineNumber)")
                    }
                }
            }
        }
        if (keyChain.size != 0) throw Exception("Invalid format of braces")
        return json
    }
}
