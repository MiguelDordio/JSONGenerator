import kotlin.reflect.KClass
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.memberProperties

fun toJSON(obj: Any) {

    val jsonObject = JSONObject()
    obj::class.declaredMemberProperties.forEach {
        // check if current class field is also an object
        if(it.returnType::class.objectInstance != null) {
            it.getter.call(obj)?.let { it1 -> toJSON(it1) }
            /*
            val jObject = JSONObject()
            it::class.declaredMemberProperties.forEach { inside ->
                jObject.add(inside.name, inside.getter.call(obj))
            }
            */
        }else
            jsonObject.add(it.name, it.getter.call(obj))
    }

    //println(jsonObject.toString())
    println(jsonObject.prettyPrintJSON(jsonObject.toString()))
}

fun main() {

    val contacts = Contacts("mig@gmail.com", "999888777")
    val person = Person("Miguel", 23, "Unicorn", contacts)
    //val person = Person("Miguel", 23, "Student" /*, mutableListOf<String>("967563224", "mig@gmail.com")*/)

    toJSON(person)
}
