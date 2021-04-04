package testModels

import JSONAnnotation

@JSONAnnotation
class Machine(
        val name: String,
        val parts: Map<*, Part>
)

@JSONAnnotation
class PartDescriptor(
        val group: String,
        val id: String
) {
    // Cases where the map "key" is an object a toString() is required to proceed
    override fun toString(): String {
        return "$group|$id"
    }
}

@JSONAnnotation
class Part(
        val group: String,
        val id: String,
        val description: String,
        val compat: String
)