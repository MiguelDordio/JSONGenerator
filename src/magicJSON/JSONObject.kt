package magicJSON

import kotlin.reflect.KClass
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.findAnnotation

class JSONObject(val key: String?, val value: Any? = null) : JSONItem() {

    private var elements: MutableList<Element> = mutableListOf()

    private fun identify(value: Any) {
        value::class.declaredMemberProperties.forEach {

            if (it.findAnnotation<JSONObjectItem>() != null ||
                    (it.returnType.classifier as KClass<*>).findAnnotation<JSONClass>() != null) {
                val node = JSONObject(it.name, it.getter.call(value))
                elements.add(node)
            } else if (it.returnType.classifier == List::class && it.getter.call(value) != null) {
                val jsonArray = JSONArray(it.name, it.getter.call(value) as MutableList<Any>, false)
                elements.add(jsonArray)
            } else if (it.returnType.classifier == Map::class && it.getter.call(value) != null) {
                val innerMap = it.getter.call(value) as Map<*, *>
                val mapItems = mutableListOf<Any>()
                innerMap.forEach { entry ->
                    val innerObj = JSONObject(entry.key.toString(), entry.value)
                    mapItems.add(innerObj)
                }
                val jsonArray = JSONArray(it.name, mapItems, true)
                elements.add(jsonArray)
            } else {
                if (it.findAnnotation<JSONExcludeItem>() == null) {
                    val leaf = JSONPrimitive(it.name, it.getter.call(value))
                    elements.add(leaf)
                }
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