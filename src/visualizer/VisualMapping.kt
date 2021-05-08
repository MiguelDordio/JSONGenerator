package visualizer

import Inject
import InjectAdd
import magicJSON.JSONInspector
import org.eclipse.swt.SWT
import org.eclipse.swt.events.SelectionAdapter
import org.eclipse.swt.events.SelectionEvent
import org.eclipse.swt.graphics.Color
import org.eclipse.swt.graphics.Image
import org.eclipse.swt.graphics.Rectangle
import org.eclipse.swt.layout.FillLayout
import org.eclipse.swt.layout.GridLayout
import org.eclipse.swt.widgets.*
import java.io.File


interface VisualFrameSetup {
    val title: String
    val layoutManager: GridLayout
    val width: Int
    val height: Int
    val folderIconPath: String
    val fileIconPath: String
}

interface VisualAction {
    val name: String
    val includeTextBox: Boolean
    var textBoxText: String
    fun execute(window: VisualMapping)
    fun undo(window: VisualMapping)
}


class VisualMapping {

    private val shell: Shell = Shell(Display.getDefault())
    private var popup: Shell? = null
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

        tree.addListener(SWT.MouseDoubleClick) {
            callPopUp(it, shell)
        }

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
                if (highLightedItem != null) {
                    highLightedItem!!.background = Color(Display.getCurrent(), 32, 32, 32)
                    highLightedItem = null
                }
                if (keyword != "") {
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

    private fun open() {
        //setupFrame()
        val display = Display.getDefault()
        tree.expandAll()

        // add to the frame all actions as buttons
        actions.forEach { action ->
            var keywordText: Text? = null
            if (action.includeTextBox) {
                keywordText = Text(shell, SWT.SINGLE or SWT.BORDER)
            }
            val button = Button(shell, SWT.PUSH)
            button.text = action.name
            button.addSelectionListener(object : SelectionAdapter() {
                override fun widgetSelected(e: SelectionEvent?) {
                    super.widgetSelected(e)
                    if (keywordText != null) {
                        action.textBoxText = keywordText.text
                    }
                    action.execute(this@VisualMapping)
                }
            })
        }
        shell.pack()

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
            if (it.data?.toString()?.toUpperCase()?.contains(searchText.toUpperCase()) == true) {
                it.background = Color(Display.getCurrent(), 0, 0, 255)
                highLightedItem = it
            }
            if (it.text == "(object)")
                searchTree(it, searchText)
        }
    }

    // ------ Tree iterators ------
    fun Tree.expandAll() = traverse { it.expanded = true }

    /**
     * Iterates the tree and applies custom icons if the setup provided them
     */
    fun Tree.traverse(visitor: (TreeItem) -> Unit) {
        items[0].image = Image(display, setup.folderIconPath)
        fun TreeItem.traverse() {
            visitor(this)
            val folderIcon = Image(display, setup.folderIconPath)
            val fileIcon = Image(display, setup.fileIconPath)
            items.forEach {
                if (it.text != "(object)")
                    it.image = fileIcon
                else
                    it.image = folderIcon
                it.traverse()
            }
        }
        items.forEach { it.traverse() }
    }

    private fun callPopUp(e: Event, shell: Shell) {
        if (popup == null) {

            // popUp setup
            val display = Display.getDefault()
            popup = Shell(display)
            popup!!.setSize(250, 200)
            popup!!.layout = FillLayout()

            // popUp components
            val nameLabel = Label(popup, SWT.NONE)
            val valText = Text(popup, SWT.SINGLE or SWT.BORDER)
            val okBtn = Button(popup, SWT.PUSH)
            okBtn.text = "Apply"

            // get selected tree item data
            selectedItem = tree.selection.first()
            val jsonData = tree.selection.first().data

            // fill popUp components
            if (jsonData is String) {
                val cleanedData = jsonData.filterNot { c -> "\"".contains(c)}
                nameLabel.text = cleanedData.substringBefore(":")
                valText.text = cleanedData.substringAfter(":").substringBefore(",")
            }

            // button event - apply new text and close popUp
            okBtn.addSelectionListener(object: SelectionAdapter() {
                override fun widgetSelected(e: SelectionEvent) {
                    val finalText = "\"" + nameLabel.text + "\"" + ":" + "\"" + valText.text + "\","
                    selectedItem!!.text = finalText
                    hidePopUp()
                }
            })

            // open new popUp
            //popup!!.setLocation(shell.location.x + e.x, shell.location.y + e.y)
            popup!!.open()
            shell.forceFocus()
            while (!popup!!.isDisposed) {
                if (!display.readAndDispatch()) display.sleep()
            }
            display.dispose()
        }
    }

    private fun hidePopUp() {
        if (popup != null && !popup!!.isDisposed) {
            popup!!.close()
            popup = null
        }
    }


    // ------ Actions supported ------
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