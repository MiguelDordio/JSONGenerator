package magicJSON

class JSONPrimitive(val key: String, val value: Any? = null) : Element {

    fun jsonChar(key: String, character: Char): String {
        return if (key != "") "\"$key\":\"$character\"" else "\"$character\""
    }

    fun jsonString(key: String, string: String): String {
        return if (key != "") "\"$key\":\"$string\"" else "\"$string\""
    }

    fun jsonNumber(key: String, number: Any): String {
        return "\"$key\":$number"
    }

    fun jsonBoolean(key: String, boolean: Boolean): String {
        return if (boolean) "\"$key\":\"true\"" else "\"$key\":\"false\""
    }

    fun jsonNull(key: String): String {
        return "\"$key\":\"null\""
    }

    fun jsonObject(key: String, value: JSONObject): String {
        return if (value.value == null) "\"" + key.capitalize() + "\"" + ":\"null\"" else "\"" + key.capitalize() + "\"" + ":"
    }

    override fun accept(v: JSONVisitor) {
        if (v.visitJSONPrimitive(this)) {
            if (value is JSONObject)
                value.accept(v)
        }
    }
}