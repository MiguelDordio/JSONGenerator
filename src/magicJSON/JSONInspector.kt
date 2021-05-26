package magicJSON

class JSONInspector : JSONVisitor {

    var jsonText = StringBuilder()
    private var mapPrinting: Boolean = false
    private val allStrings = mutableListOf<String>()


    /***************************************************
     *                 Visitor methods
     ***************************************************/

    /**
     * Visits a JSONObject and creates corresponding json string element
     */
    override fun visitJSONObject(node: JSONObject): Boolean {
        if (node.elements != null)
            jsonText.append("{")
        else
            jsonText.append("\"null\",")
        return true
    }

    /**
     * Visits a JSONObject and creates corresponding json string element
     */
    override fun visitInnerJSONObject(key: String): Boolean {
        jsonText.append("\"" + key.capitalize() + "\"" + ":")
        return true
    }

    /**
     * Marks the exit of a JSONObject and creates corresponding json string element
     */
    override fun visitExitJSONObject(node: JSONObject): Boolean {
        if (node.elements != null) {
            jsonText.setLength(jsonText.length - 1)
            jsonText.append("},")
        }
        return true
    }

    /**
     * Visits a JSONArray and creates corresponding json string element
     */
    override fun visitInnerJSONArray(key: String): Boolean {
        jsonText.append("\"$key\":")
        return true
    }

    /**
     * Visits a JSONArray and creates corresponding json string element
     */
    override fun visitJSONArray(node: JSONArray, isMap: Boolean): Boolean {
        if (node.raw != null)
            if (isMap) {
                jsonText.append("{")
                mapPrinting = true
            } else jsonText.append("[")
        else
            jsonText.append("\"null\",")
        return true
    }

    /**
     * Marks the exit of a JSONObject and creates corresponding json string element
     */
    override fun visitExitJSONArray(node: JSONArray): Boolean {
        if (node.raw != null) {
            jsonText.setLength(jsonText.length - 1)
            if (mapPrinting) {
                jsonText.append("},")
                mapPrinting = false
            } else jsonText.append("],")
        }
        return true
    }

    /**
     * Visits a JSONPrimitive and creates corresponding json string element
     */
    override fun visitJSONPrimitive(node: JSONPrimitive): Boolean {
        jsonText.append(jsonPrimitiveMapper(node))
        return true
    }


    /***************************************************
     *       JSONPrimitive to JSON format methods
     ***************************************************/

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

    fun jsonPrimitiveMapper(node: JSONPrimitive) : String {
        val sb = StringBuilder()
        if (node.value!= null) {
            when (node.value) {
                is String -> { sb.append(jsonString(node.key, node.value) + ",")
                    allStrings.add(node.value)
                }
                is Boolean -> sb.append(jsonBoolean(node.key, node.value)+ ",")
                is Int -> sb.append(jsonNumber(node.key, node.value)+ ",")
                is Float -> sb.append(jsonNumber(node.key, node.value)+ ",")
                is Double -> sb.append(jsonNumber(node.key, node.value)+ ",")
                is Char -> sb.append(jsonChar(node.key, node.value)+ ",")
                else -> {
                    print("No match found")
                }
            }
        } else
            sb.append(jsonNull(node.key)+ ",")
        return sb.toString()
    }


    /***************************************************
     *           Object to JSON format methods
     ***************************************************/

    /**
     * Generates an object corresponding json string
     */
    fun objectToJSON(obj: Any): String {
        val jsonSerializer = JSONSerializer()
        val serializedObj = jsonSerializer.identify(obj)
        val node = JSONObject(serializedObj)
        node.accept(this)
        jsonText.setLength(jsonText.length - 1)
        return "$jsonText"
    }

    /**
     * Generates an object corresponding json string with a human read format
     */
    fun objectToJSONPrettyPrint(obj: Any): String {
        val jsonSerializer = JSONSerializer()
        val serializedObj = jsonSerializer.identify(obj)
        val node = JSONObject(serializedObj)
        node.accept(this)
        jsonText.setLength(jsonText.length - 1)
        return prettyPrintJSON("$jsonText")
    }

    /**
     * Takes a raw json string and makes it more readable
     */
    private fun prettyPrintJSON(noWhite: String): String {
        val sb = java.lang.StringBuilder()
        var tabCount = 0
        var inQuotes = false
        for (c in noWhite.toCharArray()) {
            if (c == '"') {
                inQuotes=!inQuotes
            }
            if (!inQuotes) {
                when (c) {
                    '{' -> {
                        sb.append(c)
                        sb.append(System.lineSeparator())
                        tabCount++
                        printTabs(sb, tabCount)
                    }
                    '}' -> {
                        sb.append(System.lineSeparator())
                        tabCount--
                        printTabs(sb, tabCount)
                        sb.append(c)
                    }
                    ',' -> {
                        sb.append(c)
                        sb.append(System.lineSeparator())
                        printTabs(sb, tabCount)
                    }
                    '[' -> {
                        sb.append(c)
                        sb.append("\n")
                        tabCount++
                        printTabs(sb, tabCount)
                    }
                    ']' -> {
                        sb.append("\n")
                        tabCount--
                        printTabs(sb, tabCount)
                        sb.append(c)
                    }
                    ':' -> {
                        sb.append(c)
                        sb.append(" ")
                    }
                    else -> {
                        sb.append(c)
                    }
                }
            } else {
                sb.append(c)
            }
        }
        return sb.toString()
    }

    private fun printTabs(sb: java.lang.StringBuilder, tabCount: Int) {
        for (i in 0 until tabCount) {
            sb.append('\t')
        }
    }


    /***************************************************
     *             Extra functionalities
     ***************************************************/

    /**
     * Returns all strings present in a given object
     */
    fun getAllStrings(obj: Any): MutableList<String> {
        val jsonSerializer = JSONSerializer()
        val serializedObj = jsonSerializer.identify(obj)
        val node = JSONObject(serializedObj)
        node.accept(this)
        return allStrings
    }

    fun treeItemToJSONPrettyPrint(obj: Element): String {
        obj.accept(this)
        jsonText.setLength(jsonText.length - 1)
        return prettyPrintJSON("$jsonText")
    }
}