package magicJSON

import org.eclipse.swt.SWT
import org.eclipse.swt.widgets.Tree
import org.eclipse.swt.widgets.TreeItem
import kotlin.reflect.KClass
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.isSubclassOf

class JSONInspector : JSONVisitor {

    var jsonText = StringBuilder()
    private var mapPrinting: Boolean = false
    private val allStrings = mutableListOf<String>()
    private var tree: Tree? = null
    private var currentTreeNode: TreeItem? = null


    /**
     * Serializes an object into JSONObject, JSONArray or JSONPrimitive
     */
    private fun identify(value: Any) : MutableMap<String, Element> {
        val elements = mutableMapOf<String, Element>()

        value::class.declaredMemberProperties.forEach {
            // check if the item is to be excluded
            if (it.findAnnotation<JSONExcludeItem>() == null) {
                // check if the item as a custom name
                val fieldName: String = if (it.findAnnotation<JSONCustomField>() != null) {
                    it.findAnnotation<JSONCustomField>()?.name.toString()
                } else { it.name }

                // check if item is a List
                if (it.returnType.classifier == List::class && it.getter.call(value) != null) {
                    @Suppress("UNCHECKED_CAST")
                    val identifiedList = identifyList(it.getter.call(value) as MutableList<Any>)
                    val jsonArray = JSONArray(identifiedList, false)
                    elements[fieldName] = jsonArray
                }
                // check if item is a Map
                else if (it.returnType.classifier == Map::class && it.getter.call(value) != null) {
                    @Suppress("UNCHECKED_CAST")
                    val identifiedMap = identifyMap(it.getter.call(value) as MutableMap<Any, Any>)
                    val jsonArray = JSONArray(identifiedMap, true)
                    elements[fieldName] = jsonArray
                }
                // check if item is an Enum
                else if ((it.returnType.classifier as KClass<out Any>).isSubclassOf(Enum::class)) {
                    val node = JSONPrimitive(it.name, it.getter.call(value).toString())
                    elements[fieldName] = node
                }
                // check if item is basic type (String, Integer, Double, Float, Boolean or Char)
                else if (it.returnType.classifier == String::class || it.returnType.classifier == Int::class ||
                    it.returnType.classifier == Double::class || it.returnType.classifier == Float::class ||
                    it.returnType.classifier == Boolean::class || it.returnType.classifier == Char::class) {
                    val node = JSONPrimitive(it.name, it.getter.call(value))
                    elements[fieldName] = node
                }
                // then the item is another object
                else {
                    val map = it.getter.call(value)?.let { it1 -> identify(it1) }
                    val node = JSONObject(map)
                    elements[it.name] = node
                }
            }
        }
        return elements
    }

    /**
     * Auxiliary function to serialize lists
     */
    private fun identifyList(rawList: MutableList<Any>) : MutableList<Element> {
        val elementsList = mutableListOf<Element>()

        rawList.forEach {
            // check if item is basic type (String, Integer, Double, Float, Boolean or Char)
            if (it::class == String::class || it::class == Int::class ||
                it::class == Double::class || it::class == Float::class ||
                it::class == Boolean::class || it::class == Char::class) {
                elementsList.add(JSONPrimitive("", it))
            }
            // check if item is an Enum
            else if (it::class.isSubclassOf(Enum::class)) {
                elementsList.add(JSONPrimitive("", it.toString()))
            }
            // then the item is another object
            else {
                val map = identify(it)
                val node = JSONObject(map)
                elementsList.add(node)
            }
        }

        return elementsList
    }

    /**
     * Auxiliary function to serialize maps
     */
    private fun identifyMap(rawMap: MutableMap<Any, Any>) : MutableMap<String, Element> {

        val elementsMap = mutableMapOf<String, Element>()

        (rawMap as LinkedHashMap<*, *>).forEach { (key, value) ->
            // check if item is basic type (String, Integer, Double, Float, Boolean or Char)
            if (value::class == String::class || value::class == Int::class ||
                value::class == Double::class || value::class == Float::class ||
                value::class == Boolean::class || value::class == Char::class) {
                elementsMap[""] = JSONPrimitive("", value)
            }
            // check if item is an Enum
            else if (value::class.isSubclassOf(Enum::class)) {
                elementsMap[""] = JSONPrimitive("", value.toString())
            }
            // then the item is another object
            else {
                // to deal with cases where the map`s key is also an object
                // the object is converted into a simplified String
                if (key::class == String::class) {
                    val map = identify(value)
                    val node = JSONObject(map)
                    elementsMap[key as String] = node
                }else {
                    val map = identify(value)
                    val node = JSONObject(map)
                    elementsMap[key.toString()] = node
                }
            }
        }

        return elementsMap
    }


    /***************************************************
     *                 Visitor methods
     ***************************************************/

    /**
     * Visits a JSONObject and creates corresponding json string element
     */
    override fun visitJSONObject(node: JSONObject): Boolean {
        if (node.elements != null)
            jsonText.append("{")
        else
            jsonText.append("\"null\",")
        if (tree != null) {
            val treeNode: TreeItem = if (currentTreeNode == null)
                TreeItem(tree, SWT.NONE)
            else
                TreeItem(currentTreeNode, SWT.NONE)
            treeNode.text = "(object)"
            treeNode.data = node.elements
            currentTreeNode = treeNode
        }
        return true
    }

    /**
     * Visits a JSONObject and creates corresponding json string element
     */
    override fun visitInnerJSONObject(key: String): Boolean {
        jsonText.append("\"" + key.capitalize() + "\"" + ":")
        return true
    }

    /**
     * Marks the exit of a JSONObject and creates corresponding json string element
     */
    override fun visitExitJSONObject(node: JSONObject): Boolean {
        if (node.elements != null) {
            jsonText.setLength(jsonText.length - 1)
            jsonText.append("},")
        }
        if (tree != null)
            currentTreeNode = currentTreeNode!!.parentItem
        return true
    }

    /**
     * Visits a JSONArray and creates corresponding json string element
     */
    override fun visitInnerJSONArray(key: String): Boolean {
        jsonText.append("\"$key\":")
        return true
    }

    /**
     * Visits a JSONArray and creates corresponding json string element
     */
    override fun visitJSONArray(node: JSONArray, isMap: Boolean): Boolean {
        if (node.raw != null)
            if (isMap) {
                jsonText.append("{")
                mapPrinting = true
            } else jsonText.append("[")
        else
            jsonText.append("\"null\",")
        return true
    }

    /**
     * Marks the exit of a JSONObject and creates corresponding json string element
     */
    override fun visitExitJSONArray(): Boolean {
        jsonText.setLength(jsonText.length - 1)
        if (mapPrinting) {
            jsonText.append("},")
            mapPrinting = false
        } else jsonText.append("],")
        return true
    }

    /**
     * Visits a JSONPrimitive and creates corresponding json string element
     */
    override fun visitJSONPrimitive(node: JSONPrimitive): Boolean {
        val sb = StringBuilder()
        if (node.value!= null) {
            when (node.value) {
                is String -> { sb.append(jsonString(node.key, node.value) + ",")
                    allStrings.add(node.value)
                }
                is Boolean -> sb.append(jsonBoolean(node.key, node.value)+ ",")
                is Int -> sb.append(jsonNumber(node.key, node.value)+ ",")
                is Float -> sb.append(jsonNumber(node.key, node.value)+ ",")
                is Double -> sb.append(jsonNumber(node.key, node.value)+ ",")
                is Char -> sb.append(jsonChar(node.key, node.value)+ ",")
                else -> {
                    print("No match found")
                }
            }
        } else
            sb.append(jsonNull(node.key)+ ",")
        jsonText.append(sb.toString())

        // Create a new tree item
        if (tree != null && node.value !is JSONObject) {
            val treeElement = TreeItem(currentTreeNode, SWT.NONE)
            treeElement.text = sb.toString().replaceFirst("\"", "").replaceFirst("\"", "").replaceFirst(",", "")
            treeElement.data = sb.toString()
        }
        return true
    }


    /***************************************************
     *       JSONPrimitive to JSON format methods
     ***************************************************/

    private fun jsonChar(key: String, character: Char): String {
        return if (key != "") "\"$key\":\"$character\"" else "\"$character\""
    }

    private fun jsonString(key: String, string: String): String {
        return if (key != "") "\"$key\":\"$string\"" else "\"$string\""
    }

    private fun jsonNumber(key: String, number: Any): String {
        return "\"$key\":$number"
    }

    private fun jsonBoolean(key: String, boolean: Boolean): String {
        return if (boolean) "\"$key\":\"true\"" else "\"$key\":\"false\""
    }

    private fun jsonNull(key: String): String {
        return "\"$key\":\"null\""
    }


    /***************************************************
     *           Object to JSON format methods
     ***************************************************/

    /**
     * Generates an object corresponding json string
     */
    fun objectToJSON(obj: Any): String {
        val map = identify(obj)
        val node = JSONObject(map)
        node.accept(this)
        jsonText.setLength(jsonText.length - 1)
        return "$jsonText"
    }

    /**
     * Generates an object corresponding json string with a human read format
     */
    fun objectToJSONPrettyPrint(obj: Any): String {
        val map = identify(obj)
        val node = JSONObject(map)
        node.accept(this)
        jsonText.setLength(jsonText.length - 1)
        return prettyPrintJSON("$jsonText")
    }

    /**
     * Takes a raw json string and makes it more readable
     */
    private fun prettyPrintJSON(noWhite: String): String {
        val sb = java.lang.StringBuilder()
        var tabCount = 0
        var inQuotes = false
        for (c in noWhite.toCharArray()) {
            if (c == '"') {
                inQuotes=!inQuotes
            }
            if (!inQuotes) {
                when (c) {
                    '{' -> {
                        sb.append(c)
                        sb.append(System.lineSeparator())
                        tabCount++
                        printTabs(sb, tabCount)
                    }
                    '}' -> {
                        sb.append(System.lineSeparator())
                        tabCount--
                        printTabs(sb, tabCount)
                        sb.append(c)
                    }
                    ',' -> {
                        sb.append(c)
                        sb.append(System.lineSeparator())
                        printTabs(sb, tabCount)
                    }
                    '[' -> {
                        sb.append(c)
                        tabCount++
                    }
                    ']' -> {
                        sb.append(c)
                        tabCount--
                    }
                    else -> {
                        sb.append(c)
                    }
                }
            } else {
                sb.append(c)
            }
        }
        return sb.toString()
    }

    private fun printTabs(sb: java.lang.StringBuilder, tabCount: Int) {
        for (i in 0 until tabCount) {
            sb.append('\t')
        }
    }


    /***************************************************
     *             Extra functionalities
     ***************************************************/

    /**
     * Returns all strings present in a given object
     */
    fun getAllStrings(obj: Any): MutableList<String> {
        val map = identify(obj)
        val node = JSONObject(map)
        node.accept(this)
        return allStrings
    }

    /**
     * Creates a visual menu to display and interact with the objected
     */
    fun openVisualMenu(rawData: Any, frameTree: Tree): Boolean {
        tree = frameTree
        val map = identify(rawData)
        val data = JSONObject(map)
        data.accept(this)
        return true
    }
}