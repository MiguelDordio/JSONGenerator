package magicJSON

import kotlin.reflect.KClass
import kotlin.reflect.full.declaredMemberProperties

class JSONObject(val key: String?, val value: Any? = null) : JSONItem() {

    private var elements: MutableList<Element> = mutableListOf()

    private fun identify(value: Any) {
        value::class.declaredMemberProperties.forEach {
            if ((it.returnType.classifier as KClass<*>).annotations.isNotEmpty()) {
                val node = JSONObject(it.name, it.getter.call(value))
                elements.add(node)
            }else if (it.returnType.classifier == List::class && it.getter.call(value) != null) {
                val jsonArray = JSONArray(it.name, it.getter.call(value) as MutableList<Any>)
                elements.add(jsonArray)
            } else {
                val leaf = JSONPrimitive(it.name, it.getter.call(value))
                elements.add(leaf)
            }
        }
    }

    override fun generateJSON(): String? {
        TODO("Not yet implemented")
    }

    override fun accept(v: JSONVisitor) {
        if (key != null && value != null) {
            identify(value)
        }
        if (v.visitJSONObject(this))
            elements.forEach {
                it.accept(v)
                if (it is JSONObject) v.visitExitJSONObject()
                if (it is JSONArray) v.visitExitJSONArray()
            }
    }


}