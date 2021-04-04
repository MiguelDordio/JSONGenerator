package magicJSON

// JSONVisitor implementation that perform operations on CompositeElement and LeafElement
interface JSONVisitor {
    fun visitJSONObject(node: JSONObject): Boolean
    fun visitExitJSONObject(): Boolean

    fun visitJSONArray(node: JSONArray, isMap: Boolean): Boolean
    fun visitExitJSONArray(): Boolean

    fun visitJSONPrimitive(node: JSONPrimitive): Boolean
}