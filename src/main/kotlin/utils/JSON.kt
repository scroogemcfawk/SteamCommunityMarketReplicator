package utils

import org.json.JSONObject

fun JSONObject.getByKeyChain(keyChain: Collection<String>): JSONObject {
    var ret = this
    for (key in keyChain) {
        ret = ret[key] as JSONObject
    }
    return ret
}

fun JSONObject.putIfAbsent(key: String, value: Any) {
    if (!this.has(key)) this.put(key, value)
}