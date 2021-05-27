package visualizer

import Inject
import InjectAdd
import magicJSON.*
import org.eclipse.swt.SWT
import org.eclipse.swt.events.SelectionAdapter
import org.eclipse.swt.events.SelectionEvent
import org.eclipse.swt.graphics.Color
import org.eclipse.swt.graphics.Rectangle
import org.eclipse.swt.layout.GridData
import org.eclipse.swt.layout.GridLayout
import org.eclipse.swt.layout.RowLayout
import org.eclipse.swt.widgets.*
import java.io.File


interface VisualFrameSetup {
    val title: String
    val layoutManager: GridLayout
    val width: Int
    val height: Int
    fun applyRules(node: TreeItem, display: Display)
}

interface VisualAction {
    val name: String
    val includeTextBox: Boolean
    var textBoxText: String
    fun execute(window: VisualMapping)
}


class VisualMapping {

    private val shell: Shell = Shell(Display.getDefault())
    private var popup: Shell? = null
    lateinit var tree: Tree
    var highLightedItems = mutableListOf<TreeItem>()
    var selectedItem: TreeItem? = null

    @Inject
    private lateinit var setup: VisualFrameSetup

    @InjectAdd
    private lateinit var actions: MutableList<VisualAction>

    /************************
     * Visual Menu Setup
     *************************/
    private fun setupFrame() {
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
                labelJSON.text = jsonVisitor.treeItemToJSONPrettyPrint(jsonData as Element)
                labelJSON.requestLayout()
                shell.pack()
            }
        })

        tree.addListener(SWT.MouseDoubleClick) {
            callPopUp(shell)
        }

        // depth event
        val label = Label(shell, SWT.NONE)
        label.text = "Nothing selected"

        val button = Button(shell, SWT.PUSH)
        button.text = "depth"
        button.addSelectionListener(object: SelectionAdapter() {
            override fun widgetSelected(e: SelectionEvent) {
                val item = tree.selection.first()
                label.text = item.depth().toString()
            }
        })

        // search event
        val keywordText = Text(shell, SWT.SINGLE or SWT.BORDER)
        keywordText.message = "Search"
        keywordText.addModifyListener {
            val keyword = keywordText.text
            if (highLightedItems.size > 0) {
                highLightedItems.forEach {
                    it.background = Color(Display.getCurrent(), 32, 32, 32)
                }
            }
            if (keyword != "") {
                val root: TreeItem = tree.getItem(0)
                searchTree(root, keyword)
            }
        }
    }

    private fun open() {
        //setupFrame()
        val display = Display.getDefault()
        tree.expandAll()

        // group together all the actions
        val group = Group(shell, SWT.NONE)
        group.text = "Plugin Actions"
        val gridData = GridData(SWT.FILL, SWT.FILL, true, false)
        group.layoutData = gridData
        group.layout = RowLayout(SWT.HORIZONTAL)

        // add to the group all actions as buttons
        actions.forEach { action ->
            var keywordText: Text? = null
            if (action.includeTextBox) {
                keywordText = Text(group, SWT.SINGLE or SWT.BORDER)
            }
            val button = Button(group, SWT.PUSH)
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

    // Receives raw object and serializes it to JSON
    // Opens the visualizer
    fun initializeJSON(obj: Any) {
        setupFrame()
        val jsonSerializer = JSONSerializer()
        val serializedObj = jsonSerializer.identify(obj)
        val jsonVisualTree = JSONVisualTree()
        val rootJsonObject = JSONObject(serializedObj)
        jsonVisualTree.createTree(rootJsonObject, tree)
        open()
    }

    /************************
     * JSON Tree methods
     *************************/
    private fun Tree.expandAll() = traverse { it.expanded = true }

    // Iterates the tree and applies custom icons if the setup provided them
    private fun Tree.traverse(visitor: (TreeItem) -> Unit) {
        fun TreeItem.traverse() {
            visitor(this)
            setup.applyRules(this, display)
            items.forEach {
                it.traverse()
            }
        }
        items.forEach { it.traverse() }
    }

    // find node depth
    fun TreeItem.depth(): Int =
        if(parentItem == null) 0
        else 1 + parentItem.depth()

    // allows the user to search the tree for a given keyword
    private fun searchTree(node: TreeItem, searchText: String) {
        node.items.forEach {
            if (it.data?.toString()?.toUpperCase()?.contains(searchText.toUpperCase()) == true ||
                it.text?.toString()?.toUpperCase()?.contains(searchText.toUpperCase()) == true) {
                it.background = Color(Display.getCurrent(), 0, 0, 255)
                highLightedItems.add(it)
            }
            searchTree(it, searchText)
        }
    }

    // PopUp when user double clicks a tree item
    private fun callPopUp(shell: Shell) {
        if (popup == null) {

            // popUp setup
            val popPupDisplay = Display.getDefault()
            popup = Shell(popPupDisplay)
            popup!!.setSize(250, 200)
            popup!!.layout = GridLayout(2, false)

            // popUp components
            val nameLabel = Label(popup, SWT.NONE)
            val valText = Text(popup, SWT.SINGLE or SWT.BORDER)
            val okBtn = Button(popup, SWT.PUSH)
            okBtn.text = "Apply"

            // get selected tree item data
            selectedItem = tree.selection.first()
            val jsonData = tree.selection.first().data

            // fill popUp components
            if (jsonData is JSONObject) {
                jsonData.elements?.forEach {
                    if (it.value is JSONPrimitive) {
                        nameLabel.text = it.key
                        valText.text = it.value.toString()
                    }
                }
            }

            // button event - apply new text and close popUp
            okBtn.addSelectionListener(object: SelectionAdapter() {
                override fun widgetSelected(e: SelectionEvent) {
                    selectedItem!!.text = valText.text
                    hidePopUp()
                }
            })

            // open new popUp
            popup!!.pack()
            popup!!.open()
            shell.forceFocus()
            while (!popup!!.isDisposed) {
                if (!popPupDisplay.readAndDispatch()) popPupDisplay.sleep()
            }
        }
    }

    private fun hidePopUp() {
        if (popup != null && !popup!!.isDisposed) {
            popup!!.close()
        }
    }

    /************************
     * Actions Supported
     *************************/
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
}