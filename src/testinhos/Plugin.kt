package testinhos

import org.eclipse.swt.layout.GridLayout
import visualizer.VisualAction
import visualizer.VisualFrameSetup
import visualizer.VisualMapping

class VisualSetup : VisualFrameSetup {
    override val title: String
        get() = "Json Visual Display"
    override val layoutManager: GridLayout
        get() = GridLayout(2, false)
    override val width: Int
        get() = 650
    override val height: Int
        get() = 600
    override val folderIconPath: String
        get() = "folderIcon.png"
    override val fileIconPath: String
        get() = "fileIcon.png"
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
    override fun undo(window: VisualMapping) {
        // TODO
        window.undo()
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
    override fun undo(window: VisualMapping) {
        // TODO
        window.undo()
    }

}

class UndoAction : VisualAction {
    override val name: String
        get() = "undo"
    override val includeTextBox: Boolean
        get() = false
    override var textBoxText: String = ""
        get() = TODO("Not yet implemented")

    override fun execute(window: VisualMapping) {
        window.undo()
    }
    override fun undo(window: VisualMapping) {
        // TODO
        window.undo()
    }
}