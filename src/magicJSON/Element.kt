package magicJSON

interface Element {
    fun accept(v: JSONVisitor)
}