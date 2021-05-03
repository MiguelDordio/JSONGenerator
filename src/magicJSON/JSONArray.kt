package magicJSON

import kotlin.reflect.full.isSubclassOf

class JSONArray(val rawList: Any? = null, private val isMap: Boolean) : Element {

    var elementsList = mutableListOf<Element>()
    var elements = mutableMapOf<String, Element>()

    private fun identify() {

        if (rawList != null) {
            if (rawList::class == ArrayList::class) {
                (rawList as ArrayList<*>).forEach {
                    if (it::class == String::class || it::class == Int::class ||
                        it::class == Double::class || it::class == Float::class ||
                        it::class == Boolean::class || it::class == Char::class) {
                        elementsList.add(JSONPrimitive("", it))
                    } else if (it::class.isSubclassOf(Enum::class)) {
                        elementsList.add(JSONPrimitive("", it.toString()))
                    } else {
                        elementsList.add(JSONObject(it))
                    }
                }
            } else if (rawList::class == LinkedHashMap::class) {
                (rawList as LinkedHashMap<*, *>).forEach { (key, value) ->
                    if (value::class == String::class || value::class == Int::class ||
                        value::class == Double::class || value::class == Float::class ||
                        value::class == Boolean::class || value::class == Char::class) {
                        elements[""] = JSONPrimitive("", value)
                    } else if (value::class.isSubclassOf(Enum::class)) {
                        elements[""] = JSONPrimitive("", value.toString())
                    } else {
                        if (key::class == String::class)
                            elements[key as String] = JSONObject(value)
                        else
                            // to deal with cases where the map`s key is also an object
                            // the object is converted into a simplified String
                            elements[key.toString()] = JSONObject(value)
                    }
                }
            }
        }
    }

    override fun accept(v: JSONVisitor) {
        if (v.visitJSONArray(this, isMap)) {
            identify()
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
            v.visitExitJSONArray()
        }
    }
}