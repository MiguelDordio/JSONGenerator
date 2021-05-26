package magicJSON

import org.eclipse.swt.SWT
import org.eclipse.swt.widgets.Tree
import org.eclipse.swt.widgets.TreeItem

class JSONVisualTree: JSONVisitor {

    private lateinit var rootNode: Tree
    private var currentNode: TreeItem? = null

    fun createTree(obj: Element, root: Tree) {
        rootNode = root
        obj.accept(this)
    }

    override fun visitJSONObject(node: JSONObject): Boolean {
        val treeNode: TreeItem = if (currentNode == null) {
            TreeItem(rootNode, SWT.NONE)
        }else
            TreeItem(currentNode, SWT.NONE)

        treeNode.text = "(object)"
        treeNode.data = node
        currentNode = treeNode
        return true
    }

    override fun visitInnerJSONObject(key: String): Boolean {
        return true
    }

    override fun visitExitJSONObject(node: JSONObject): Boolean {
        currentNode = currentNode!!.parentItem
        return true
    }

    override fun visitJSONArray(node: JSONArray, isMap: Boolean): Boolean {

        if (node.raw != null) {

            var parentName = ""

            // obtain array property name
            if (currentNode?.data is JSONObject) {
                val parentData: JSONObject = currentNode?.data as JSONObject
                parentData.elements?.forEach { (key, value) ->
                    if (value is JSONArray)
                        parentName = key
                }
            } else parentName = "(array)"

            val treeNode: TreeItem = if (currentNode == null) {
                TreeItem(rootNode, SWT.NONE)
            }else
                TreeItem(currentNode, SWT.NONE)

            treeNode.text = parentName
            treeNode.data = node
            currentNode = treeNode
        }
        return true
    }

    override fun visitInnerJSONArray(key: String): Boolean {
        return true
    }

    override fun visitExitJSONArray(node: JSONArray): Boolean {
        if (node.raw != null)
            currentNode = currentNode!!.parentItem
        return true
    }

    override fun visitJSONPrimitive(node: JSONPrimitive): Boolean {
        val treeElement = TreeItem(currentNode, SWT.NONE)
        treeElement.text = "${node.key}: ${node.value}"
        treeElement.data = node
        return true
    }
}