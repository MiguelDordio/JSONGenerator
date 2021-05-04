package Testinhos

import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance

annotation class Inject
class Injector {

    companion object {
        fun <T:Any> create(type: KClass<T>) : T {
            return type.createInstance()
        }
    }
}