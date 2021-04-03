package magicJSON

abstract class JSONItem : Element {
    abstract fun generateJSON(): String?
}