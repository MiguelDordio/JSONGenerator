package magicJSON

import org.eclipse.swt.SWT
import org.eclipse.swt.widgets.Tree
import org.eclipse.swt.widgets.TreeItem
import visualizer.VisualMapping
import java.io.File
import java.nio.file.Path

class JSONInspector : JSONVisitor {

    private var jsonText = StringBuilder()
    private var mapPrinting: Boolean = false
    private val allStrings = mutableListOf<String>()
    private var tree: Tree? = null
    private var currentTreeNode: TreeItem? = null

    /**
     * Core methods
     */
    override fun visitJSONObject(node: JSONObject): Boolean {
        jsonText.append("{")

        if (tree != null) {
            val treeNode: TreeItem = if (currentTreeNode == null)
                TreeItem(tree, SWT.NONE)
            else
                TreeItem(currentTreeNode, SWT.NONE)
            treeNode.text = "(object)"
            treeNode.data = node.value
            currentTreeNode = treeNode
        }

        return true
    }

    override fun visitExitJSONObject(): Boolean {
        jsonText.setLength(jsonText.length - 1)
        jsonText.append("},")
        if (tree != null)
            currentTreeNode = currentTreeNode!!.parentItem
        return true
    }

    override fun visitJSONPrimitive(node: JSONPrimitive): Boolean {
        val sb = StringBuilder()
        if (node.value!= null) {
            when (node.value) {
                is String -> { sb.append(node.jsonString(node.key, node.value) + ",")
                    allStrings.add(node.value)
                }
                is Boolean -> sb.append(node.jsonBoolean(node.key, node.value)+ ",")
                is Int -> sb.append(node.jsonNumber(node.key, node.value)+ ",")
                is Float -> sb.append(node.jsonNumber(node.key, node.value)+ ",")
                is Double -> sb.append(node.jsonNumber(node.key, node.value)+ ",")
                is Char -> sb.append(node.jsonChar(node.key, node.value)+ ",")
                is JSONObject -> sb.append(node.jsonObject(node.key, node.value))
                else -> {
                    print("No match found")
                }
            }
        } else
            sb.append(node.jsonNull(node.key)+ ",")
        jsonText.append(sb.toString())

        // Create a new tree item
        if (tree != null && node.value !is JSONObject) {
            val treeElement = TreeItem(currentTreeNode, SWT.NONE)
            treeElement.text = sb.toString().replaceFirst("\"", "").replaceFirst("\"", "").replaceFirst(",", "")
            treeElement.data = sb.toString()
        }
        return true
    }

    override fun visitJSONArray(node: JSONArray, isMap: Boolean): Boolean {
        if (isMap) {
            jsonText.append("\"${node.key}\":{")
            mapPrinting = true
        } else jsonText.append("\"${node.key}\":[")
        return true
    }

    override fun visitExitJSONArray(): Boolean {
        jsonText.setLength(jsonText.length - 1)
        if (mapPrinting) {
            jsonText.append("},")
            mapPrinting = false
        } else jsonText.append("],")
        return true
    }

    fun objectToJSON(obj: Any): String {
        val node = JSONObject(obj)
        node.accept(this)
        jsonText.setLength(jsonText.length - 1)
        return "$jsonText"
    }

    fun objectToJSONPrettyPrint(obj: Any): String {
        val node = JSONObject(obj)
        node.accept(this)
        jsonText.setLength(jsonText.length - 1)
        return prettyPrintJSON("$jsonText")
    }

    private fun prettyPrintJSON(unformattedJsonString: String): String {
        val prettyJSONBuilder = StringBuilder()
        var indentLevel = 0
        var inQuote = false
        var inArray = false
        for (charFromUnformattedJson in unformattedJsonString.toCharArray()) {
            when (charFromUnformattedJson) {
                '"' -> {
                    // switch the quoting status
                    inQuote = !inQuote
                    prettyJSONBuilder.append(charFromUnformattedJson)
                }
                ' ' ->         // For space: ignore the space if it is not being quoted.
                    if (inQuote) {
                        prettyJSONBuilder.append(charFromUnformattedJson)
                    }
                '{' -> {
                    if (inArray) {
                        if (prettyJSONBuilder[prettyJSONBuilder.length - 4] != ',')
                            prettyJSONBuilder.setLength(prettyJSONBuilder.length - 3)
                        prettyJSONBuilder.append(charFromUnformattedJson)
                        indentLevel++
                        appendIndentedNewLine(indentLevel, prettyJSONBuilder)
                    } else {
                        // Starting a new block: increase the indent level
                        prettyJSONBuilder.append(charFromUnformattedJson)
                        indentLevel++
                        appendIndentedNewLine(indentLevel, prettyJSONBuilder)
                    }
                }
                '[' -> {
                    inArray = true
                    // Starting a new block: increase the indent level
                    prettyJSONBuilder.append(charFromUnformattedJson)
                    indentLevel++
                    appendIndentedNewLine(indentLevel, prettyJSONBuilder)
                }
                '}' -> {
                    // Ending a new block; decrese the indent level
                    indentLevel--
                    appendIndentedNewLine(indentLevel, prettyJSONBuilder)
                    prettyJSONBuilder.append(charFromUnformattedJson)
                }
                ']' -> {
                    // Ending a new block; decrese the indent level
                    inArray = false
                    indentLevel--
                    appendIndentedNewLine(indentLevel, prettyJSONBuilder)
                    prettyJSONBuilder.append(charFromUnformattedJson)
                }
                ',' -> {
                    // Ending a json item; create a new line after
                    prettyJSONBuilder.append(charFromUnformattedJson)
                    if (!inQuote) {
                        appendIndentedNewLine(indentLevel, prettyJSONBuilder)
                    }
                }
                ':' -> {
                    prettyJSONBuilder.append("$charFromUnformattedJson ")
                }
                else -> prettyJSONBuilder.append(charFromUnformattedJson)
            }
        }
        return prettyJSONBuilder.toString()
    }

    private fun appendIndentedNewLine(indentLevel: Int, stringBuilder: java.lang.StringBuilder) {
        stringBuilder.append("\n")
        for (i in 0 until indentLevel) {
            // Assuming indention using 2 spaces
            stringBuilder.append("\t")
        }
    }

    /**
     * Misc methods
     */

    fun getAllStrings(obj: Any): MutableList<String> {
        val node = JSONObject(obj)
        node.accept(this)
        return allStrings
    }

    /**
     * Writes the serialized contents to a file.
     * @param path path for the new file.
     */
    fun writeToFile(path: Path) {
        val file = File(path.toString())

        file.bufferedWriter().use { out ->
            out.write("$jsonText")
        }
    }

    /**
     * Visualizer menu
     */
    fun openVisualMenu(rawData: Any): Boolean {
        val visualMenu = VisualMapping()
        tree = visualMenu.tree

        val data = JSONObject(rawData)
        data.accept(this)

        visualMenu.open()
        return true
    }
}