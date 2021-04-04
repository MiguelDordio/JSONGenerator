package testModels

import JSONAnnotation

@JSONAnnotation
class Machine(
        val name: String,
        val parts: Map<PartDescriptor, Part>
)

@JSONAnnotation
class PartDescriptor(
        val group: String,
        val id: String
) {
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