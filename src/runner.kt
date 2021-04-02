import kotlin.reflect.KClass
import kotlin.reflect.full.declaredMemberProperties

fun toJSON(obj: Any) : JSONObject {

    val jsonObject = JSONObject()
    obj::class.declaredMemberProperties.forEach {
        // check if current class field is also an object
        if ((it.returnType.classifier as KClass<*>).annotations.isNotEmpty()) {
            it.getter.call(obj)?.let { it1 ->
                val childObj = toJSON(it1)
                jsonObject.add(it.name.capitalize(), childObj)
            }
        } else if (it.returnType.classifier == List::class) {
            val jsonArray = JSONArray()
            (it.getter.call(obj) as MutableList<*>).forEach { item ->
                val jsonArrayItem = JSONObject()
                if (item!!::class.annotations.isNotEmpty()) {
                    val childObj = toJSON(item)
                    jsonArrayItem.add("", childObj)
                } else
                    jsonArrayItem.add("", item)
                jsonArray.add(jsonArrayItem)
            }
            jsonObject.add(it.name, jsonArray)
        } else
            jsonObject.add(it.name, it.getter.call(obj))
    }

    return jsonObject
}

fun main() {

    val contacts = Contacts("mig@gmail.com", "999888777")
    val relationShipStatus = RelationShipStatus("Married", "09-12-1978")
    val car = Cars("Cloudy", "Biggy", 2008)
    val extras: MutableList<Any> = mutableListOf<Any>(relationShipStatus, car)
    val person = Person("Miguel", 23, "Unicorn", contacts, mutableListOf("Rua da vida", "Aqui não passas"), extras)

    val finalObj = toJSON(person)
    println(finalObj.prettyPrintJSON(finalObj.toString()))
}
