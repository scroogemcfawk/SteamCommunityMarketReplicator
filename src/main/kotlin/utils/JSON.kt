package utils

import org.json.JSONObject

/**
 * Returns JSONObject by applying .get(key) to this from keyChain one by one.
 */
fun JSONObject.getByKeyChain(keyChain: Collection<String>): JSONObject {
    var ret = this
    for (key in keyChain) {
        ret = ret[key] as JSONObject
    }
    return ret
}

/**
 * Skips put value if this contains key.
 */
fun JSONObject.putIfAbsent(key: String, value: Any) {
    if (!this.has(key)) this.put(key, value)
}