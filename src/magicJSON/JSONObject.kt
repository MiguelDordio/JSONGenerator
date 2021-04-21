package magicJSON

import kotlin.reflect.KClass
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.isSubtypeOf

class JSONObject(val key: String?, val value: Any? = null) : JSONItem() {

    private var elements: MutableList<Element> = mutableListOf()

    private fun identify(value: Any) {
        value::class.declaredMemberProperties.forEach {
            if (it.findAnnotation<JSONCustomField>() != null) {
                val fieldName = it.findAnnotation<JSONCustomField>()?.name
                val node = JSONObject(fieldName, it.getter.call(value))
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
            } else if ((it.returnType.classifier as KClass<out Any>).isSubclassOf(Enum::class)) {
                val node = JSONPrimitive(it.name, it.getter.call(value).toString())
                elements.add(node)
            } else {
                if (it.findAnnotation<JSONExcludeItem>() == null) {
                    if (it.returnType.classifier == String::class || it.returnType.classifier == Int::class ||
                            it.returnType.classifier == Double::class || it.returnType.classifier == Float::class ||
                            it.returnType.classifier == Boolean::class || it.returnType.classifier == Char::class) {
                        val node = JSONPrimitive(it.name, it.getter.call(value))
                        elements.add(node)
                    } else {
                        val node = JSONObject(it.name, it.getter.call(value))
                        elements.add(node)
                    }
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