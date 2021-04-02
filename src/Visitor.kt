interface Visitor {

    fun JSONTransform(key: String, string: String) : String
    fun JSONTransform(key: String, number: Any) : String
    fun JSONTransform(key: String, boolean: Boolean) : String
}