import org.json.JSONObject
import java.io.File

fun main() {
    val items = JSONObject(File("C:\\Users\\scroo\\IdeaProjects\\PriceStalkerDBM\\src\\main\\resources\\temp\\items_game.json").readText())
    (items["items_game"] as JSONObject).keySet().forEach { println(it) }
}