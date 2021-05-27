package magicJSON

interface Element {
    fun accept(v: JSONVisitor)
    fun isEmpty() : Boolean
}