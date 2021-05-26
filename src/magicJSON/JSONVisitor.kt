package magicJSON

/**
 * JSONVisitor implementation that perform operations
 * on CompositeElement(JSONObject/JSONArray) and LeafElement(JSONPrimitive)
 */
interface JSONVisitor {
    fun visitJSONObject(node: JSONObject): Boolean
    fun visitInnerJSONObject(key: String): Boolean
    fun visitExitJSONObject(node: JSONObject): Boolean

    fun visitJSONArray(node: JSONArray, isMap: Boolean): Boolean
    fun visitInnerJSONArray(key: String): Boolean
    fun visitExitJSONArray(node: JSONArray): Boolean

    fun visitJSONPrimitive(node: JSONPrimitive): Boolean
}