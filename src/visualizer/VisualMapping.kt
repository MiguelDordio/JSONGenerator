package visualizer

import Inject
import InjectAdd
import magicJSON.JSONInspector
import org.eclipse.swt.SWT
import org.eclipse.swt.events.SelectionAdapter
import org.eclipse.swt.events.SelectionEvent
import org.eclipse.swt.graphics.Color
import org.eclipse.swt.graphics.Rectangle
import org.eclipse.swt.layout.GridLayout
import org.eclipse.swt.widgets.*
import java.io.File


interface VisualFrameSetup {
    val title: String
    val layoutManager: GridLayout
    val width: Int
    val height: Int
}

interface VisualAction {
    val name: String
    fun execute(window: VisualMapping)
    fun undo(window: VisualMapping)
}


class VisualMapping {

    private val shell: Shell = Shell(Display.getDefault())
    lateinit var tree: Tree
    var highLightedItem: TreeItem? = null
    var selectedItem: TreeItem? = null

    @Inject
    private lateinit var setup: VisualFrameSetup

    @InjectAdd
    private lateinit var actions: MutableList<VisualAction>

    private var operations = mutableListOf<VisualAction>()

    fun setupFrame() {
        // shell properties
        shell.setSize(setup.width, setup.height)
        shell.text = setup.title
        shell.layout = setup.layoutManager

        tree = Tree(shell, SWT.SINGLE or SWT.BORDER)

        // json label
        val labelJSON = Label(shell, SWT.NONE)

        // handle tree clicks to show json
        tree.addSelectionListener(object : SelectionAdapter() {
            override fun widgetSelected(e: SelectionEvent) {
                selectedItem = tree.selection.first()
                val jsonVisitor = JSONInspector()
                val jsonData = tree.selection.first().data
                if (jsonData is String)
                    labelJSON.text = jsonData
                else
                    labelJSON.text = jsonVisitor.objectToJSONPrettyPrint(jsonData)
                println("selected: " + tree.selection.first().data)
                labelJSON.requestLayout()
            }
        })

        // depth event
        val label = Label(shell, SWT.NONE)
        label.text = "Nothing selected"

        var button = Button(shell, SWT.PUSH)
        button.text = "depth"
        button.addSelectionListener(object: SelectionAdapter() {
            override fun widgetSelected(e: SelectionEvent) {
                val item = tree.selection.first()
                label.text = item.depth().toString()
            }
        })


        // search event
        val keywordText = Text(shell, SWT.SINGLE or SWT.BORDER)
        //keywordText.layoutData = GridData(GridData.FILL_HORIZONTAL)

        button = Button(shell, SWT.PUSH)
        button.text = "Search"
        button.addSelectionListener(object : SelectionAdapter() {
            override fun widgetSelected(e: SelectionEvent) {
                val keyword = keywordText.text
                if (keyword == "" && highLightedItem != null) {
                    highLightedItem!!.background = Color(Display.getCurrent(), 32, 32, 32)
                    highLightedItem = null
                } else {
                    val root: TreeItem = tree.getItem(0)
                    searchTree(root, keyword)
                }
            }
        })
    }

    // find node depth
    fun TreeItem.depth(): Int =
        if(parentItem == null) 0
        else 1 + parentItem.depth()

    fun open() {
        //setupFrame()
        val display = Display.getDefault()
        tree.expandAll()
        shell.pack()

        // add to the frame all actions as buttons
        actions.forEach { action ->
            val button = Button(shell, SWT.PUSH)
            button.text = action.name
            button.addSelectionListener(object : SelectionAdapter() {
                override fun widgetSelected(e: SelectionEvent?) {
                    super.widgetSelected(e)
                    action.execute(this@VisualMapping)
                }
            })
        }

        // center shell
        val primary: Monitor = display.primaryMonitor
        val bounds: Rectangle = primary.bounds
        val rect: Rectangle = shell.bounds
        val x: Int = bounds.x + (bounds.width - rect.width) / 2
        val y: Int = bounds.y + (bounds.height - rect.height) / 2
        shell.setLocation(x, y)
        shell.open()

        while (!shell.isDisposed) {
            if (!display.readAndDispatch()) display.sleep()
        }
        display.dispose()
    }

    /**
     * Receives raw object and serializes it to JSON
     * Opens the visualizer
     */
    fun initializeJSON(obj: Any) {
        setupFrame()
        val jsonVisitor = JSONInspector()
        jsonVisitor.openVisualMenu(obj, tree)
        open()
    }

    /**
     * Search keyword by iterating the tree
     */
    fun searchTree(node: TreeItem, searchText: String) {
        node.items.forEach {
            if (it.data.toString().toUpperCase().contains(searchText.toUpperCase())) {
                it.background = Color(Display.getCurrent(), 0, 0, 255)
                highLightedItem = it
            }
            if (it.text == "(object)")
                searchTree(it, searchText)
        }
    }

    // auxiliares para varrer a árvore

    fun Tree.expandAll() = traverse { it.expanded = true }

    fun Tree.traverse(visitor: (TreeItem) -> Unit) {
        fun TreeItem.traverse() {
            visitor(this)
            items.forEach {
                it.traverse()
            }
        }
        items.forEach { it.traverse() }
    }

    // Actions supported
    fun editObject(name: String) {
        if (selectedItem != null) {
            selectedItem!!.text = name
        }
    }

    fun writeObjectToFile(path: String) {
        val file = File(path)
        val jsonData = tree.selection.first().data
        val jsonVisitor = JSONInspector()
        val jsonFinalText = jsonVisitor.objectToJSONPrettyPrint(jsonData)
        file.bufferedWriter().use { out ->
            out.write(jsonFinalText)
        }
    }

    fun openExternalWindow() {
        open()
    }

    fun undo() {
        if (operations.isNotEmpty()) {
            val lastOp = operations.last()
            //if (lastOp::class != UndoActio::class)
            lastOp.undo(this)
            operations.removeAt(operations.size - 1)
        }
    }
}