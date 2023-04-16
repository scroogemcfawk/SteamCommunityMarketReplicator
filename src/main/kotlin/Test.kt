import java.io.File
import org.json.JSONObject as JO
import

class Test {
}


// https://steamcommunity.com/market/search/render/?search_descriptions=0&sort_column=default&sort_dir=desc&appid=730&norender=1&count=500
// https://community.cloudflare.steamstatic.com/economy/image/ICON_URL/RESfxRESf
fun main() {
    val text = JO(File("C:\\Users\\scroo\\IdeaProjects\\RePricerDBM\\src\\main\\resources\\response.json").readText())
    for (r in text.getJSONArray("results")) {
        val o = JO(r.toString())
        val name = o["name"]
        val hash_name = o["hash_name"]
        val market_name = JO(o["asset_description"].toString())["market_name"]
        val market_hash_name = JO(o["asset_description"].toString())["market_hash_name"]
        if (!(name == hash_name && name == market_name && name == market_hash_name)) {
            println(name)
        }
    }
}