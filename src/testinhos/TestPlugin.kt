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
}


// ----------- Actions -----------
class ObjectEditor : VisualAction {
    override val name: String
        get() = "Novo nome"

    override fun execute(window: VisualMapping) {
        window.editObject(name)
    }

    override fun undo(window: VisualMapping) {
        // TODO
        window.undo()
    }
}

class FileWriter : VisualAction {
    override val name: String
        get() = "Write File"

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

    override fun execute(window: VisualMapping) {
        window.undo()
    }

    override fun undo(window: VisualMapping) {
        // TODO
        window.undo()
    }
}