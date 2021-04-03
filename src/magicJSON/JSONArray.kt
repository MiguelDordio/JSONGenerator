package magicJSON

class JSONArray(val key: String, private val rawList: MutableList<Any>) : JSONItem() {

    val itemsList = mutableListOf<Element>()

    private fun identify() {
        rawList.forEach {
            if(it::class.annotations.isNotEmpty()) {
                val jsonObject = JSONObject("", it)
                itemsList.add(jsonObject)
            } else {
                val jsonPrimitive = JSONPrimitive("", it)
                itemsList.add(jsonPrimitive)
            }
        }
    }

    override fun generateJSON(): String? {
        TODO("Not yet implemented")
    }

    override fun accept(v: JSONVisitor) {
        identify()
        if (v.visitJSONArray(this))
            itemsList.forEach {
                it.accept(v)
                if (it is JSONObject) v.visitExitJSONObject()
            }
    }
}