package magicJSON

import kotlin.reflect.KClass
import kotlin.reflect.full.isSubclassOf

class JSONArray(val key: String, private val rawList: MutableList<Any>, val isMap: Boolean) : JSONItem() {

    val itemsList = mutableListOf<Element>()

    private fun identify() {
        rawList.forEach {
            if (it::class == String::class || it::class == Int::class ||
                    it::class == Double::class || it::class == Float::class ||
                    it::class == Boolean::class || it::class == Char::class) {
                val jsonPrimitive = JSONPrimitive("", it)
                itemsList.add(jsonPrimitive)
            } else if (it is JSONObject) {
                itemsList.add(it)
            } else if ((it as KClass<out Any>).isSubclassOf(Enum::class)) {
                val jsonPrimitive = JSONPrimitive("", it.toString())
                itemsList.add(jsonPrimitive)
            } else {
                val jsonObject = JSONObject("", it)
                itemsList.add(jsonObject)
            }
            /*
            if(it::class.annotations.isNotEmpty()) {
                val jsonObject = JSONObject("", it)
                itemsList.add(jsonObject)
            } else if(it is JSONObject) {
                itemsList.add(it)
            } else {
                val jsonPrimitive = JSONPrimitive("", it)
                itemsList.add(jsonPrimitive)
            }

             */
        }
    }

    override fun generateJSON(): String? {
        TODO("Not yet implemented")
    }

    override fun accept(v: JSONVisitor) {
        identify()
        if (v.visitJSONArray(this, isMap))
            itemsList.forEach {
                it.accept(v)
                if (it is JSONObject) v.visitExitJSONObject()
            }
    }
}