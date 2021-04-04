package magicJSON

class JSONInspector : JSONVisitor {

    private val components = hashMapOf<String, Any?>()
    private var jsonText = StringBuilder()
    private var firstObject: Boolean = true

    private val allStrings = mutableListOf<String>()

    /**
     * Core methods
     */

    override fun visitJSONObject(node: JSONObject): Boolean {
        if (firstObject) firstObject = false
        else {
            if (node.key != "") jsonText.append("\"${node.key?.capitalize()}\":{")
            else jsonText.append("{")
        }
        node.key?.let { components.put(it, node.value) }
        return true
    }

    override fun visitExitJSONObject(): Boolean {
        jsonText.setLength(jsonText.length - 1)
        jsonText.append("},")
        return true
    }

    override fun visitJSONPrimitive(node: JSONPrimitive): Boolean {
        jsonText.append(node.generateJSON() + ",")
        node.key.let { components.put(it, node) }

        if (node.value is String)
            allStrings.add(node.value)

        return true
    }

    override fun visitJSONArray(node: JSONArray): Boolean {
        jsonText.append("\"${node.key}\":[")
        node.key.let { components.put(it, node.itemsList) }
        return true
    }

    override fun visitExitJSONArray(): Boolean {
        jsonText.setLength(jsonText.length - 1)
        jsonText.append("],")
        return true
    }

    fun objectToJSON(obj: Any): String {
        val node = JSONObject("", obj)
        node.accept(this)
        jsonText.setLength(jsonText.length - 1)
        return if(components.size > 1) "{$jsonText}" else "$jsonText"
    }

    fun objectToJSONPrettyPrint(obj: Any): String {
        val node = JSONObject("", obj)
        node.accept(this)
        jsonText.setLength(jsonText.length - 1)
        val finalJSONText: String = if(components.size > 1) "{$jsonText}" else "$jsonText"
        return prettyPrintJSON(finalJSONText)
    }

    private fun prettyPrintJSON(unformattedJsonString: String): String {
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

    /**
     * Misc methods
     */

    fun getAllStrings(): MutableList<String> {
        return allStrings
    }
}