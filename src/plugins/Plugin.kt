package plugins

import magicJSON.JSONObject
import magicJSON.JSONPrimitive
import org.eclipse.swt.graphics.Image
import org.eclipse.swt.layout.GridLayout
import org.eclipse.swt.widgets.Display
import visualizer.VisualAction
import visualizer.VisualFrameSetup
import visualizer.VisualMapping

class VisualSetup : VisualFrameSetup {
    override val title: String
        get() = "Json Visual Display"
    override val layoutManager: GridLayout
        get() = GridLayout(2, true)
    override val width: Int
        get() = 650
    override val height: Int
        get() = 600

    override fun applyRules(node: org.eclipse.swt.widgets.TreeItem, display: Display) {
        val folderIcon = Image(display, "FolderIcon.png")
        val fileIcon = Image(display, "FileIcon.png")
        var hasName = false
        var hasChildren = false
        if (node.data is JSONObject) {
            val data = node.data as JSONObject
            data.elements?.forEach {
                if (it.key == "name" && !it.value.isEmpty()) {
                    hasName = true
                    node.text = it.value.toString()
                }

                if (it.key == "children" && !it.value.isEmpty())
                    hasChildren = true
            }
            node.image = folderIcon
            if (hasName && hasChildren) {
                node.items.forEach {
                    if (it.data is JSONPrimitive)
                        it.dispose()
                }
            }else {
                node.image = fileIcon
                node.items.forEach {
                    it.dispose()
                }
            }
        }
    }
}


// ----------- Actions -----------
class ObjectEditor : VisualAction {
    override val name: String
        get() = "Novo nome"
    override val includeTextBox: Boolean
        get() = true
    override var textBoxText: String = ""

    override fun execute(window: VisualMapping) {
        window.editObject(textBoxText)
    }
}

class FileWriter : VisualAction {
    override val name: String
        get() = "Write File"
    override val includeTextBox: Boolean
        get() = false
    override var textBoxText: String = ""
        get() = TODO("Not yet implemented")

    override fun execute(window: VisualMapping) {
        window.writeObjectToFile("/Users/migueloliveira/IdeaProjects/JSONGenerator/src/Testinhos/jsonResults")
    }
}