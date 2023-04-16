import java.io.File


private val DEFAULT_SOURCE_PATH = "D:/SteamLibrary/steamapps/common/Counter-Strike Global Offensive/csgo/scripts/items/items_game" +
        ".txt"
private val DEFAULT_TARGET_PATH = "items_game.json"


/**
 * Converts items_game.txt CS:GO config file to json format.
 */
class CSConfigConverter(
    source: String = DEFAULT_SOURCE_PATH,
    target: String = DEFAULT_TARGET_PATH,
) {
    private val fr: File
    private val to: File
    init {
        fr = File(source)
        to = File(target)
    }

    private val tab = "\t"

    fun run() {
        // TODO: optimize with StringBuilder?
        var res = ""
        res += "{\n"
        var cl = 1
        for (line in fr.bufferedReader().lines()) {
            if (line.contains("\"")) {
                if (line.split("\"").size == 3) {
                    res += tab.repeat(cl) + line.trim() + ": "
                } else {
                    val (k, v) = line.trim().split("\t\t")
                    res += tab.repeat(cl) + "$k: $v,\n"
                }
            } else {
                if (line.trim() == "{") {
                    ++cl
                    res += "{\n"
                }
                if (line.trim() == "}") {
                    --cl
                    if (res.takeLast(2) == ",\n") {
                        res = res.dropLast(2) + "\n"
                    }
                    res += tab.repeat(cl) + "}" + if (cl > 1) ",\n" else "\n"
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