class JSONArray {

    private val elements = mutableListOf<JSONObject>()

    fun add(item: JSONObject) {
        elements.add(item)
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
