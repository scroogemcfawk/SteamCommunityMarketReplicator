import org.json.JSONObject
import utils.CSConfigConverter
import java.io.File
import java.sql.DriverManager

fun levenshtein(lhs: CharSequence, rhs: CharSequence): Int {
    val lhsLength = lhs.length
    val rhsLength = rhs.length

    var cost = Array(lhsLength) { it }
    var newCost = Array(lhsLength) { 0 }

    for (i in 1..rhsLength - 1) {
        newCost[0] = i

        for (j in 1..lhsLength - 1) {
            val match = if (lhs[j - 1] == rhs[i - 1]) 0 else 1

            val costReplace = cost[j - 1] + match
            val costInsert = cost[j] + 1
            val costDelete = newCost[j - 1] + 1

            newCost[j] = Math.min(Math.min(costInsert, costDelete), costReplace)
        }

        val swap = cost
        cost = newCost
        newCost = swap
    }

    return cost[lhsLength - 1]
}

fun main() {

    CSConfigConverter.run(CSConfigConverter.Options().setDefaultEnglishOpts())
    CSConfigConverter.run(CSConfigConverter.Options().setDefaultItemsOpts())

    val itemsJSON = JSONObject(
        File("C:\\Users\\scroo\\IdeaProjects\\PriceStalkerDBM\\src\\main\\resources\\temp\\items_game.json").readText()
    )
    val items = (itemsJSON["items_game"] as JSONObject)
    val keys = listOf("items")
    val a = items[keys[0]] as JSONObject

    val l = HashSet<String>()
    val exclude = listOf(
        "sfui", "quest", "campaign", "xpgrant", "subscription1", "storageunit", "spray_std", "License", "TradeUp",
        "FiveYearService", "GlobalGeneral"
    )
    val excludeIfHigher = listOf(Pair("CSGO_crate_key_community_", "CSGO_crate_key_community_22"), Pair("CSGO_Tool_", "CSGO_Tool_Name_Tag"))
    // TODO add 'dreams & nightmares case key'
    val reg = Regex("csgo_ticket_communityseason\\D+\\d*_|csgo_collectible_communityseason\\w+|[\\w]+collectiblecoin[\\w]+|[\\w]+maptoken" +
            "[\\w]+|[\\w]+tournamentjournal[\\w]+")
    keys@ for (key in a.keySet()) {
        val o = a[key] as JSONObject
        if (o.has("item_name")) {
            val item_name = o["item_name"].toString().replace("#", "")
            for (e in exclude) {
                if (e in item_name) {
                    continue@keys
                }
            }
            for (p in excludeIfHigher) {
                if (p.first in item_name && item_name > p.second) {
                    continue@keys
                }
            }
            if (reg.matches(item_name)) {
//                println("$key $item_name")
                continue@keys
            }
            l.add(item_name)
        }
    }
    val eng = (JSONObject(
        File("C:\\Users\\scroo\\IdeaProjects\\PriceStalkerDBM\\src\\main\\resources\\temp\\csgo_english.json").readText
            ()
    )["lang"] as JSONObject)["tokens"] as JSONObject

//    val jdbcURL = "jdbc:postgresql://localhost:5432/scm_test"
//    val user = "postgres"
//    val pass = "admin"
//    val connection = DriverManager.getConnection(jdbcURL, user, pass)

    println(l.joinToString("\n"))


    for (k in l.sorted()) {
//        if (eng.has(k) && (eng[k] == "axe")) {
//            println(k)
//        }
//        if (eng.has(k) && eng[k].toString().contains("for operation broken")) {
//            println(k)
//        }
//        if (reg.matches(k)) {
//            println(k)
//        }
//        if (eng.has(k)) {
//            val q = connection.prepareStatement("INSERT INTO Asset(name, app_id) VALUES (?, ?) ON CONFLICT DO NOTHING")
//            q.setString(1, "${eng[k]}")
//            q.setInt(2, 730)
//            try {
//                q.execute()
//            } catch (e: Exception) {
//                println(k)
//                println(q.toString())
//                break
//            }
//        }
    }
}