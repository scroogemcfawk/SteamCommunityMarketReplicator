package utils

import org.json.JSONObject
import java.io.File
import java.nio.charset.Charset
import java.util.Stack
import kotlin.Exception

/**
 * Converts items_game.txt CS:GO config file to json format.
 */
@Deprecated("Ambiguous setup.", replaceWith = ReplaceWith("VDFReader"))
class CSConfigConverter {
    /**
     *  Class of CSConfigConverter options.
     *  @param source source file path
     *  @param target target file path
     *  @param encoding source file encoding
     */
    public class Options(var source: String = "", var target: String = "", var encoding: Charset = Charsets.UTF_8) {
        /**
         * Check if given paths exist.
         */
        private fun tryExist() {
            if (!File(source).exists()) {
                throw Exception("Source file not found.")
            }
            if (!File(target.split("/").dropLast(1).joinToString("/")).exists()) {
                throw Exception("Target directory not found.")
            }
        }

        /**
         *  Set default options for items_game.txt source file.
         */
        public fun setDefaultItemsOpts(): Options {
            source = System.getenv("CSITEMS").replace("\\", "/").ifBlank { throw Exception("Environment variable \"CSITEMS\" not found.") }
            target = System.getProperty("user.dir").replace("\\", "/") + "/src/main/resources/temp/items_game.json"
            encoding = Charsets.UTF_8
            tryExist()
            return this
        }

        /**
         *  Set default options for csgo_english.txt source file.
         */
        public fun setDefaultEnglishOpts(): Options {
            source = System.getenv("CSENGLISH") ?: throw Exception("Environment variable \"CSENGLISH\" not found.")
            target = System.getProperty("user.dir") + "/src/main/resources/temp/csgo_english.json"
            encoding = Charsets.UTF_16
            tryExist()
            return this
        }
    }

    companion object {
        /**
         * Run conversion.
         */
        fun run(opt: Options) {
            val src = File(opt.source)
            val tar = File(opt.target)
            val json = JSONObject()
            val keyChain = Stack<String>()
            var prevK = ""
            for (line in src.bufferedReader(opt.encoding).lines()) {
                var trimmed = line.trim().lowercase().split(" ", "\t", "\\n").filter { it.isNotBlank() }.joinToString(" ")
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
    }
}