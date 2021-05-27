package magicJSON

class JSONObject(val elements: MutableMap<String, Element>? = null) : Element {

    /**
     * Adds an Element to the JSONObject.
     */
    fun accumulate(name: String, value: Element) {
        elements?.set(name, value)
    }

    override fun isEmpty(): Boolean {
        return elements?.isEmpty() ?: true
    }


    /**
     * Iterates the JSONObject to create the corresponding json string
     */
    override fun accept(v: JSONVisitor) {
        if (v.visitJSONObject(this)) {
            elements?.forEach {
                if (it.value is JSONObject)
                    v.visitInnerJSONObject(it.key)
                else if (it.value is JSONArray)
                    v.visitInnerJSONArray(it.key)
                it.value.accept(v)
            }
        }
        v.visitExitJSONObject(this)
    }
}