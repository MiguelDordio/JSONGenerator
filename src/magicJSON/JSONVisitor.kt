package magicJSON

/**
 * JSONVisitor implementation that perform operations
 * on CompositeElement(JSONObject/JSONArray) and LeafElement(JSONPrimitive)
 */
interface JSONVisitor {
    fun visitJSONObject(node: JSONObject): Boolean
    fun visitExitJSONObject(): Boolean

    fun visitJSONArray(node: JSONArray, isMap: Boolean): Boolean
    fun visitExitJSONArray(): Boolean

    fun visitJSONPrimitive(node: JSONPrimitive): Boolean
}