package magicJSON

import kotlin.reflect.KClass
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.isSubclassOf

class JSONObject(val value: Any? = null) : Element {

    var elements = mutableMapOf<String, Element>()

    private fun identify(value: Any) {
        value::class.declaredMemberProperties.forEach {
            if (it.findAnnotation<JSONCustomField>() != null) {
                val fieldName = it.findAnnotation<JSONCustomField>()?.name
                val node: JSONPrimitive? = if (it.getter.call(value) != null)
                    fieldName?.let { newName -> JSONPrimitive(newName, JSONObject(it.getter.call(value))) }
                else
                    fieldName?.let { newName -> JSONPrimitive(newName, null) }
                if (node != null)
                    elements.put(it.name, node)
            } else if (it.returnType.classifier == List::class && it.getter.call(value) != null) {
                val jsonArray = JSONArray(it.name, it.getter.call(value) as MutableList<Any>, false)
                elements.put(it.name, jsonArray)
            } else if (it.returnType.classifier == Map::class && it.getter.call(value) != null) {
                val innerMap = it.getter.call(value) as Map<*, *>
                val mapItems = mutableListOf<Any>()
                innerMap.forEach { entry ->
                    val innerObj = JSONPrimitive(entry.key.toString(), JSONObject(entry.value))
                    mapItems.add(innerObj)
                }
                val jsonArray = JSONArray(it.name,mapItems, true)
                elements.put(it.name, jsonArray)
            } else if ((it.returnType.classifier as KClass<out Any>).isSubclassOf(Enum::class)) {
                val node = JSONPrimitive(it.name, it.getter.call(value).toString())
                elements.put(it.name, node)
            } else {
                if (it.findAnnotation<JSONExcludeItem>() == null) {
                    if (it.returnType.classifier == String::class || it.returnType.classifier == Int::class ||
                            it.returnType.classifier == Double::class || it.returnType.classifier == Float::class ||
                            it.returnType.classifier == Boolean::class || it.returnType.classifier == Char::class) {
                        val node = JSONPrimitive(it.name, it.getter.call(value))
                        elements.put(it.name, node)
                    } else {
                        val node: JSONPrimitive = if (it.getter.call(value) != null)
                            JSONPrimitive(it.name, JSONObject(it.getter.call(value)))
                        else
                            JSONPrimitive(it.name, null)
                        elements.put(it.name, node)
                    }
                }
            }
        }
    }

    override fun accept(v: JSONVisitor) {
        if (value != null) {
            identify(value)
        }
        if (v.visitJSONObject(this)) {
            elements.forEach {
                it.value.accept(v)
            }
        }
        v.visitExitJSONObject()
    }
}