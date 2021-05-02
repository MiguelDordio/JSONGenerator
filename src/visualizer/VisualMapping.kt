package visualizer

import expandAll
import magicJSON.JSONInspector
import org.eclipse.swt.SWT
import org.eclipse.swt.events.SelectionAdapter
import org.eclipse.swt.events.SelectionEvent
import org.eclipse.swt.graphics.Color
import org.eclipse.swt.graphics.Rectangle
import org.eclipse.swt.layout.GridLayout
import org.eclipse.swt.widgets.*


class VisualMapping {

    val shell: Shell = Shell(Display.getDefault())
    val tree: Tree
    var hightLightedItem: TreeItem? = null

    init {
        // shell properties
        shell.setSize(650, 600)
        shell.text = "JSON Display"
        shell.layout = GridLayout(2,false)

        tree = Tree(shell, SWT.SINGLE or SWT.BORDER)

        // json label
        val labelJSON = Label(shell, SWT.NONE)

        // handle tree clicks to show json
        tree.addSelectionListener(object : SelectionAdapter() {
            override fun widgetSelected(e: SelectionEvent) {
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
                if (keyword == "" && hightLightedItem != null) {
                    hightLightedItem!!.background = Color(Display.getCurrent(), 32, 32, 32)
                    hightLightedItem = null
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
        val display = Display.getDefault()
        tree.expandAll()
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
     * Search keyword by iterating the tree
     */
    fun searchTree(node: TreeItem, searchText: String) {
        node.items.forEach {
            if (it.data.toString().toUpperCase().contains(searchText.toUpperCase())) {
                it.background = Color(Display.getCurrent(), 0, 0, 255)
                hightLightedItem = it
            }
            if (it.text == "(object)")
                searchTree(it, searchText)
        }
    }
}