package magicJSON

import kotlin.reflect.full.isSubclassOf

class JSONArray(val key: String, private val rawList: MutableList<Any>? = null, private val isMap: Boolean) : Element {

    val itemsList = mutableListOf<Element>()

    private fun identify() {
        rawList?.forEach {
            if (it::class == String::class || it::class == Int::class ||
                it::class == Double::class || it::class == Float::class ||
                it::class == Boolean::class || it::class == Char::class) {
                val jsonPrimitive = JSONPrimitive("", it)
                itemsList.add(jsonPrimitive)
            } else if (it is JSONPrimitive) {
                itemsList.add(it)
            } else if (it::class.isSubclassOf(Enum::class)) {
                val jsonPrimitive = JSONPrimitive("", it.toString())
                itemsList.add(jsonPrimitive)
            } else {
                val jsonObject = JSONObject(it)
                itemsList.add(jsonObject)
            }
        }
    }

    override fun accept(v: JSONVisitor) {
        if (v.visitJSONArray(this, isMap)) {
            identify()
            itemsList.forEach {
                it.accept(v)
            }
            v.visitExitJSONArray()
        }
    }
}