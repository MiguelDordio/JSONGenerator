package testModels

class Machine(
        val name: String,
        val parts: Map<*, Part>
)

class PartDescriptor(
        val group: String,
        val id: String
) {
    // Cases where the map "key" is an object a toString() is required to proceed
    override fun toString(): String {
        return "$group|$id"
    }
}

class Part(
        val group: String,
        val id: String,
        val description: String,
        val compat: String
)