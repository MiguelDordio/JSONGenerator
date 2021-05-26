package magicJSON

import kotlin.reflect.KClass
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.isSubclassOf

class JSONSerializer {

    /**
     * Serializes an object into JSONObject, JSONArray or JSONPrimitive
     */
    fun identify(value: Any) : MutableMap<String, Element> {
        val elements = mutableMapOf<String, Element>()

        value::class.declaredMemberProperties.forEach {
            // check if the item is to be excluded
            if (it.findAnnotation<JSONExcludeItem>() == null) {
                // check if the item as a custom name
                val fieldName: String = if (it.findAnnotation<JSONCustomField>() != null) {
                    it.findAnnotation<JSONCustomField>()?.name.toString()
                } else { it.name }

                // check if item is a List
                if (it.returnType.classifier == List::class) {
                    var identifiedList: MutableList<Element>? = null
                    if (it.getter.call(value) != null)
                        @Suppress("UNCHECKED_CAST")
                        identifiedList = identifyList(it.getter.call(value) as MutableList<Any>)
                    val jsonArray = JSONArray(identifiedList, false)
                    elements[fieldName] = jsonArray
                }
                // check if item is a Map
                else if (it.returnType.classifier == Map::class) {
                    var identifiedMap: MutableMap<String, Element>? = null
                    if (it.getter.call(value) != null)
                        @Suppress("UNCHECKED_CAST")
                        identifiedMap = identifyMap(it.getter.call(value) as MutableMap<Any, Any>)
                    val jsonArray = JSONArray(identifiedMap, true)
                    elements[fieldName] = jsonArray
                }
                // check if item is an Enum
                else if ((it.returnType.classifier as KClass<out Any>).isSubclassOf(Enum::class)) {
                    val node = JSONPrimitive(it.name, it.getter.call(value).toString())
                    elements[fieldName] = node
                }
                // check if item is basic type (String, Integer, Double, Float, Boolean or Char)
                else if (it.returnType.classifier == String::class || it.returnType.classifier == Int::class ||
                    it.returnType.classifier == Double::class || it.returnType.classifier == Float::class ||
                    it.returnType.classifier == Boolean::class || it.returnType.classifier == Char::class) {
                    val node = JSONPrimitive(it.name, it.getter.call(value))
                    elements[fieldName] = node
                }
                // then the item is another object
                else {
                    val map = it.getter.call(value)?.let { it1 -> identify(it1) }
                    val node = JSONObject(map)
                    elements[fieldName] = node
                }
            }
        }
        return elements
    }

    /**
     * Auxiliary function to serialize lists
     */
    private fun identifyList(rawList: MutableList<Any>) : MutableList<Element> {
        val elementsList = mutableListOf<Element>()

        rawList.forEach {
            // check if item is basic type (String, Integer, Double, Float, Boolean or Char)
            if (it::class == String::class || it::class == Int::class ||
                it::class == Double::class || it::class == Float::class ||
                it::class == Boolean::class || it::class == Char::class) {
                elementsList.add(JSONPrimitive("", it))
            }
            // check if item is an Enum
            else if (it::class.isSubclassOf(Enum::class)) {
                elementsList.add(JSONPrimitive("", it.toString()))
            }
            // then the item is another object
            else {
                val map = identify(it)
                val node = JSONObject(map)
                elementsList.add(node)
            }
        }

        return elementsList
    }

    /**
     * Auxiliary function to serialize maps
     */
    private fun identifyMap(rawMap: MutableMap<Any, Any>) : MutableMap<String, Element> {

        val elementsMap = mutableMapOf<String, Element>()

        (rawMap as LinkedHashMap<*, *>).forEach { (key, value) ->
            // check if item is basic type (String, Integer, Double, Float, Boolean or Char)
            if (value::class == String::class || value::class == Int::class ||
                value::class == Double::class || value::class == Float::class ||
                value::class == Boolean::class || value::class == Char::class) {
                elementsMap[""] = JSONPrimitive("", value)
            }
            // check if item is an Enum
            else if (value::class.isSubclassOf(Enum::class)) {
                elementsMap[""] = JSONPrimitive("", value.toString())
            }
            // then the item is another object
            else {
                // to deal with cases where the map`s key is also an object
                // the object is converted into a simplified String
                if (key::class == String::class) {
                    val map = identify(value)
                    val node = JSONObject(map)
                    elementsMap[key as String] = node
                }else {
                    val map = identify(value)
                    val node = JSONObject(map)
                    elementsMap[key.toString()] = node
                }
            }
        }

        return elementsMap
    }
}