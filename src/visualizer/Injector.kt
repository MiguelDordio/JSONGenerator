import visualizer.VisualAction
import java.io.File
import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.jvm.isAccessible

@Target(AnnotationTarget.PROPERTY)
annotation class Inject

@Target(AnnotationTarget.PROPERTY)
annotation class InjectAdd

class Injector {

    companion object {
        val map: MutableMap<String, List<KClass<*>>> = mutableMapOf()

        init {
            val scanner = Scanner(File("/Users/migueloliveira/IdeaProjects/JSONGenerator/src/testinhos/di.properties"))
            while(scanner.hasNextLine()) {
                val line = scanner.nextLine()
                val parts = line.split("=")
                map[parts[0]] = parts[1].split(",").map {
                        print(it)
                        Class.forName(it).kotlin
                    }
            }
            scanner.close()
        }

        fun <T:Any> create(type: KClass<T>) : T {
            val o =  type.createInstance()
            type.declaredMemberProperties.forEach {
                if(it.findAnnotation<Inject>() != null) {
                    it.isAccessible = true
                    val key = type.simpleName + "." + it.name
                    val obj = map[key]!!.first().createInstance()
                    (it as KMutableProperty<*>).setter.call(o, obj)
                }
                else if(it.findAnnotation<InjectAdd>() != null) {
                    it.isAccessible = true
                    val key = type.simpleName + "." + it.name
                    val actions = mutableListOf<VisualAction>()
                    // initialize each action object
                    map[key]?.forEach { action ->
                        actions.add(action.createInstance() as VisualAction)
                    }
                    // fill Plugin`s actions list with actions from the di.properties file
                    (it as KMutableProperty<*>).setter.call(o, actions)
                }
            }
            return o
        }
    }

}