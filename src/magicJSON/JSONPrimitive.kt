package magicJSON

class JSONPrimitive(val key: String, val value: Any? = null) : Element {

    override fun accept(v: JSONVisitor) {
        if (v.visitJSONPrimitive(this)) {
            if (value is JSONObject)
                value.accept(v)
        }
    }

    override fun isEmpty(): Boolean {
        return value == null
    }

    override fun toString(): String {
        return "$value"
    }


}