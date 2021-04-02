class JSONArray(val elements: MutableList<*>) {

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
