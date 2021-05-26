package magicJSON

class JSONArray(val raw: Any? = null, val isMap: Boolean) : Element {

    private var elementsList = mutableListOf<Element>()
    private var elements = mutableMapOf<String, Element>()

    init {
        if (raw != null) {
            if (isMap) {
                @Suppress("UNCHECKED_CAST")
                elements = raw as MutableMap<String, Element>
            }else {
                @Suppress("UNCHECKED_CAST")
                elementsList = raw as MutableList<Element>
            }
        }
    }


    override fun accept(v: JSONVisitor) {
        if (v.visitJSONArray(this, isMap)) {
            if (isMap) {
                elements.forEach { (key, value) ->
                    if (key != "")
                        v.visitInnerJSONObject(key)
                    value.accept(v)
                }
            } else {
                elementsList.forEach {
                    it.accept(v)
                }
            }
            v.visitExitJSONArray(this)
        }
    }
}