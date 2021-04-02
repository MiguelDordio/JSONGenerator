class JSONObject() : Visitor {

    val components = hashMapOf<String, Any?>()

    fun add(key: String, value: Any?): JSONObject {
        if (value != null && (value is String || value is Boolean || value is Int || value is Float || value is JSONArray || value is JSONObject)) {
            this.components.put(key, value)
        }else if (value == null) {
            this.components.put(key, "null")
        } else {
            throw IllegalArgumentException(
                "Value must be either String, Boolean, Integer or Float, was "
            )
        }
        return this
    }

    override fun JSONTransform(key: String, string: String): String {
        return "\"$key\":\"$string\""
    }

    override fun JSONTransform(key: String, number: Any): String {
        return "\"$key\":$number"
    }

    override fun JSONTransform(key: String, boolean: Boolean): String {
        return if (boolean) "\"$key\": \"true\"" else "\"$key\": \"false\""
    }

    override fun toString(): String {
        val sb = StringBuilder()
        var first = true
        components.forEach {
            if (first) first = false else sb.append(",")
            val temp = it.value
            when (temp) {
                is String -> sb.append(JSONTransform(it.key, temp))
                is Boolean -> sb.append(JSONTransform(it.key, temp))
                is Int -> sb.append(JSONTransform(it.key, temp))
                is Float -> sb.append(JSONTransform(it.key, temp))
                is JSONArray -> sb.append("\"").append(it.key).append("\":").append((temp).toString())
                is JSONObject -> sb.append("\"").append(it.key).append("\":").append(temp.toString())
                else -> {
                    print("No match found")
                }
            }
        }
        return "{$sb}"
    }

    fun prettyPrintJSON(unformattedJsonString: String): String? {
        val prettyJSONBuilder = java.lang.StringBuilder()
        var indentLevel = 0
        var inQuote = false
        for (charFromUnformattedJson in unformattedJsonString.toCharArray()) {
            when (charFromUnformattedJson) {
                '"' -> {
                    // switch the quoting status
                    inQuote = !inQuote
                    prettyJSONBuilder.append(charFromUnformattedJson)
                }
                ' ' ->         // For space: ignore the space if it is not being quoted.
                    if (inQuote) {
                        prettyJSONBuilder.append(charFromUnformattedJson)
                    }
                '{', '[' -> {
                    // Starting a new block: increase the indent level
                    prettyJSONBuilder.append(charFromUnformattedJson)
                    indentLevel++
                    appendIndentedNewLine(indentLevel, prettyJSONBuilder)
                }
                '}', ']' -> {
                    // Ending a new block; decrese the indent level
                    indentLevel--
                    appendIndentedNewLine(indentLevel, prettyJSONBuilder)
                    prettyJSONBuilder.append(charFromUnformattedJson)
                }
                ',' -> {
                    // Ending a json item; create a new line after
                    prettyJSONBuilder.append(charFromUnformattedJson)
                    if (!inQuote) {
                        appendIndentedNewLine(indentLevel, prettyJSONBuilder)
                    }
                }
                else -> prettyJSONBuilder.append(charFromUnformattedJson)
            }
        }
        return prettyJSONBuilder.toString()
    }

    private fun appendIndentedNewLine(indentLevel: Int, stringBuilder: java.lang.StringBuilder) {
        stringBuilder.append("\n")
        for (i in 0 until indentLevel) {
            // Assuming indention using 2 spaces
            stringBuilder.append("  ")
        }
    }
}
