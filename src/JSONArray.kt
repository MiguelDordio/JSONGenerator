class JSONArray {

    val elements = mutableListOf<JSONObject>()

    fun add(value: JSONObject?): JSONArray? {
        if (value != null) {
            elements.add(value)
        } else {
            throw IllegalArgumentException("Value must be JSONObject, was blah")
        }
        return this
    }

    override fun toString(): String {
        val sb = StringBuilder()
        var first = true
        elements.forEach {
            if (first) first = false else sb.append(",")
            sb.append(it.toString())
        }
        return "[$sb]"
    }
}
