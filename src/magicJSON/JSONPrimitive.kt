package magicJSON

class JSONPrimitive(val key: String, val value: Any? = null) : JSONItem() {

    fun JSONString(key: String, string: String): String {
        return if (key != "") "\"$key\":\"$string\"" else "\"$string\""
    }

    fun JSONNumber(key: String, number: Any): String {
        return "\"$key\":$number"
    }

    fun JSONBoolean(key: String, boolean: Boolean): String {
        return if (boolean) "\"$key\": \"true\"" else "\"$key\": \"false\""
    }

    fun JSONNull(key: String): String {
        return "\"$key\":\"null\""
    }

    override fun generateJSON(): String {
        val sb = StringBuilder()
        if (value != null) {
            when (value) {
                is String -> sb.append(JSONString(key, value))
                is Boolean -> sb.append(JSONBoolean(key, value))
                is Int -> sb.append(JSONNumber(key, value))
                is Float -> sb.append(JSONNumber(key, value))
                is Double -> sb.append(JSONNumber(key, value))
                is Char -> sb.append(JSONString(key, value as String))
                else -> {
                    print("No match found")
                }
            }
        } else
            sb.append(JSONNull(key))
        return "$sb"
    }

    override fun accept(v: JSONVisitor) {
        v.visitJSONPrimitive(this)
    }
}