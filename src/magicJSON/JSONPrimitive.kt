package magicJSON

class JSONPrimitive(val key: String, val value: Any? = null) : JSONItem() {

    private fun jsonChar(key: String, character: Char): String {
        return if (key != "") "\"$key\":\"$character\"" else "\"$character\""
    }

    private fun jsonString(key: String, string: String): String {
        return if (key != "") "\"$key\":\"$string\"" else "\"$string\""
    }

    private fun jsonNumber(key: String, number: Any): String {
        return "\"$key\":$number"
    }

    private fun jsonBoolean(key: String, boolean: Boolean): String {
        return if (boolean) "\"$key\":\"true\"" else "\"$key\":\"false\""
    }

    private fun jsonNull(key: String): String {
        return "\"$key\":\"null\""
    }

    override fun generateJSON(): String {
        val sb = StringBuilder()
        if (value != null) {
            when (value) {
                is String -> sb.append(jsonString(key, value))
                is Boolean -> sb.append(jsonBoolean(key, value))
                is Int -> sb.append(jsonNumber(key, value))
                is Float -> sb.append(jsonNumber(key, value))
                is Double -> sb.append(jsonNumber(key, value))
                is Char -> sb.append(jsonChar(key, value))
                else -> {
                    print("No match found")
                }
            }
        } else
            sb.append(jsonNull(key))
        return "$sb"
    }

    override fun accept(v: JSONVisitor) {
        v.visitJSONPrimitive(this)
    }
}